import { writable } from 'svelte/store';

const STORAGE_KEY = 'easyservice_bookings';

function loadSavedBookings() {
  try {
    const raw = localStorage.getItem(STORAGE_KEY);
    const parsed = raw ? JSON.parse(raw) : [];
    return Array.isArray(parsed) ? parsed : [];
  } catch {
    return [];
  }
}

export const userBookings = writable(loadSavedBookings());

userBookings.subscribe((val) => {
  try {
    localStorage.setItem(STORAGE_KEY, JSON.stringify(Array.isArray(val) ? val : []));
  } catch {
    /* ignore quota / private mode */
  }
});

// Keep multiple open tabs/windows in sync without ever clearing existing bookings.
if (typeof window !== 'undefined') {
  window.addEventListener('storage', (event) => {
    if (event.key !== STORAGE_KEY || !event.newValue) return;
    try {
      const parsed = JSON.parse(event.newValue);
      if (Array.isArray(parsed)) userBookings.set(parsed);
    } catch {
      /* ignore malformed storage events */
    }
  });
}

export function addBooking(booking) {
  if (!booking || !booking.id) return;
  userBookings.update(items => {
    const withoutDuplicate = items.filter(item => item.id !== booking.id);
    return [booking, ...withoutDuplicate];
  });
}

export function mergeBookings(bookings) {
  if (!Array.isArray(bookings) || bookings.length === 0) return;
  userBookings.update(items => {
    const byId = new Map(items.map(item => [item.id, item]));
    for (const booking of bookings) {
      if (booking?.id) byId.set(booking.id, { ...byId.get(booking.id), ...booking });
    }
    return [...byId.values()].sort((a, b) => {
      const aTime = new Date(a.createdAt || a.bookingCreatedAt || 0).getTime();
      const bTime = new Date(b.createdAt || b.bookingCreatedAt || 0).getTime();
      return bTime - aTime;
    });
  });
}

export function getCustomerBookings(items, customerId) {
  if (!customerId) return [];
  return (items || []).filter(item => item.customerId === customerId);
}

export function getProviderBookings(items, providerId, providerName = '') {
  if (!providerId && !providerName) return [];
  return (items || []).filter(item =>
    (providerId && item.providerId === providerId) ||
    (providerName && item.providerName === providerName) ||
    (providerName && item.hostName === providerName)
  );
}

export function getBookingOccupiedDates(booking) {
  if (!booking?.startDate) return [];

  const start = new Date(`${booking.startDate}T00:00:00`);
  const end = booking.endDate ? new Date(`${booking.endDate}T00:00:00`) : new Date(start);
  const dates = [];

  // A hotel/car stay occupies each date from check-in/pickup through the day
  // before checkout/return. Single-date services occupy exactly startDate.
  const exclusiveEnd = booking.endDate && end > start ? end : new Date(start.getTime() + 86400000);
  for (let cursor = new Date(start); cursor < exclusiveEnd; cursor.setDate(cursor.getDate() + 1)) {
    dates.push(cursor.toISOString().slice(0, 10));
  }
  return dates;
}

export function isBookingActive(booking) {
  return booking && booking.status === 'CONFIRMED' && booking.providerStatus !== 'DECLINED' && booking.status !== 'CANCELLED';
}

export function getBookedDates(items, listingId, excludeBookingId = null) {
  const dates = new Set();
  for (const booking of items || []) {
    if (booking.listingId !== listingId || booking.id === excludeBookingId || !isBookingActive(booking)) continue;
    getBookingOccupiedDates(booking).forEach(date => dates.add(date));
  }
  return dates;
}

export function isDateRangeBooked(items, listingId, startDate, endDate, excludeBookingId = null) {
  if (!listingId || !startDate) return false;
  const requested = getBookingOccupiedDates({ startDate, endDate });
  const booked = getBookedDates(items, listingId, excludeBookingId);
  return requested.some(date => booked.has(date));
}

export function getListingBookings(items, listingId) {
  return (items || [])
    .filter(item => item.listingId === listingId)
    .sort((a, b) => new Date(b.createdAt || b.bookingCreatedAt || 0) - new Date(a.createdAt || a.bookingCreatedAt || 0));
}

export function updateBookingProviderStatus(bookingId, providerStatus, reason = '') {
  userBookings.update(items => items.map(item => {
    if (item.id !== bookingId) return item;
    return {
      ...item,
      providerStatus,
      status: providerStatus === 'CANCELLED' ? 'CANCELLED' : providerStatus === 'COMPLETED' ? 'COMPLETED' : providerStatus === 'DECLINED' ? 'DECLINED' : item.status,
      providerCancellationReason: providerStatus === 'CANCELLED' || providerStatus === 'DECLINED' ? reason : item.providerCancellationReason,
      providerDecisionAt: new Date().toISOString(),
      providerDecisionMessage: providerStatus === 'ACCEPTED'
        ? 'Your provider accepted this booking.'
        : 'Your provider declined this booking.',
      status: providerStatus === 'DECLINED' ? 'DECLINED' : item.status
    };
  }));
}

export function cancelBookingItem(bookingId, reason = '') {
  let refundAmount = 0;
  userBookings.update(items => items.map(item => {
    if (item.id === bookingId && item.status === 'CONFIRMED') {
      refundAmount = Number(item.totalAmount || 0);
      return { ...item, status: 'CANCELLED', providerStatus: 'CANCELLED', cancellationReason: reason, cancelledBy: 'CUSTOMER', cancelledAt: new Date().toISOString() };
    }
    return item;
  }));
  return refundAmount;
}

export function markBookingCompleted(bookingId) {
  userBookings.update(items => items.map(item => item.id === bookingId && item.status === 'CONFIRMED'
    ? { ...item, status: 'COMPLETED', completedAt: new Date().toISOString() }
    : item));
}

export function addBookingReview(bookingId, rating, reviewText) {
  userBookings.update(items => items.map(item => item.id === bookingId && item.status === 'COMPLETED'
    ? { ...item, rating: Number(rating), reviewText: reviewText.trim(), reviewedAt: new Date().toISOString() }
    : item));
}
