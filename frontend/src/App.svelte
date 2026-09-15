<script>
  import { afterUpdate, onMount } from 'svelte';
  import { Facebook, Instagram, Twitter, Youtube } from 'lucide-svelte';
  import Navbar from './lib/components/Navbar.svelte';
  import ListingCard from './lib/components/ListingCard.svelte';
  import DiscoveredListingCard from './lib/components/DiscoveredListingCard.svelte';
  import DiscoveredListingModal from './lib/components/DiscoveredListingModal.svelte';
  import BookingModal from './lib/components/BookingModal.svelte';
  import BookingDetailsModal from './lib/components/BookingDetailsModal.svelte';
  import LoginModal from './lib/components/LoginModal.svelte';
  import ProviderShowcaseModal from './lib/components/ProviderShowcaseModal.svelte';
  import SpinWheelModal from './lib/components/SpinWheelModal.svelte';
  import EasyToolsModal from './lib/components/EasyToolsModal.svelte';
  import RegisterModal from './lib/components/RegisterModal.svelte';
  import EasyAssistant from './lib/components/EasyAssistant.svelte';
  import SitePage from './lib/components/SitePage.svelte';
  import Icon from './lib/components/Icon.svelte';
  import { fetchListings, mockProviders,discoverListings, fetchCustomerBookings, fetchProviderBookings, updateProviderBookingStatus, cancelBooking } from './lib/api/api.js';
  import { currentUser } from './lib/stores/authStore.js';
  import { userBookings, cancelBookingItem, updateBookingProviderStatus, markBookingCompleted, addBookingReview, mergeBookings, getCustomerBookings, getProviderBookings, getListingBookings, getBookingOccupiedDates, isBookingActive } from './lib/stores/bookingStore.js';
  import addisAbabaSkyline from './assets/addis-ababa-skyline.png';

  let activeTab = 'listings'; // 'listings' | 'history' | 'provider'
  let previousActiveTab = activeTab;
  let profileSubTab = 'BOOKINGS'; // 'BOOKINGS' | 'FAVORITES'
  let selectedCategory = 'ALL';
  let selectedLocation = 'ALL';
  let searchQuery = '';
  let sortBy = 'RECOMMENDED';
  let currentTheme = 'light';
  let currentLanguage = 'en';
  let onlyVerified = false;

  let listings = [];
  let discoveredListings = [];
  let discoveryLoading = false;
  let discoveryError = '';
  let discoveryRequestId = 0;
  let favoriteIds = new Set(['h_prov_1', 'c_prov_1', 's_prov_1']); // Default pre-liked favorites
  let selectedProviderListing = null;
  let listingReturnState = null;
  let selectedDiscoveredListing = null;
  let selectedListing = null;
  let selectedBookingPass = null;
  let showSpinModal = false;
  let showToolsModal = false;
  let showRegisterModal = false;

  // Thematic transition animation trigger
  let transitionCategory = null;
  let isTransitioning = false;

  // Category specific search fields
  let checkInDate = '';
  let checkOutDate = '';
  let guestCount = 2;
  let vehicleType = '4X4';

  // Provider Onboarding & Dashboard State
  let isCompanyRegistered = false;
  let companyName = '';
  let companyCategory = 'HOTEL';
  let companyLocation = 'Addis Ababa';
  let companyPhone = '+251 91 123 4567';

  // Provider Add Listing Modal State
  let showAddListingModal = false;
  let newTitle = '';
  let newType = 'Executive Suite';
  let newPrice = 2500;
  let newCapacity = 10;
  let newImage = 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=80';
  let newDesc = '';
  let amenityWifi = true;
  let amenityParking = true;
  let amenityRestaurant = true;
  let amenityPool = false;
  let amenitySpa = false;
  let amenityGym = false;
  let newCancellationPolicy = 'Free cancellation up to 24 hours before check-in';
  let newCheckIn = '14:00';
  let newCheckOut = '12:00';
  let editingListingId = null;
  let newAccommodationType = 'Hotel';
  let newRoomType = 'Deluxe Room';
  let newVehicleType = 'SUV';
  let newVehicleBrand = 'Toyota';
  let newVehicleModel = 'RAV4';
  let newShopCategory = 'Handicrafts';
  let newEventCategory = 'Festival';
  let newEventDate = '';
  let newEventStartTime = '18:00';
  let newEventEndTime = '22:00';
  let providerPublishedListings = [];
  let providerSuccessMsg = '';
  let providerCancellationReasons = {};
  let sitePage = null;
  let newsletterMessage = '';
  let currency = 'ETB';

  function persistProviderInventory(listingId, quantity, variantId = null) {
    providerPublishedListings = providerPublishedListings.map((listing) =>
      listing.id === listingId
        ? {
            ...listing,
            availableQuantity: Math.max(0, listing.availableQuantity - quantity),
            variants: (listing.variants || []).map((variant) =>
              variant.id === variantId
                ? { ...variant, availableCount: Math.max(0, variant.availableCount - quantity) }
                : variant
            )
          }
        : listing
    );
    localStorage.setItem('easyservice_provider_listings', JSON.stringify(providerPublishedListings));
  }

  async function refreshCustomerListings() {
    const refreshed = await fetchListings();
    const providerIds = new Set(providerPublishedListings.map((listing) => listing.id));
    listings = [
      ...providerPublishedListings,
      ...refreshed.filter((listing) => !providerIds.has(listing.id))
    ];
  }

  $: footerLabels = currentLanguage === 'am'
    ? { providerEyebrow: 'የEasyService ማህበረሰብን ይቀላቀሉ', providerTitle: 'የተረጋገጠ አቅራቢ ይሁኑ', providerDesc: 'ሆቴልዎን፣ መኪናዎን፣ ዝግጅትዎን፣ ልምድዎን ወይም ምርቶችዎን ይዘርዝሩ እና በመላው ኢትዮጵያ ያሉ የተረጋገጡ ደንበኞችን ያግኙ።', verified: 'የተረጋገጠ የገበያ ቦታ', reachCustomers: 'የታመኑ ደንበኞችን ያግኙ', management: 'ቀላል አስተዳደር', managementDesc: 'ለመዘርዘር እና ለማስተዳደር ቀላል መሳሪያዎች', securePayments: 'የተጠበቁ ክፍያዎች', paymentsDesc: 'የማሳያ ክፍያዎች እና ክፍያ መቀበያዎች', growth: 'የደንበኛ እድገት', growthDesc: 'ንግድዎን በየቀኑ ያሳድጉ', become: 'አቅራቢ ይሁኑ', how: 'እንዴት እንደሚሰራ', free: 'መቀላቀል ነፃ ነው እና መጀመር ቀላል ነው።', activeProviders: 'ንቁ አቅራቢዎች', growing: 'በመላው ኢትዮጵያ አብረን እያደግን ነው', description: 'የኢትዮጵያ የታመነ የመኖሪያ፣ የመጓጓዣ፣ የልምድ፣ የዝግጅት እና የእውነተኛ ምርቶች የገበያ ቦታ።', discover: 'ያግኙ', customers: 'ለደንበኞች', providers: 'ለአቅራቢዎች', destinations: 'መዳረሻዎች', company: 'ድርጅት', explore: 'መርምር', stays: 'መኖሪያዎች', drive: 'መኪና', experiences: 'ልምዶች', shop: 'ግዢ', bookings: 'የእኔ ቦታ ማስያዣዎች', passport: 'የፓስፖርት መገለጫ', wallet: 'Easy የኪስ ቦርሳ', help: 'እርዳታ እና ድጋፍ', providerHub: 'የአቅራቢ ማዕከል', listService: 'አገልግሎት ይዘርዝሩ', support: 'የአቅራቢ ድጋፍ', about: 'ስለ EasyService', trust: 'እምነት እና ደህንነት', terms: 'ውሎች', privacy: 'ግላዊነት', stayConnected: 'ግንኙነታችሁን ይቀጥሉ', deals: 'ምርጥ ቅናሾችን እና የአካባቢ ግኝቶችን ያግኙ።', email: 'ኢሜይልዎን ያስገቡ', subscribe: 'ይመዝገቡ', app: 'መተግበሪያችንን ያውርዱ', secure: 'የተረጋገጠ እና የተጠበቀ', secureDesc: 'ሁሉም አቅራቢዎች ለደህንነትዎ ተረጋግጠዋል', support24: '24/7 ድጋፍ', supportDesc: 'እርዳታ በሚፈልጉበት ጊዜ ሁሉ እዚህ ነን', made: 'በኢትዮጵያ የተሰራ', discovery: 'የህዝብ ምንጭ ፍለጋ', discovered: 'የተገኙ ንግዶች', found: 'ተገኝተዋል', searching: 'በመፈለግ ላይ', discoveryDesc: 'ከህዝብ ምንጮች የተገኙ ንግዶች። እነዚህ የEasyService አቅራቢዎች አይደሉም እና በEasyService ሊያዙ አይችሉም።', allBusinesses: 'ሁሉም የተረጋገጡ ንግዶች', verifiedListed: 'የተረጋገጡ አቅራቢዎች ተዘርዝረዋል', sortBy: 'ደርድር በ', recommended: 'የሚመከር', priceLow: 'ዋጋ፡ ከዝቅተኛ ወደ ከፍተኛ', priceHigh: 'ዋጋ፡ ከፍተኛ ወደ ዝቅተኛ', reset: 'ሁሉንም ማጣሪያዎች ዳግም አስጀምር' }
    : { providerEyebrow: 'JOIN THE EASYSERVICE COMMUNITY', providerTitle: 'Become a Verified Provider', providerDesc: 'List your hotel, vehicle, event, experience, or products and reach thousands of verified customers across Ethiopia.', verified: 'Verified Marketplace', reachCustomers: 'Reach trusted customers', management: 'Easy Management', managementDesc: 'Simple tools to list and manage', securePayments: 'Secure Payments', paymentsDesc: 'Simulated payments and payouts', growth: 'Customer Growth', growthDesc: 'Grow your business every day', become: 'Become a Provider', how: 'How It Works', free: "It's free to join and easy to get started.", activeProviders: 'Active Providers', growing: 'Growing together across Ethiopia', description: "Ethiopia's trusted marketplace for stays, transportation, experiences, events, and authentic products.", discover: 'Discover', customers: 'For Customers', providers: 'For Providers', destinations: 'Destinations', company: 'Company', explore: 'Explore', stays: 'Stays', drive: 'Drive', experiences: 'Experiences', shop: 'Shop', bookings: 'My Bookings', passport: 'Passport Profile', wallet: 'Easy Wallet', help: 'Help & Support', providerHub: 'Provider Hub', listService: 'List a Service', support: 'Provider Support', about: 'About EasyService', trust: 'Trust & Safety', terms: 'Terms', privacy: 'Privacy', stayConnected: 'Stay Connected', deals: 'Get the best deals and local discoveries.', email: 'Enter your email', subscribe: 'Subscribe', app: 'Download our app', secure: 'Verified & Secure', secureDesc: 'All providers are verified for your safety and trust', support24: '24/7 Support', supportDesc: "We're here anytime you need help", made: 'Made in Ethiopia', discovery: 'PUBLIC SOURCE DISCOVERY', discovered: 'Discovered Businesses', found: 'found', searching: 'searching', discoveryDesc: 'Businesses found from public sources. These are not EasyService providers and are not bookable through EasyService.', allBusinesses: 'All Verified Businesses', verifiedListed: 'verified business providers listed', sortBy: 'Sort by', recommended: 'Recommended', priceLow: 'Price: Low to High', priceHigh: 'Price: High to Low', reset: 'Reset All Filters' };

  const accommodationTypes = ['Hotel', 'Guesthouse', 'Resort', 'Lodge', 'Villa', 'Hostel'];
  const roomTypes = ['Single Room', 'Double Room', 'Twin Room', 'Standard Room', 'Deluxe Room', 'Superior Room', 'Family Room', 'Executive Room', 'Studio', 'Apartment', 'Junior Suite', 'Executive Suite', 'Presidential Suite', 'Villa', 'Bungalow', 'Cottage', 'Guesthouse Room', 'Dormitory Bed', 'Entire Guesthouse', 'Resort Villa'];
  const vehicleTypes = ['Sedan', 'SUV', 'Crossover', 'Hatchback', 'Coupe', 'Sports Car', 'Luxury Car', 'Convertible', 'Pickup', 'Van', 'Minivan', 'Bus', '4×4 / Off-road', 'EV', 'Hybrid', 'Limousine', 'Commercial Vehicle'];
  const vehicleBrands = ['Toyota', 'Hyundai', 'Kia', 'Mercedes-Benz', 'BMW', 'Audi', 'Volkswagen', 'Ford', 'Nissan', 'Honda', 'Suzuki', 'Mitsubishi', 'Land Rover', 'Jeep', 'Isuzu'];
  const vehicleModels = {
    Toyota: ['Land Cruiser', 'Land Cruiser Prado', 'Hilux', 'RAV4', 'Corolla', 'Fortuner'],
    Hyundai: ['Tucson', 'Santa Fe', 'Elantra', 'Staria', 'i10'],
    Kia: ['Sportage', 'Sorento', 'Picanto', 'Carnival'],
    'Mercedes-Benz': ['E-Class', 'S-Class', 'V-Class', 'Sprinter'],
    BMW: ['3 Series', '5 Series', 'X3', 'X5'],
    Audi: ['A4', 'A6', 'Q5', 'Q7'],
    Volkswagen: ['Golf', 'Tiguan', 'Transporter'],
    Ford: ['Ranger', 'Everest', 'Transit'],
    Nissan: ['Patrol', 'X-Trail', 'Navara'],
    Honda: ['CR-V', 'Civic', 'Accord'],
    Suzuki: ['Dzire', 'Swift', 'Jimny'],
    Mitsubishi: ['Pajero', 'Outlander', 'L200'],
    'Land Rover': ['Defender', 'Discovery', 'Range Rover'],
    Jeep: ['Wrangler', 'Grand Cherokee', 'Compass'],
    Isuzu: ['D-Max', 'MU-X', 'N-Series']
  };
  const shopCategories = ['Fashion', 'Electronics', 'Beauty', 'Jewelry', 'Home & Furniture', 'Food & Grocery', 'Handicrafts', 'Art', 'Books & Stationery', 'Sports', 'Health & Wellness', 'Automotive', 'Gifts', 'Other'];
  const eventCategories = ['Music', 'Concert', 'Jazz', 'Festival', 'Cultural', 'Sports', 'Business', 'Conference', 'Workshop', 'Exhibition', 'Food & Drink', 'Family', 'Entertainment', 'Community', 'Religious', 'Other'];
  $: availableVehicleModels = vehicleModels[newVehicleBrand] || [];
  $: if (!availableVehicleModels.includes(newVehicleModel)) newVehicleModel = availableVehicleModels[0] || '';

  function restoreProviderProfile(userId) {
    try {
      const raw = localStorage.getItem(`easyservice_provider_profile_${userId}`);
      if (!raw) {
        companyName = '';
        companyCategory = 'HOTEL';
        companyLocation = 'Addis Ababa';
        companyPhone = '+251 91 123 4567';
        isCompanyRegistered = false;
        return false;
      }
      const profile = JSON.parse(raw);
      companyName = profile.companyName || '';
      companyCategory = profile.companyCategory || 'HOTEL';
      companyLocation = profile.companyLocation || 'Addis Ababa';
      companyPhone = profile.companyPhone || '+251 91 123 4567';
      isCompanyRegistered = Boolean(profile.companyName);
      return isCompanyRegistered;
    } catch {
      companyName = '';
      companyCategory = 'HOTEL';
      companyLocation = 'Addis Ababa';
      companyPhone = '+251 91 123 4567';
      isCompanyRegistered = false;
      return false;
    }
  }

  afterUpdate(() => {
    if (activeTab !== previousActiveTab) {
      previousActiveTab = activeTab;
      window.scrollTo({ top: 0, left: 0, behavior: 'auto' });
    }
  });

  function loadProviderListings() {
    try {
      const saved = localStorage.getItem('easyservice_provider_listings');
      return saved ? JSON.parse(saved) : [];
    } catch {
      return [];
    }
  }

  // Context-aware Hero Atmosphere Configurations
  const categoryAtmospheres = {
    ALL: {
      tag: 'ETHIOPIA MARKETPLACE',
      headline: 'One place for everything worth experiencing in Ethiopia.',
      subtitle: 'Discover verified luxury resort stays, 4×4 rentals, cultural jazz summits, and authentic Ethiopian crafts.',
      bgImg: addisAbabaSkyline,
      accentColor: 'var(--accent-gold)'
    },
    HOTEL: {
      tag: 'STAYS • 7 VERIFIED HOTELS & RESORTS',
      headline: 'Stay somewhere unforgettable in Ethiopia.',
      subtitle: 'Browse 7 top hotel providers: Kuriftu, Skylight, Haile Resort, Sheraton, Radisson Blu, Lalibela Lodge & Grand Hotel.',
      bgImg: 'https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1600&q=80',
      accentColor: '#C89B3C'
    },
    CAR_RENTAL: {
      tag: 'DRIVE • 7 VERIFIED CAR FLEETS',
      headline: 'Go further across Ethiopia.',
      subtitle: 'Rent 4×4 Land Cruisers, VIP sedans, and safari vehicles from 7 trusted Ethiopian car rental providers.',
      bgImg: 'https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&w=1600&q=80',
      accentColor: '#3B82F6'
    },
    EVENT: {
      tag: 'EXPERIENCES • 7 VERIFIED EVENT ORGANIZERS',
      headline: "What's happening in Ethiopia?",
      subtitle: 'Book passes for African Jazz Summit, Great Ethiopian Run, Timkat festival & cultural fairs.',
      bgImg: 'https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=1600&q=80',
      accentColor: '#E11D48'
    },
    STORE: {
      tag: 'SHOP • 7 ETHIOPIAN ARTISAN SHOPS',
      headline: 'Bring Ethiopia home.',
      subtitle: 'Shop Yirgacheffe coffee, handwoven habesha kemis dresses, silver jewelry & genuine leather goods.',
      bgImg: 'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?auto=format&fit=crop&w=1600&q=80',
      accentColor: '#10B981'
    }
  };

  const ethiopianLocations = [
    { id: 'ALL', name: 'All Ethiopia', icon: 'globe' },
    { id: 'Addis', name: 'Addis Ababa', icon: 'city' },
    { id: 'Bishoftu', name: 'Bishoftu Lakefront', icon: 'wave' },
    { id: 'Hawassa', name: 'Lake Hawassa', icon: 'wave' },
    { id: 'Lalibela', name: 'Lalibela Highlands', icon: 'mountain' },
    { id: 'Bahir Dar', name: 'Bahir Dar & Tana', icon: 'boat' }
  ];

  const amharicListingNames = {
    h_prov_1: 'ኩሪፍቱ ሪዞርት እና ስፓ ቢሾፍቱ',
    h_prov_2: 'ኢትዮጵያ ስካይላይት ሆቴል',
    h_prov_3: 'ሃይሌ ሪዞርት ሐዋሳ',
    h_prov_4: 'ሸራተን አዲስ የቅንጦት ሆቴል',
    h_prov_5: 'ራዲሰን ብሉ አዲስ ሆቴል',
    h_prov_6: 'ላሊበላ ተራራ እይታ ሎጅ',
    h_prov_7: 'ግራንድ ሆቴል እና ሪዞርት ባሕር ዳር',
    c_prov_1: 'ከበደ 4×4 የአስቸጋሪ መንገድ ኪራይ',
    c_prov_2: 'የአዲስ የቅንጦት መኪና መርከብ',
    c_prov_3: 'ኢትዮ ድራይቭ ሳፋሪ ኪራይ',
    c_prov_4: 'ቦሌ ኤክስፕረስ የአየር ማረፊያ መጓጓዣ',
    c_prov_5: 'ሪፍት ቫሊ 4×4 የጉዞ መኪና',
    c_prov_6: 'ሃይላንድ ሞተርስ ኢትዮጵያ',
    c_prov_7: 'ሸገር የመኪና ኪራይ',
    e_prov_1: 'የአፍሪካ ጃዝ ሰሚት ትኬቶች',
    e_prov_2: 'የታላቁ ኢትዮጵያ ሩጫ ትኬቶች',
    e_prov_3: 'የኢትዮጵያ ባህላዊ ፌስቲቫል',
    e_prov_4: 'የቦሌ የቀጥታ ሙዚቃ ኮንሰርቶች',
    e_prov_5: 'የእንጦጦ ፓርክ ባህላዊ ትርዒት',
    e_prov_6: 'የላሊበላ ጥምቀት ፌስቲቫል',
    e_prov_7: 'የሐዋሳ ሐይቅ የውሃ ስፖርት ፌስቲቫል',
    s_prov_1: 'የይርጋጨፌ ቡና አርቲዛኖች',
    s_prov_2: 'የሐበሻ ቅርስ ቀሚስ ሱቅ',
    s_prov_3: 'የላሊበላ ባህላዊ ብር ዕቃዎች',
    s_prov_4: 'የአዲስ እውነተኛ የቆዳ ዕቃዎች',
    s_prov_5: 'የእንጦጦ ቅመማ ቅመም እና ጤፍ ሱቅ',
    s_prov_6: 'የቢሾፍቱ በእጅ የተሰራ ሸክላ',
    s_prov_7: 'የአክሱም ባህላዊ ጥንታዊ የእጅ ሥራዎች'
  };

  const amharicCategoryNames = { HOTEL: 'መኖሪያ', CAR_RENTAL: 'መኪና', EVENT: 'ልምድ', STORE: 'ግዢ' };
  const amharicLocations = { 'Addis Ababa': 'አዲስ አበባ', Bishoftu: 'ቢሾፍቱ', Hawassa: 'ሐዋሳ', Lalibela: 'ላሊበላ', 'Bahir Dar': 'ባሕር ዳር' };

  function localizeListing(listing) {
    if (currentLanguage !== 'am') return listing;
    const category = amharicCategoryNames[listing.category] || 'አገልግሎት';
    const location = Object.entries(amharicLocations).reduce((value, [english, amharic]) => value.replaceAll(english, amharic), listing.location || 'ኢትዮጵያ');
    return {
      ...listing,
      title: amharicListingNames[listing.id] || `${category} አገልግሎት`,
      description: `${location} ውስጥ የሚገኝ የታመነ ${category} አገልግሎት። ዝርዝሮችን፣ ዋጋን እና መገኘትን ይመልከቱ።`,
      location,
      hostName: amharicListingNames[listing.id] || listing.hostName,
      categoryLabel: category,
      unitLabel: listing.category === 'HOTEL' ? '/ ሌሊት' : listing.category === 'CAR_RENTAL' ? '/ ቀን' : listing.category === 'EVENT' ? '/ ትኬት' : '/ ዕቃ'
    };
  }

  function getBackendProviderId(userId) {
    const providerMap = { user4: 'prov_1', user6: 'prov_2', user8: 'prov_3', user10: 'prov_4' };
    return providerMap[userId] || userId;
  }

  async function syncBackendBookings(user) {
    if (!user?.id) return;
    try {
      const requests = [fetchCustomerBookings(user.id)];
      if (user.role === 'PROVIDER') requests.push(fetchProviderBookings(getBackendProviderId(user.id)));
      const results = await Promise.all(requests);
      const backendBookings = results.flat().map(tx => {
        const listing = listings.find(l => l.id === tx.listingId) || {};
        return {
          id: tx.id,
          customerId: tx.customerId,
          customerName: tx.customerName,
          customerEmail: tx.customerEmail,
          customerPhone: tx.customerPhone,
          listingId: tx.listingId,
          listingTitle: listing.title || tx.listingId,
          category: listing.category,
          location: listing.location || 'Addis Ababa',
          hostName: listing.hostName || 'EasyService Provider',
          imageUrl: listing.imageUrl,
          providerId: tx.providerId || listing.providerId,
          providerName: listing.hostName,
          quantity: tx.quantity,
          totalAmount: Number(tx.totalAmount || 0),
          status: tx.status,
          providerStatus: tx.providerStatus || 'PENDING',
          startDate: tx.startDate,
          endDate: tx.endDate,
          pickupTime: tx.pickupTime,
          returnTime: tx.returnTime,
          driverOption: tx.driverOption,
          bookingDate: tx.createdAt ? new Date(tx.createdAt).toLocaleDateString('en-US', { month: 'short', day: 'numeric', year: 'numeric' }) : '',
          createdAt: tx.createdAt
        };
      });
      mergeBookings(backendBookings);
    } catch (err) {
      // LocalStorage remains the immediate source for the demo when the API is offline.
      console.warn('Could not sync bookings from backend:', err);
    }
  }

  onMount(async () => {
    const savedTheme = localStorage.getItem('easyservice_theme') || 'light';
    const savedLanguage = localStorage.getItem('easyservice_language') || 'en';
    const savedCurrency = localStorage.getItem('easyservice_currency') || 'ETB';
    currentTheme = savedTheme;
    currentLanguage = savedLanguage;
    currency = savedCurrency;
    document.documentElement.setAttribute('data-theme', savedTheme);
    if ($currentUser) restoreProviderProfile($currentUser.id);

    const savedFavs = localStorage.getItem('easyservice_favorites');
    if (savedFavs) {
      try {
        favoriteIds = new Set(JSON.parse(savedFavs));
      } catch {
        /* keep defaults */
      }
    }
    providerPublishedListings = loadProviderListings();
    const localCatalog = [...providerPublishedListings, ...mockProviders];
    listings = localCatalog;

    handleHashNavigation();
    void handleDiscoverySearch();

    // Show the marketplace immediately, then overlay live backend data.
    const data = await fetchListings();
    const catalog = data && data.length > 0 ? data : mockProviders;
    const savedIds = new Set(providerPublishedListings.map((listing) => listing.id));
    listings = [...providerPublishedListings, ...catalog.filter((listing) => !savedIds.has(listing.id))];
    await syncBackendBookings($currentUser);

    const handleBrowserBack = () => {
      selectedProviderListing = null;
      handleHashNavigation();
      if (listingReturnState) {
        const returnState = listingReturnState;
        listingReturnState = null;
        selectedCategory = returnState.category || selectedCategory;
        selectedLocation = returnState.location || selectedLocation;
        searchQuery = returnState.search || searchQuery;
        requestAnimationFrame(() => window.scrollTo({ top: 0, left: 0, behavior: 'auto' }));
        setTimeout(() => window.scrollTo({ top: returnState.scrollY, left: 0, behavior: 'auto' }), 0);
      } else {
        window.scrollTo({ top: 0, left: 0, behavior: 'auto' });
      }
    };

    window.addEventListener('hashchange', handleHashNavigation);
    window.addEventListener('popstate', handleBrowserBack);

    return () => {
      window.removeEventListener('hashchange', handleHashNavigation);
      window.removeEventListener('popstate', handleBrowserBack);
    };
  });

  function handleHashNavigation() {
    const hash = window.location.hash.replace(/^#/, '');
    const sitePages = ['explore', 'stays', 'drive', 'experiences', 'shop', 'bookings', 'passport', 'wallet', 'help', 'provider-support', 'become-provider', 'provider', 'list-service', 'addis', 'bishoftu', 'hawassa', 'lalibela', 'bahir-dar', 'about', 'how-it-works', 'trust', 'terms', 'privacy'];
    const hashCategories = { explore: 'ALL', stays: 'HOTEL', drive: 'CAR_RENTAL', experiences: 'EVENT', shop: 'STORE' };
    if (hash.startsWith('listing-')) {
      const listingId = hash.slice('listing-'.length);
      const listing = listings.find((item) => item.id === listingId);
      if (listing) {
        selectedProviderListing = localizeListing(listing);
        sitePage = null;
        activeTab = 'listings';
      }
      window.scrollTo({ top: 0, left: 0, behavior: 'auto' });
      return;
    }
    if (!hash) {
      selectedProviderListing = null;
      sitePage = null;
      activeTab = 'listings';
      selectedCategory = 'ALL';
      window.scrollTo({ top: 0, left: 0, behavior: 'auto' });
      return;
    }
    if (hashCategories[hash]) {
      sitePage = null;
      activeTab = 'listings';
      handleSelectCategory(hashCategories[hash]);
      selectedProviderListing = null;
      window.scrollTo({ top: 0, left: 0, behavior: 'auto' });
      return;
    }
    if (hash === 'bookings') {
      sitePage = null;
      activeTab = 'history';
      window.scrollTo({ top: 0, left: 0, behavior: 'auto' });
      return;
    }
    if (hash === 'provider' || hash === 'list-service') {
      sitePage = null;
      activeTab = 'provider';
      if (hash === 'list-service') showAddListingModal = true;
      window.scrollTo({ top: 0, left: 0, behavior: 'auto' });
      return;
    }
    if (sitePages.includes(hash)) {
      sitePage = hash;
      activeTab = 'listings';
      window.scrollTo({ top: 0, left: 0, behavior: 'auto' });
    }
  }

  function navigateToSitePage(page) {
    const legacyCategoryPages = { explore: 'ALL', stays: 'HOTEL', drive: 'CAR_RENTAL', experiences: 'EVENT', shop: 'STORE' };
    if (legacyCategoryPages[page]) {
      sitePage = null;
      activeTab = 'listings';
      handleSelectCategory(legacyCategoryPages[page]);
      window.history.pushState({}, '', `#${page}`);
      window.scrollTo({ top: 0, left: 0, behavior: 'auto' });
      return;
    }
    if (page === 'my-bookings' || page === 'bookings') page = 'bookings';
    if (page === 'bookings') {
      sitePage = null;
      activeTab = 'history';
    } else if (page === 'provider' || page === 'list-service') {
      sitePage = null;
      activeTab = 'provider';
      if (page === 'list-service') showAddListingModal = true;
    } else {
      sitePage = page;
      activeTab = 'listings';
    }
    window.history.pushState({}, '', `#${page}`);
    window.scrollTo({ top: 0, left: 0, behavior: 'auto' });
  }

  function handleAssistantNavigation(target) {
    const categoryTargets = { HOTEL: 'HOTEL', CAR_RENTAL: 'CAR_RENTAL', EVENT: 'EVENT' };
    if (categoryTargets[target]) {
      goToCategory(categoryTargets[target]);
      return;
    }
    navigateToSitePage(target);
  }

  function handleWalletTopUp(amount) {
    currentUser.update((user) => user ? { ...user, balance: Number(user.balance || 0) + amount } : user);
  }

  function handleNewsletterSubmit(event) {
    const email = String(new FormData(event.currentTarget).get('email') || '').trim().toLowerCase();
    if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      newsletterMessage = currentLanguage === 'am' ? 'እባክዎ ትክክለኛ ኢሜይል ያስገቡ።' : 'Please enter a valid email address.';
      return;
    }
    const subscribers = JSON.parse(localStorage.getItem('easyservice_newsletter_subscribers') || '[]');
    if (subscribers.includes(email)) {
      newsletterMessage = currentLanguage === 'am' ? 'አስቀድመው በዝርዝሩ ውስጥ ነዎት።' : "You're already on the list.";
      return;
    }
    localStorage.setItem('easyservice_newsletter_subscribers', JSON.stringify([...subscribers, email]));
    newsletterMessage = currentLanguage === 'am' ? 'ተመዝግበዋል!' : "You're subscribed!";
  }

  function toggleTheme() {
    currentTheme = currentTheme === 'light' ? 'dark' : 'light';
    localStorage.setItem('easyservice_theme', currentTheme);
    document.documentElement.setAttribute('data-theme', currentTheme);
  }

  function toggleLanguage() {
    currentLanguage = currentLanguage === 'en' ? 'am' : 'en';
    localStorage.setItem('easyservice_language', currentLanguage);
  }

  async function handleDiscoverySearch(categoryOverride = selectedCategory) {
    const requestId = ++discoveryRequestId;
    discoveryLoading = true;
    discoveryError = '';

    try {
      const city = selectedLocation === 'Addis'
        ? 'Addis Ababa'
        : selectedLocation === 'ALL'
          ? 'Addis Ababa'
          : selectedLocation;

      const area = 'ALL';

      const category = categoryOverride === 'CAR_RENTAL'
        ? 'CAR'
        : categoryOverride;

      const categories = category === 'ALL'
        ? ['HOTEL', 'CAR', 'STORE', 'EVENT']
        : [category];
      const resultSets = await Promise.allSettled(
        categories.map((discoveryCategory) =>
          discoverListings(city, area, discoveryCategory))
      );
      const successfulResults = resultSets
        .filter((result) => result.status === 'fulfilled')
        .flatMap((result) => result.value);
      const failedResults = resultSets.filter((result) => result.status === 'rejected');
      const results = successfulResults;

      if (requestId !== discoveryRequestId) return;

      discoveredListings = Array.from(
        new Map(results.map((item) => [item.id, item])).values()
      ).map((item) => ({
        id: item.id,
        name: item.name,
        title: item.name,
        category: item.category === 'CAR' ? 'CAR_RENTAL' : item.category,
        description: item.description || `${item.name} in ${item.city || city}.`,
        price: item.price ? Number(item.price) : 0,
        capacity: 1,
        availableQuantity: 1,
        status: 'DISCOVERED',
        location: item.city || city,
        area: item.area || '',
        hostName: item.sourceName || item.name,
        imageUrl: item.imageUrl || null,
        imageUrls: item.imageUrls || [],
        sourceType: item.sourceType,
        sourceName: item.sourceName,
        sourceUrl: item.sourceUrl,
        website: item.website,
        phone: item.phone,
        priceSource: item.priceSource,
        currency: item.currency,
        lastChecked: item.lastChecked,
        tags: item.tags || [],
        variants: []
      }));
      discoveryError = results.length === 0 && failedResults.length === resultSets.length
        ? 'Discovery service is unavailable. Start easyservice-discovery on port 8090.'
        : '';

      console.log('Marketplace discovery listings:', discoveredListings);
    } catch (error) {
      if (requestId === discoveryRequestId) {
        discoveredListings = [];
        discoveryError = 'Discovery service is unavailable. Start easyservice-discovery on port 8090.';
      }
      console.error('Discovery search failed:', error);
    } finally {
      if (requestId === discoveryRequestId) discoveryLoading = false;
    }
  }

  function handleSelectCategory(cat) {
    clearListingDetail();
    if (selectedCategory !== cat) {
      transitionCategory = cat;
      isTransitioning = true;
      selectedCategory = cat;

      setTimeout(() => {
        isTransitioning = false;
      }, 180);
    }

    void handleDiscoverySearch(cat);
  }

  function toggleFavorite(id) {
    if (favoriteIds.has(id)) {
      favoriteIds.delete(id);
    } else {
      favoriteIds.add(id);
    }
    favoriteIds = new Set(favoriteIds);
    localStorage.setItem('easyservice_favorites', JSON.stringify([...favoriteIds]));
  }

  const amharicAtmosphereCopy = {
    ALL: { tag: 'የኢትዮጵያ የገበያ ቦታ', headline: 'በኢትዮጵያ የሚገባውን ሁሉ በአንድ ቦታ።', subtitle: 'የተረጋገጡ መኖሪያዎችን፣ የመኪና ኪራይን፣ ባህላዊ ልምዶችን እና የእጅ ሥራዎችን ያግኙ።' },
    HOTEL: { tag: 'መኖሪያ • 7 የተረጋገጡ ሆቴሎች', headline: 'የማይረሳ መኖሪያ ያግኙ።', subtitle: 'በኢትዮጵያ ያሉ የታመኑ ሆቴሎችንና ሪዞርቶችን ያወዳድሩ።' },
    CAR_RENTAL: { tag: 'መንዳት • 7 የተረጋገጡ የመኪና መርከቦች', headline: 'በኢትዮጵያ ርቀው ይጓዙ።', subtitle: '4×4 መኪናዎችን፣ የቅንጦት መኪናዎችን እና የሳፋሪ መኪናዎችን ይከራዩ።' },
    EVENT: { tag: 'ልምድ • 7 የተረጋገጡ ዝግጅቶች', headline: 'በኢትዮጵያ ምን እየተከናወነ ነው?', subtitle: 'የሙዚቃ፣ የባህል እና የስፖርት ዝግጅቶችን ያግኙ።' },
    STORE: { tag: 'ግዢ • 7 የኢትዮጵያ የእጅ ሥራ ሱቆች', headline: 'ኢትዮጵያን ወደ ቤትዎ ይዘው ይሂዱ።', subtitle: 'ቡና፣ ባህላዊ ልብሶች፣ ጌጣጌጦች እና የእጅ ሥራዎችን ይግዙ።' }
  };
  $: currentAtmosphere = currentLanguage === 'am'
    ? { ...categoryAtmospheres[selectedCategory] || categoryAtmospheres.ALL, ...amharicAtmosphereCopy[selectedCategory] || amharicAtmosphereCopy.ALL }
    : (categoryAtmospheres[selectedCategory] || categoryAtmospheres.ALL);

  $: filteredListings = listings.filter(l => {
    const matchCat = selectedCategory === 'ALL' || l.category === selectedCategory;
    const matchLoc = selectedLocation === 'ALL' || (l.location && l.location.toLowerCase().includes(selectedLocation.toLowerCase()));
    const q = (searchQuery || '').trim().toLowerCase();
    const matchSearch = !q || [l.title, l.description, l.hostName, l.location].some((field) => (field || '').toLowerCase().includes(q));
    const matchVerified = !onlyVerified || l.status === 'PUBLISHED';
    const matchGuests =
      selectedCategory !== 'HOTEL' ||
      l.sourceType === 'IMPORTED' ||
      Number(guestCount) <= (l.capacity || 1);
    const haystack = `${l.title} ${l.description} ${(l.variants || []).map((v) => v.title).join(' ')}`.toLowerCase();
    const matchVehicle =
      selectedCategory !== 'CAR_RENTAL' ||
      l.sourceType === 'IMPORTED' ||
      vehicleType === 'ALL' ||
      (vehicleType === '4X4' && /4x4|4×4|land cruiser|offroad|off-road/.test(haystack)) ||
      (vehicleType === 'SUV' && /suv|tucson|crossover/.test(haystack)) ||
      (vehicleType === 'SEDAN' && /sedan|e-class|executive|vip/.test(haystack));
    return matchCat && matchLoc && matchSearch && matchVerified && matchGuests && matchVehicle;
  }).sort((a, b) => {
    if (sortBy === 'PRICE_LOW') return a.price - b.price;
    if (sortBy === 'PRICE_HIGH') return b.price - a.price;
    return 0;
  });
  $: filteredDiscoveredListings = discoveredListings.filter(l => {
    const matchCat =
      selectedCategory === 'ALL' ||
      l.category === selectedCategory;

    const locationName = selectedLocation === 'Addis'
      ? 'Addis Ababa'
      : selectedLocation;
    const matchLoc =
      selectedLocation === 'ALL' ||
      (l.location &&
        l.location.toLowerCase().includes(locationName.toLowerCase()));

    const q = (searchQuery || '').trim().toLowerCase();

    const matchSearch =
      !q ||
      [l.title, l.description, l.hostName, l.location, l.area]
        .some((field) =>
          (field || '').toLowerCase().includes(q)
        );

    return matchCat && matchLoc && matchSearch;
  });
  $: displayedFilteredListings = filteredListings.map(localizeListing);
  $: displayedDiscoveredListings = filteredDiscoveredListings.map(localizeListing);
  $: customerBookings = getCustomerBookings($userBookings, $currentUser?.id);
  $: providerBookings = ($currentUser && $currentUser.role === 'PROVIDER')
    ? getProviderBookings($userBookings, $currentUser.id, companyName)
    : [];
  $: providerCalendar = providerBookings.reduce((days, booking) => {
    if (!isBookingActive(booking)) return days;
    const dates = getBookingOccupiedDates(booking);
    (dates.length ? dates : [booking.startDate || booking.bookingDate || 'Unscheduled']).forEach(date => {
      days[date] = (days[date] || 0) + 1;
    });
    return days;
  }, {});
  $: providerListingsForDashboard = listings.filter(listing =>
    $currentUser?.role === 'PROVIDER' &&
    (listing.providerId === $currentUser.id || (companyName && listing.hostName === companyName))
  );
  $: providerRevenue = providerBookings.filter(isBookingActive).reduce((sum, booking) => sum + Number(booking.totalAmount || 0), 0);

  $: favoritedListings = listings.filter(l => favoriteIds.has(l.id));
  $: displayedFavoritedListings = favoritedListings.map(localizeListing);

  $: trendingListings = listings.slice(0, 3);

  // Customer Profile Spending Analytics
  $: hotelBookings = customerBookings.filter(b => b.category === 'HOTEL' && b.status === 'CONFIRMED');
  $: carBookings = customerBookings.filter(b => b.category === 'CAR_RENTAL' && b.status === 'CONFIRMED');
  $: eventBookings = customerBookings.filter(b => b.category === 'EVENT' && b.status === 'CONFIRMED');
  $: storeBookings = customerBookings.filter(b => b.category === 'STORE' && b.status === 'CONFIRMED');

  $: hotelSpent = hotelBookings.reduce((sum, b) => sum + (b.totalAmount || 0), 0);
  $: carSpent = carBookings.reduce((sum, b) => sum + (b.totalAmount || 0), 0);
  $: eventSpent = eventBookings.reduce((sum, b) => sum + (b.totalAmount || 0), 0);
  $: storeSpent = storeBookings.reduce((sum, b) => sum + (b.totalAmount || 0), 0);
  $: totalSpentLifetime = hotelSpent + carSpent + eventSpent + storeSpent;

  function handleRegisterCompany() {
    if (!companyName) return;
    isCompanyRegistered = true;
    currentUser.update((user) => user ? { ...user, role: 'PROVIDER' } : user);
    localStorage.setItem(`easyservice_provider_profile_${$currentUser.id}`, JSON.stringify({ companyName, companyCategory, companyLocation, companyPhone }));
    providerSuccessMsg = `Company "${companyName}" registered successfully! Welcome to your Provider Dashboard.`;
  }

  function handleCreateProviderListing() {
    if (!newTitle || newPrice <= 0 || !Number.isInteger(Number(newCapacity)) || newCapacity <= 0 || (companyCategory === 'EVENT' && !newEventDate)) {
      providerSuccessMsg = companyCategory === 'EVENT' && !newEventDate
        ? 'Event date is required.'
        : 'Price and inventory must be whole numbers greater than zero.';
      return;
    }

    const listingId = editingListingId || 'prov_' + Date.now();
    const newListing = {
      id: listingId,
      title: `${companyName} — ${newTitle}`,
      category: String(companyCategory).toUpperCase(),
      providerId: $currentUser?.id || companyName,
      description: newDesc || `Offered directly by ${companyName}.`,
      price: Number(newPrice),
      capacity: Number(newCapacity),
      availableQuantity: Number(newCapacity),
      status: 'PUBLISHED',
      location: companyLocation || 'Addis Ababa',
      hostName: companyName,
      imageUrl: newImage,
      amenities: [
        amenityWifi && '📶 Free WiFi', amenityParking && '🚗 Free Parking',
        amenityRestaurant && '🍽️ Restaurant', amenityPool && '🏊 Swimming Pool',
        amenitySpa && '🪷 Spa & Wellness', amenityGym && '🏋️ Gym'
      ].filter(Boolean),
      cancellationPolicy: newCancellationPolicy,
      checkIn: newCheckIn,
      checkOut: newCheckOut,
      accommodationType: companyCategory === 'HOTEL' ? newAccommodationType : null,
      roomType: companyCategory === 'HOTEL' ? newRoomType : null,
      vehicleType: companyCategory === 'CAR_RENTAL' ? newVehicleType : null,
      vehicleBrand: companyCategory === 'CAR_RENTAL' ? newVehicleBrand : null,
      vehicleModel: companyCategory === 'CAR_RENTAL' ? newVehicleModel : null,
      shopCategory: companyCategory === 'STORE' ? newShopCategory : null,
      eventCategory: companyCategory === 'EVENT' ? newEventCategory : null,
      eventDate: companyCategory === 'EVENT' ? newEventDate : null,
      eventStartTime: companyCategory === 'EVENT' ? newEventStartTime : null,
      eventEndTime: companyCategory === 'EVENT' ? newEventEndTime : null,
      variants: [
        { id: 'v_custom_1', title: `${newTitle} (${companyCategory === 'HOTEL' ? newRoomType : companyCategory === 'CAR_RENTAL' ? `${newVehicleBrand} ${newVehicleModel}` : companyCategory === 'EVENT' ? newEventCategory : newShopCategory})`, desc: newDesc || 'High quality service option.', price: Number(newPrice), availableCount: Number(newCapacity), unitLabel: companyCategory === 'HOTEL' ? '/ night' : (companyCategory === 'CAR_RENTAL' ? '/ day' : companyCategory === 'EVENT' ? '/ ticket' : '/ item'), badge: newType }
      ]
    };

    listings = editingListingId
      ? listings.map((listing) => listing.id === editingListingId ? newListing : listing)
      : [newListing, ...listings];
    providerPublishedListings = editingListingId
      ? providerPublishedListings.map((listing) => listing.id === editingListingId ? newListing : listing)
      : [newListing, ...providerPublishedListings];
    localStorage.setItem('easyservice_provider_listings', JSON.stringify(providerPublishedListings));
    providerSuccessMsg = editingListingId
      ? `Listing "${newTitle}" updated successfully.`
      : `New listing "${newTitle}" created and published successfully under ${companyName}!`;
    showAddListingModal = false;
    editingListingId = null;
    newTitle = '';
    newDesc = '';
  }

  function editProviderListing(listing) {
    editingListingId = listing.id;
    newTitle = listing.title.replace(`${companyName} — `, '');
    newPrice = Number(listing.price || 0);
    newCapacity = Number(listing.capacity || listing.availableQuantity || 1);
    newImage = listing.imageUrl || '';
    newDesc = listing.description || '';
    const amenityText = (listing.amenities || []).join(' ').toLowerCase();
    amenityWifi = amenityText.includes('wifi') && !amenityText.includes('no wifi');
    amenityParking = amenityText.includes('parking') && !amenityText.includes('no parking');
    amenityRestaurant = amenityText.includes('restaurant') && !amenityText.includes('no restaurant');
    amenityPool = amenityText.includes('pool') && !amenityText.includes('no pool');
    amenitySpa = amenityText.includes('spa') && !amenityText.includes('no spa');
    amenityGym = amenityText.includes('gym') && !amenityText.includes('no gym');
    newCancellationPolicy = listing.cancellationPolicy || 'Free cancellation up to 24 hours before check-in';
    newCheckIn = listing.checkIn || '14:00';
    newCheckOut = listing.checkOut || '12:00';
    showAddListingModal = true;
  }

  function handleListingImageUpload(event) {
    const file = event.currentTarget.files?.[0];
    if (!file) return;
    const reader = new FileReader();
    reader.onload = () => { newImage = String(reader.result); };
    reader.readAsDataURL(file);
  }

  async function handleSwitchUser(u) {
    $currentUser = u;
    if (restoreProviderProfile(u.id)) {
      currentUser.update((user) => user ? { ...user, role: 'PROVIDER' } : user);
    }
    await syncBackendBookings(u);
  }

  async function handleProviderStatus(bookingId, providerStatus, reason = '') {
    updateBookingProviderStatus(bookingId, providerStatus, reason);
    if (!$currentUser?.id) return;
    try {
      const updated = await updateProviderBookingStatus(bookingId, getBackendProviderId($currentUser.id), providerStatus);
      mergeBookings([updated]);
    } catch (err) {
      // Local status is still authoritative for the offline demo/stub booking.
      console.warn('Provider status backend sync skipped:', err);
    }
  }

  async function handleCancelBooking(bookingId, reason) {
    const refund = cancelBookingItem(bookingId, reason);
    if (refund > 0) {
      currentUser.update((u) => u ? { ...u, balance: Number(u.balance || 0) + refund } : u);
    }
    if ($currentUser?.id) {
      try {
        const updated = await cancelBooking(bookingId, $currentUser.id);
        mergeBookings([updated]);
      } catch (err) {
        console.warn('Booking cancel backend sync skipped:', err);
      }
    }
  }

  function handleCompleteBooking(bookingId) {
    markBookingCompleted(bookingId);
  }

  function handleBookingReview(bookingId, rating, reviewText) {
    addBookingReview(bookingId, rating, reviewText);
    const reviewedBooking = $userBookings.find((booking) => booking.id === bookingId);
    if (reviewedBooking) updateListingReviewStats(reviewedBooking.listingId);
  }

  function updateListingReviewStats(listingId) {
    const reviews = getListingBookings($userBookings, listingId).filter((booking) => booking.rating);
    if (!reviews.length) return;
    const reviewStats = {
      rating: reviews.reduce((sum, booking) => sum + Number(booking.rating), 0) / reviews.length,
      reviewCount: reviews.length
    };
    listings = listings.map((listing) => listing.id === listingId ? { ...listing, ...reviewStats } : listing);
    if (selectedProviderListing?.id === listingId) selectedProviderListing = { ...selectedProviderListing, ...reviewStats };
  }

  function openListing(listing) {
    const reviews = getListingBookings($userBookings, listing.id).filter((booking) => booking.rating);
    const reviewStats = reviews.length
      ? { rating: reviews.reduce((sum, booking) => sum + Number(booking.rating), 0) / reviews.length, reviewCount: reviews.length }
      : {};
    listingReturnState = {
      hash: window.location.hash,
      scrollY: window.scrollY,
      category: selectedCategory,
      location: selectedLocation,
      search: searchQuery
    };
    selectedProviderListing = { ...listing, ...reviewStats };
    activeTab = 'listings';

    window.history.pushState(
      { easyServiceListing: true, returnState: listingReturnState },
      '',
      `#listing-${listing.id}`
    );
    window.scrollTo({ top: 0, left: 0, behavior: 'auto' });
  }

  function closeListing() {
    const returnState = listingReturnState || window.history.state?.returnState;
    selectedProviderListing = null;
    listingReturnState = null;
    if (window.location.hash.startsWith('#listing-')) {
      const returnHash = returnState?.hash || '';
      window.history.replaceState({}, '', `${window.location.pathname}${window.location.search}${returnHash}`);
    }
    selectedCategory = returnState?.category || selectedCategory;
    selectedLocation = returnState?.location || selectedLocation;
    searchQuery = returnState?.search || searchQuery;
    requestAnimationFrame(() => window.scrollTo({ top: returnState?.scrollY || 0, left: 0, behavior: 'auto' }));
  }

  function clearListingDetail() {
    selectedProviderListing = null;
    if (window.location.hash.startsWith('#listing-')) {
      window.history.replaceState({}, '', `${window.location.pathname}${window.location.search}`);
    }
  }

  function scrollToResults() {
    const el = document.querySelector('.marketplace-browsing-section');
    if (el) el.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }

  function goToCategory(cat) {
    activeTab = 'listings';
    sitePage = null;
    selectedProviderListing = null;
    const categoryRoutes = {
      ALL: 'explore',
      HOTEL: 'stays',
      CAR_RENTAL: 'drive',
      EVENT: 'experiences',
      STORE: 'shop'
    };
    const route = categoryRoutes[cat] || 'explore';
    if (window.location.hash !== `#${route}`) {
      window.history.pushState({}, '', `#${route}`);
    }
    handleSelectCategory(cat);
    setTimeout(scrollToResults, 50);
  }

  function goToLocation(locId) {
    activeTab = 'listings';
    clearListingDetail();
    selectedLocation = locId;
    void handleDiscoverySearch();
    setTimeout(scrollToResults, 50);
  }

  function handleLocationChange(locId) {
    selectedLocation = locId;
    void handleDiscoverySearch();
  }
</script>

<main class="app-container">
  <!-- Category Switcher Transition Banner Animation -->
  {#if isTransitioning}
    <div class="category-transition-overlay animate-fade-in">
      <div class="transition-icon-mover">
        {#if transitionCategory === 'CAR_RENTAL'}
          <div class="moving-vehicle">
            <Icon name="car" size={38} color="var(--accent-gold)" />
            <span>Cruising to Drive 4x4s & Car Fleets...</span>
          </div>
        {:else if transitionCategory === 'HOTEL'}
          <div class="moving-vehicle">
            <Icon name="bed" size={38} color="var(--accent-gold)" />
            <span>Opening Hotels & Luxury Resorts...</span>
          </div>
        {:else if transitionCategory === 'EVENT'}
          <div class="moving-vehicle">
            <Icon name="ticket" size={38} color="var(--accent-gold)" />
            <span>Unlocking Ethiopian Experiences...</span>
          </div>
        {:else}
          <div class="moving-vehicle">
            <Icon name="bag" size={38} color="var(--accent-gold)" />
            <span>Discovering Artisan Coffee & Kemis Crafts...</span>
          </div>
        {/if}
      </div>
    </div>
  {/if}

  <!-- Navbar Header -->
  <Navbar 
    bind:activeTab 
    bind:selectedCategory 
    onNavigate={goToCategory}
    {currentTheme} 
    {toggleTheme} 
    {currentLanguage}
    {toggleLanguage}
    openSpinModal={() => showSpinModal = true} 
    openToolsModal={() => showToolsModal = true}
    openRegisterModal={() => showRegisterModal = true} 
    onSwitchUser={handleSwitchUser} 
  />

  <EasyAssistant listings={listings} discoveredListings={discoveredListings} currentLanguage={currentLanguage} on:navigate={(event) => handleAssistantNavigation(event.detail)} on:openListing={(event) => openListing(event.detail)} on:openDiscoveredListing={(event) => selectedDiscoveredListing = event.detail} />

  <div class="main-content">
    {#if sitePage}
      <SitePage
        page={sitePage}
        currentUser={$currentUser}
        listings={listings.map(localizeListing)}
        bookings={customerBookings}
        currentLanguage={currentLanguage}
        currency={currency}
        on:navigate={(event) => navigateToSitePage(event.detail)}
        on:category={(event) => goToCategory(event.detail)}
        on:location={(event) => goToLocation(event.detail)}
        on:provider={() => navigateToSitePage('provider')}
        on:walletTopUp={(event) => handleWalletTopUp(event.detail)}
        on:openListing={(event) => openListing(event.detail)}
      />
    {:else if activeTab === 'listings'}
      {#if selectedProviderListing}
        <!-- Standalone Full Property Detail Page -->
        <ProviderShowcaseModal 
          listing={selectedProviderListing} 
          currentLanguage={currentLanguage}
          currency={currency}
          isFavorite={favoriteIds.has(selectedProviderListing.id)}
          onToggleFavorite={toggleFavorite}
          onClose={closeListing}
          onSelectVariant={(variant) => { selectedListing = variant; window.scrollTo({ top: 0, left: 0, behavior: 'auto' }); }} 
        />
      {:else}
      {#if selectedDiscoveredListing}
        <DiscoveredListingModal
          listing={selectedDiscoveredListing}
          on:close={() => selectedDiscoveredListing = null}
        />
      {/if}
        <!-- Hero Atmosphere & Search Module -->
      <section class="hero-section animate-fade-in" style="--accent-current: {currentAtmosphere.accentColor};">
        <div class="hero-bg-wrapper">
          <img 
            src={currentAtmosphere.bgImg} 
            alt={currentAtmosphere.headline} 
            class="hero-bg-img" 
          />
          <div class="hero-overlay"></div>
        </div>

        <div class="hero-content-box">
          <span class="hero-pill-badge">{currentAtmosphere.tag}</span>
          <h1 class="hero-headline">{currentAtmosphere.headline}</h1>
          <p class="hero-subtext">{currentAtmosphere.subtitle}</p>

          <!-- Category Experience Switcher Tabs -->
          <div class="category-experience-tabs">
            <button class="experience-tab {selectedCategory === 'ALL' ? 'active' : ''}" on:click={() => handleSelectCategory('ALL')}>
              <Icon name="sparkles" size={14} /> {currentLanguage === 'am' ? 'ሁሉንም ያስሱ (28 ንግዶች)' : 'Explore All (28 Businesses)'}
            </button>
            <button class="experience-tab {selectedCategory === 'HOTEL' ? 'active' : ''}" on:click={() => handleSelectCategory('HOTEL')}>
              <Icon name="bed" size={14} /> {currentLanguage === 'am' ? '7 ሆቴሎች እና መኖሪያዎች' : '7 Hotels & Stays'}
            </button>
            <button class="experience-tab {selectedCategory === 'CAR_RENTAL' ? 'active' : ''}" on:click={() => handleSelectCategory('CAR_RENTAL')}>
              <Icon name="car" size={14} /> {currentLanguage === 'am' ? '7 የመኪና መርከቦች' : '7 Car Fleets'}
            </button>
            <button class="experience-tab {selectedCategory === 'EVENT' ? 'active' : ''}" on:click={() => handleSelectCategory('EVENT')}>
              <Icon name="ticket" size={14} /> {currentLanguage === 'am' ? '7 ዝግጅቶች' : '7 Events'}
            </button>
            <button class="experience-tab {selectedCategory === 'STORE' ? 'active' : ''}" on:click={() => handleSelectCategory('STORE')}>
              <Icon name="bag" size={14} /> {currentLanguage === 'am' ? '7 የእጅ ሥራ ሱቆች' : '7 Crafts Shops'}
            </button>
          </div>

          <!-- Context-Aware Search Module -->
          <div class="search-module marketplace-card">
            <div class="search-field">
              <label for="searchWhereInput">{currentLanguage === 'am' ? 'በኢትዮጵያ የት?' : 'Where in Ethiopia?'}</label>
              <div class="input-with-icon">
                <Icon name="mappin" size={16} color="var(--accent-gold)" />
                <select id="searchWhereInput" bind:value={selectedLocation} class="search-select">
                  <option value="ALL">{currentLanguage === 'am' ? 'ሁሉም የኢትዮጵያ መዳረሻዎች' : 'All Ethiopia Destinations'}</option>
                  <option value="Addis">{currentLanguage === 'am' ? 'አዲስ አበባ' : 'Addis Ababa'}</option>
                  <option value="Bishoftu">Bishoftu Lakefront</option>
                  <option value="Hawassa">Lake Hawassa</option>
                  <option value="Lalibela">Lalibela Highlands</option>
                  <option value="Bahir Dar">Bahir Dar & Tana</option>
                </select>
              </div>
            </div>

            <div class="search-divider"></div>

            {#if selectedCategory === 'HOTEL'}
              <div class="search-field">
                <label for="checkInInput">Dates</label>
                <input id="checkInInput" type="date" bind:value={checkInDate} class="search-text-input" />
              </div>
              <div class="search-divider"></div>
              <div class="search-field">
                <label for="guestsSelect">Guests</label>
                <select id="guestsSelect" bind:value={guestCount} class="search-select">
                  <option value={1}>1 Guest</option>
                  <option value={2}>2 Guests (Suite)</option>
                  <option value={4}>4 Guests (Family)</option>
                </select>
              </div>
            {:else if selectedCategory === 'CAR_RENTAL'}
              <div class="search-field">
                <label for="vehicleTypeSelect">Vehicle Type</label>
                <select id="vehicleTypeSelect" bind:value={vehicleType} class="search-select">
                  <option value="ALL">All Vehicle Types</option>
                  <option value="4X4">4x4 Land Cruiser</option>
                  <option value="SUV">Luxury SUV</option>
                  <option value="SEDAN">Executive Sedan</option>
                </select>
              </div>
            {:else}
              <div class="search-field">
                <label for="searchQueryInput">Keyword Search</label>
                <input id="searchQueryInput" type="text" placeholder="e.g. Kuriftu, Skylight, Land Cruiser, Coffee..." bind:value={searchQuery} class="search-text-input" />
              </div>
            {/if}

            <button class="btn-gold search-btn"on:click={handleDiscoverySearch}>
              <Icon name="search" size={18} /> Search Businesses
            </button>
          </div>

          <!-- Location Quick Chips -->
          <div class="location-chips-row">
            <span class="chips-label">Popular Spots:</span>
            {#each ethiopianLocations as loc}
              <button
                class="location-chip {selectedLocation === loc.id ? 'active' : ''}"
                on:click={() => handleLocationChange(loc.id)}>
                <Icon name={loc.icon} size={12} /> {loc.name}
              </button>
            {/each}
          </div>
        </div>
      </section>



      <!-- Main Marketplace Grid Section -->
      <section class="marketplace-browsing-section">
        <!-- Sidebar Filters -->
        <aside class="filters-sidebar marketplace-card">
          <div class="filter-header">
            <h3>Filter Marketplace</h3>

            {#if selectedCategory !== 'ALL' || selectedLocation !== 'ALL' || searchQuery}
              <button
                 class="clear-btn"
                 on:click={() => {
                   selectedCategory = 'ALL';
                   selectedLocation = 'ALL';
                   searchQuery = '';
                 }}
              >
                 Reset
              </button>
            {/if}
          </div>

          <div class="filter-group">
              <label for="filterLocationSelect" class="filter-label">
                {currentLanguage === 'am' ? 'መዳረሻ' : 'Destination'}
            </label>

            <select
              id="filterLocationSelect"
              bind:value={selectedLocation}
              on:change={() => handleLocationChange(selectedLocation)}
              class="input-field"
            >
              <option value="ALL">All Destinations</option>
              <option value="Addis">Addis Ababa</option>
              <option value="Bishoftu">Bishoftu Lakefront</option>
              <option value="Hawassa">Lake Hawassa</option>
              <option value="Lalibela">Lalibela Highlands</option>
              <option value="Bahir Dar">Bahir Dar</option>
            </select>
          </div>

          <div class="filter-group">
              <label for="filterCategorySelect" class="filter-label">
                {currentLanguage === 'am' ? 'ምድብ' : 'Category'}
            </label>

            <select
              id="filterCategorySelect"
              bind:value={selectedCategory}
              on:change={() => handleSelectCategory(selectedCategory)}
              class="input-field"
            >
              <option value="ALL">{currentLanguage === 'am' ? 'ሁሉም 28 ንግዶች' : 'All 28 Businesses'}</option>
              <option value="HOTEL">{currentLanguage === 'am' ? '7 ሆቴሎች እና ሪዞርቶች' : '7 Hotels & Resorts'}</option>
              <option value="CAR_RENTAL">{currentLanguage === 'am' ? '7 የመኪና ኪራዮች' : '7 Car Rentals'}</option>
              <option value="EVENT">{currentLanguage === 'am' ? '7 የዝግጅት ትኬት አዘጋጆች' : '7 Event Pass Organizers'}</option>
              <option value="STORE">{currentLanguage === 'am' ? '7 የእጅ ሥራ ሱቆች' : '7 Artisan Shops'}</option>
            </select>
          </div>
        </aside>

        <!-- Listings Grid Container -->
        <div class="listings-main-area">

          <div class="sort-header-row">
            <div>
              <h2 class="browse-title">
                {selectedCategory === 'ALL'
                ? `${footerLabels.allBusinesses} (${currentLanguage === 'am' ? '28 ድምር' : '28 Total'})`
                : categoryAtmospheres[selectedCategory].tag}
              </h2>

              <span class="results-count">
                {filteredListings.length} {footerLabels.verifiedListed}
              </span>
            </div>

            <div class="sort-controls">
              <label for="sortBySelect" class="sort-label">
                {footerLabels.sortBy}:
              </label>

              <select
                id="sortBySelect"
                bind:value={sortBy}
                class="input-field sort-select"
              >
                <option value="RECOMMENDED">{footerLabels.recommended}</option>
                <option value="PRICE_LOW">{footerLabels.priceLow}</option>
                <option value="PRICE_HIGH">{footerLabels.priceHigh}</option>
              </select>
            </div>
          </div>

          {#if discoveryLoading || discoveredListings.length > 0 || discoveryError}
            <section class="discovery-section">

              <div class="discovery-hero">

                <div class="discovery-hero-content">

                  <div class="discovery-eyebrow">
                    <span class="discovery-eyebrow-icon">
                      <Icon name="globe" size={15} />
                    </span>
                    {footerLabels.discovery}
                  </div>

                  <h2>
                    {currentLanguage === 'am' ? 'የህዝብ ምንጭ' : 'Public Source'}
                    <span>{currentLanguage === 'am' ? 'ፍለጋ' : 'Discovery'}</span>
                  </h2>

                  <p>
                    {footerLabels.discoveryDesc}
                  </p>

                  <div class="discovery-count">
                    <span class="discovery-count-icon">
                      <Icon name="search" size={16} />
                    </span>

                    <strong>{discoveredListings.length}</strong>
                    <span>{discoveryLoading ? footerLabels.searching : footerLabels.found}</span>
                  </div>

                </div>

                <div class="discovery-hero-mark">
                  ES
                </div>

                <div class="discovery-hero-line"></div>

              </div>


              <div class="discovery-results-header">
                <div>
                  <span class="discovery-results-label">
                    {footerLabels.discovery}
                  </span>

                  <h3>{footerLabels.discovered}</h3>

                  <p>
                    {footerLabels.discoveryDesc}
                    {#if discoveryLoading}
                      <br />{currentLanguage === 'am' ? 'የተዋቀሩ የህዝብ ምንጮች እየተፈተሹ ነው...' : 'Searching configured public sources now...'}
                    {/if}
                  </p>
                </div>

                <div class="discovery-results-count">
                  {discoveredListings.length} {footerLabels.found}
                </div>
              </div>


              {#if filteredDiscoveredListings.length > 0}
                <div class="discovery-grid">
                  {#each displayedDiscoveredListings as listing}
                    <DiscoveredListingCard
                      {listing}
                      currentLanguage={currentLanguage}
                      on:select={(event) => selectedDiscoveredListing = event.detail}
                    />
                  {/each}
                </div>
              {:else if discoveryLoading}
                <div class="marketplace-card empty-state-box">
                  <Icon name="search" size={36} color="var(--accent-gold)" />
                  <h3>{currentLanguage === 'am' ? 'የህዝብ ንግዶችን በመፈለግ ላይ' : 'Finding public businesses'}</h3>
                  <p>{currentLanguage === 'am' ? 'ፍለጋው የተዋቀሩ የህዝብ ድረ-ገጾችን እየፈተሸ ነው። ውጤቶቹ በራስ-ሰር እዚህ ይታያሉ።' : 'Discovery is checking the configured public websites. Results will appear here automatically.'}</p>
                </div>
              {:else if discoveryError}
                <div class="marketplace-card empty-state-box">
                  <Icon name="search" size={36} color="var(--text-muted)" />
                  <h3>No matching business providers found</h3>
                  <p>Try adjusting your search location, category, or keyword filters.</p>
                  <button
                    class="btn-outline"
                    on:click={() => {
                      selectedCategory = 'ALL';
                      selectedLocation = 'ALL';
                      searchQuery = '';
                      void handleDiscoverySearch('ALL');
                    }}
                  >
                    {footerLabels.reset}
                  </button>
                </div>
              {/if}

            </section>
          {/if}

          <!-- EASYSERVICE MARKETPLACE -->
          {#if filteredListings.length > 0}

            <section class="easyservice-marketplace-section">
              <div class="marketplace-section-header">
                <div>
                  <span class="marketplace-eyebrow">
                    EASYSERVICE MARKETPLACE
                  </span>
                  <h2>Book with EasyService</h2>
                  <p>
                    Verified providers available to book directly through EasyService.
                  </p>
                </div>

                <span class="marketplace-count">
                  {filteredListings.length} available
                </span>
              </div>

              <div class="listings-grid">
                {#each displayedFilteredListings as listing}
                  <ListingCard
                    {listing}
                    currentCurrency={currency}
                    isFavorite={favoriteIds.has(listing.id)}
                    onToggleFavorite={toggleFavorite}
                    onSelect={openListing}
                  />
                {/each}
              </div>
            </section>
          {:else if discoveredListings.length > 0}
            <!-- Discovery exists, but no EasyService providers match -->
            <section class="easyservice-empty-state">
              <div class="empty-state-icon">⌂</div>
              <h3>No EasyService providers found</h3>
              <p>
                We found businesses from public sources above, but there are currently
                no EasyService providers matching this search.
              </p>
            </section>
          {:else}
            <!-- Nothing found at all -->
            <div class="marketplace-card empty-state-box">
              <Icon
                name="search"
                size={36}
                color="var(--text-muted)"
              />
              <h3>No matching business providers found</h3>
              <p>Try adjusting your search location, category, or keyword filters.
              </p>
              <button
                class="btn-outline"
                on:click={() => {
                  selectedCategory = 'ALL';
                  selectedLocation = 'ALL';
                  searchQuery = '';
                }}
              >
                Reset All Filters
              </button>
            </div>
          {/if}
        </div>
      </section>
      {/if}

    {:else if activeTab === 'deals'}
      <!-- 🔥 Section 15: Dedicated Hot Deals Marketplace Section -->
      <section class="hot-deals-section animate-fade-in">
        <div class="deals-hero-banner">
          <div>
            <span class="sub-pill">EXCLUSIVE LIMITED TIME OFFERS</span>
            <h2>🔥 Hot Deals & Promotional Discounts</h2>
            <p>Save up to 30% on luxury resorts, overland 4x4 safaris, festival passes, and handcrafted goods.
            </p>
          </div>
        </div>

        <div class="deals-grid">
          <!-- Deal 1 -->
          <div class="marketplace-card deal-card">
            <div class="deal-badge-overlay">20% OFF</div>
            <img src="https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=800&q=80" alt="Kuriftu Resort Deal" class="deal-img" />
            <div class="deal-body">
              <span class="deal-category">🏨 HOTEL RESORT DEAL</span>
              <h3>🔥 Kuriftu Weekend Lakeside Deal</h3>
              <p class="deal-host">Kuriftu Resort & Spa Bishoftu</p>
              
              <div class="timer-box">
                <span>Ends in:</span>
                <strong class="timer-val">02d 14h 32m</strong>
              </div>

              <div class="deal-price-row">
                <div class="price-strikethrough-box">
                  <span class="orig-price">ETB 5,000</span>
                  <span class="discounted-price">ETB 4,000 / night</span>
                </div>
                <button class="btn-gold" on:click={() => { selectedListing = { ...mockProviders[0], title: '🔥 Kuriftu Weekend Lakeside Deal', price: 5000, preAppliedPromo: 'SUMMER20' }; }}>
                  Book Deal →
                </button>
              </div>
            </div>
          </div>

          <!-- Deal 2 -->
          <div class="marketplace-card deal-card">
            <div class="deal-badge-overlay">15% OFF</div>
            <img src="https://images.unsplash.com/photo-1533473359331-0135ef1b58bf?auto=format&fit=crop&w=800&q=80" alt="Car Rental Safari Deal" class="deal-img" />
            <div class="deal-body">
              <span class="deal-category">🚗 CAR RENTAL DEAL</span>
              <h3>🔥 5-Day Highland Safari Special</h3>
              <p class="deal-host">Kebede 4×4 Offroad Rentals</p>
              
              <div class="timer-box">
                <span>Ends in:</span>
                <strong class="timer-val">01d 08h 15m</strong>
              </div>

              <div class="deal-price-row">
                <div class="price-strikethrough-box">
                  <span class="orig-price">ETB 3,500</span>
                  <span class="discounted-price">ETB 2,975 / day</span>
                </div>
                <button class="btn-gold" on:click={() => { selectedListing = { ...mockProviders[7], title: '🔥 5-Day Highland Safari Special', price: 3500, preAppliedPromo: 'GOLD15' }; }}>
                  Book Deal →
                </button>
              </div>
            </div>
          </div>

          <!-- Deal 3 -->
          <div class="marketplace-card deal-card">
            <div class="deal-badge-overlay">30% OFF</div>
            <img src="https://images.unsplash.com/photo-1514525253161-7a46d19cd819?auto=format&fit=crop&w=800&q=80" alt="African Jazz Fest Deal" class="deal-img" />
            <div class="deal-body">
              <span class="deal-category">🎟 EVENT FESTIVAL DEAL</span>
              <h3>🔥 African Jazz Early Bird VIP Pass</h3>
              <p class="deal-host">Addis Music Festival Org</p>
              
              <div class="timer-box">
                <span>Ends in:</span>
                <strong class="timer-val">04d 18h 45m</strong>
              </div>

              <div class="deal-price-row">
                <div class="price-strikethrough-box">
                  <span class="orig-price">ETB 1,500</span>
                  <span class="discounted-price">ETB 1,050 / pass</span>
                </div>
                <button class="btn-gold" on:click={() => { selectedListing = { ...mockProviders[14], title: '🔥 African Jazz Early Bird VIP Pass', price: 1500, preAppliedPromo: 'ETHIO30' }; }}>
                  Book Deal →
                </button>
              </div>
            </div>
          </div>
        </div>
      </section>

    {:else if activeTab === 'history'}
      <!-- Customer Passport Profile Dashboard -->
      <section class="passport-dashboard animate-fade-in">
        <div class="marketplace-card profile-summary-card">
          <div class="profile-main-meta">
            <div class="avatar-large">
              <Icon name="user" size={32} color="#ffffff" />
            </div>
            <div>
              <div class="user-title-row">
                <h2>{$currentUser ? $currentUser.name : 'Abebe Kebede'}</h2>
                <span class="badge-verified">✓ {$currentUser ? $currentUser.identityStatus : 'FAYDA VERIFIED'}</span>
              </div>
              <p class="user-sub-email">{$currentUser ? $currentUser.email : 'user1@aau.edu.et'}</p>
            </div>
          </div>

          <div class="spending-analytics-grid">
            <div class="spending-box">
              <span class="sp-lbl"><Icon name="bed" size={14} color="var(--accent-gold)" /> Hotels & Stays</span>
              <span class="sp-qty">{hotelBookings.length} Bookings</span>
              <span class="sp-val">ETB {hotelSpent.toLocaleString()}</span>
            </div>

            <div class="spending-box">
              <span class="sp-lbl"><Icon name="car" size={14} color="#3B82F6" /> Car Rentals</span>
              <span class="sp-qty">{carBookings.length} Rented</span>
              <span class="sp-val">ETB {carSpent.toLocaleString()}</span>
            </div>

            <div class="spending-box">
              <span class="sp-lbl"><Icon name="ticket" size={14} color="#E11D48" /> Events</span>
              <span class="sp-qty">{eventBookings.length} Passes</span>
              <span class="sp-val">ETB {eventSpent.toLocaleString()}</span>
            </div>

            <div class="spending-box">
              <span class="sp-lbl"><Icon name="bag" size={14} color="#10B981" /> Shop Products</span>
              <span class="sp-qty">{storeBookings.length} Items</span>
              <span class="sp-val">ETB {storeSpent.toLocaleString()}</span>
            </div>

            <div class="spending-box total-box">
              <span class="sp-lbl">Total Spending</span>
              <span class="sp-qty">All Services</span>
              <span class="sp-val total-val">ETB {totalSpentLifetime.toLocaleString()}</span>
            </div>
          </div>
        </div>

        <div class="profile-tabs-header">
          <button 
            class="profile-sub-tab {profileSubTab === 'BOOKINGS' ? 'active' : ''}" 
            on:click={() => profileSubTab = 'BOOKINGS'}>
            <Icon name="book" size={16} /> My Reservation Booklet ({customerBookings.length})
          </button>
          
          <button 
            class="profile-sub-tab {profileSubTab === 'FAVORITES' ? 'active' : ''}" 
            on:click={() => profileSubTab = 'FAVORITES'}>
            <Icon name="heart-filled" size={16} color="#ef4444" /> Saved Favorites ({favoritedListings.length})
          </button>
        </div>

        {#if profileSubTab === 'BOOKINGS'}
          <div class="passport-booklet-container">
            {#if customerBookings.length === 0}
              <div class="marketplace-card empty-state-box">
                <Icon name="calendar" size={36} color="var(--text-muted)" />
                <h3>Your reservation booklet is empty</h3>
                <p>Book stays, 4x4 vehicles, or cultural experiences across Ethiopia to fill your booklet!</p>
                <button class="btn-gold" on:click={() => goToCategory('ALL')}>{currentLanguage === 'am' ? 'የገበያ ቦታውን ያስሱ' : 'Explore Marketplace'}</button>
              </div>
            {:else}
              <div class="booklet-pages-list">
                {#each customerBookings as b, idx}
                  <div 
                    class="passport-page-spread marketplace-card" 
                    role="button"
                    tabindex="0"
                    on:click={() => selectedBookingPass = b}
                    on:keydown={(e) => e.key === 'Enter' && (selectedBookingPass = b)}>
                    
                    <div class="page-left">
                      <div class="page-header-stamp">
                        <span class="stamp-number">PAGE 0{idx + 1}</span>
                        <span class={b.providerStatus === 'DECLINED' || b.status === 'DECLINED' ? 'badge-warning' : (b.providerStatus === 'ACCEPTED' ? 'badge-verified' : 'badge-warning')}>
                          {b.providerStatus === 'ACCEPTED' ? '● PROVIDER ACCEPTED' : (b.providerStatus === 'DECLINED' ? '● PROVIDER DECLINED' : '● AWAITING PROVIDER')}
                        </span>
                      </div>

                      <img src={b.imageUrl} alt={b.listingTitle} class="page-thumb-img" />
                      
                      <div class="page-info-box">
                        <h3 class="page-title">{b.listingTitle}</h3>
                        <p class="page-location"><Icon name="mappin" size={13} color="var(--accent-gold)" /> {b.location}</p>
                        <p class="page-date">🗓️ Date: <strong>{b.startDate || b.bookingDate}</strong></p>
                        {#if b.category === 'CAR_RENTAL'}
                          <p class="page-date">🕘 <strong>{b.pickupTime}</strong> pickup · {b.driverOption === 'WITH_DRIVER' ? 'With driver' : 'Without driver'}</p>
                        {/if}
                      </div>
                    </div>

                    <div class="book-spine"></div>

                    <div class="page-right">
                      <div class="pass-stub-header">
                        <span class="stub-tag">ENTRY PASS STUB</span>
                        <span class="stub-tx">Tx: {b.id}</span>
                      </div>

                      <div class="mini-qr-preview">
                        <svg viewBox="0 0 100 100" width="70" height="70">
                          <rect x="0" y="0" width="30" height="30" fill="var(--text-main)" />
                          <rect x="5" y="5" width="20" height="20" fill="var(--bg-surface)" />
                          <rect x="10" y="10" width="10" height="10" fill="var(--text-main)" />

                          <rect x="70" y="0" width="30" height="30" fill="var(--text-main)" />
                          <rect x="75" y="5" width="20" height="20" fill="var(--bg-surface)" />
                          <rect x="80" y="10" width="10" height="10" fill="var(--text-main)" />

                          <rect x="0" y="70" width="30" height="30" fill="var(--text-main)" />
                          <rect x="5" y="75" width="20" height="20" fill="var(--bg-surface)" />
                          <rect x="10" y="80" width="10" height="10" fill="var(--text-main)" />
                          <rect x="40" y="40" width="20" height="20" fill="var(--accent-gold)" />
                        </svg>
                        <span class="tap-hint">Tap to Open Full Info</span>
                      </div>

                      <div class="stub-price-row">
                        <span class="qty-txt">Qty: {b.quantity}</span>
                        <span class="price-val">ETB {b.totalAmount.toLocaleString()}</span>
                      </div>

                      <button class="btn-gold open-pass-btn">
                        Open Booking Pass →
                      </button>
                    </div>
                  </div>
                {/each}
              </div>
            {/if}
          </div>

        {:else if profileSubTab === 'FAVORITES'}
          <div class="favorites-grid-container">
            {#if favoritedListings.length === 0}
              <div class="marketplace-card empty-state-box">
                <Icon name="heart" size={36} color="var(--text-muted)" />
                <h3>No saved favorites yet</h3>
                <p>Click the heart icon on any listing card to save your favorite stay, vehicle, or craft providers!</p>
                <button class="btn-gold" on:click={() => goToCategory('ALL')}>{currentLanguage === 'am' ? 'የገበያ ቦታውን ይመልከቱ' : 'Browse Marketplace'}</button>
              </div>
            {:else}
              <div class="listings-grid">
                {#each displayedFavoritedListings as listing}
                  <ListingCard 
                    {listing} 
                    currentCurrency={currency}
                    isFavorite={true} 
                    onToggleFavorite={toggleFavorite} 
                    on:select={(l) => openListing(l)}
                  />
                {/each}
              </div>
            {/if}
          </div>
        {/if}
      </section>

    {:else if activeTab === 'provider-how'}
      <section class="provider-guide animate-fade-in">
        <div class="provider-guide-hero">
          <div>
            <span class="eyebrow"><Icon name="sparkles" size={13} /> EASYSERVICE PROVIDER GUIDE</span>
            <h1>Grow your business with EasyService.</h1>
            <p>List your hotel, vehicle, event, experience, or products and connect with customers looking for trusted services across Ethiopia.</p>
            <button class="btn-gold" on:click={() => activeTab = 'provider'}>Become a Provider <span aria-hidden="true">→</span></button>
            <span class="guide-proof">First month FREE · Verified marketplace · Simple management</span>
          </div>
          <div class="guide-hero-mark"><span>01</span><strong>Start simple.</strong><small>Grow with confidence.</small></div>
        </div>

        <div class="guide-section guide-workflow">
          <div class="guide-section-heading"><span class="eyebrow">HOW EASYSERVICE WORKS</span><h2>Getting started is simple.</h2></div>
          <div class="workflow-grid">
            <article><span>01</span><h3>Create your provider profile</h3><p>Tell us about yourself and your business.</p><small>Full name · Business name · Phone · Email · Category · Location</small></article>
            <article><span>02</span><h3>Get verified</h3><p>Complete the required checks so customers know who they are booking with.</p><small>Identity · Business · Contact · Listing information</small><b>✓ Verified Provider</b></article>
            <article><span>03</span><h3>Create your listing</h3><p>Add photos, title, description, location, price, availability, capacity, and rules.</p><small>Stays · Drive · Experiences · Shop</small></article>
            <article><span>04</span><h3>Customers discover you</h3><p>Your listings appear throughout the marketplace.</p><strong>Search → Discover → Compare → Book</strong></article>
            <article><span>05</span><h3>Manage your business</h3><p>Use Provider Hub to manage listings, bookings, availability, customers, and transactions.</p></article>
          </div>
        </div>

        <div class="guide-free-month">
          <div><span class="eyebrow">YOUR FIRST MONTH IS FREE</span><h2>Start with EasyService at no cost.</h2><p>Your first 30 days are completely free while you create listings and connect with customers.</p></div>
          <div class="free-checks"><span>✓ Create your profile</span><span>✓ Complete verification</span><span>✓ Receive bookings</span><span>✓ Use Provider Hub</span></div>
          <button class="btn-gold" on:click={() => activeTab = 'provider'}>Start Your Free Month →</button>
        </div>

        <div class="guide-section">
          <div class="guide-section-heading"><span class="eyebrow">WHAT CAN YOU OFFER?</span><h2>Choose the service that fits your business.</h2></div>
          <div class="offer-grid"><article><span>🏨</span><h3>Hotels & Stays</h3><p>Showcase your hotel, resort, villa, or accommodation.</p></article><article><span>🚗</span><h3>Car Rentals</h3><p>Offer vehicles for customers traveling around Ethiopia.</p></article><article><span>🎟</span><h3>Events & Experiences</h3><p>Promote festivals, activities, and unique experiences.</p></article><article><span>🛍</span><h3>Store Products</h3><p>Sell authentic Ethiopian products and artisan goods.</p></article></div>
        </div>

        <div class="guide-two-column">
          <article class="guide-info-panel"><span class="eyebrow">PROVIDER GUIDELINES</span><h2>Keep your business information accurate.</h2><p>Customers rely on what you display. Keep prices, availability, photos, descriptions, location, and contact details current.</p><ul><li>Be honest about what you offer.</li><li>Honor confirmed bookings.</li><li>Communicate respectfully.</li><li>Respond to customer requests promptly.</li></ul></article>
          <article class="guide-info-panel"><span class="eyebrow">VERIFICATION & TRUST</span><h2>Built around trusted providers.</h2><p>Verified providers help customers make more confident decisions. The badge means you completed EasyService's required verification process.</p><div class="verified-callout">✓ Verified Provider</div><small>Verification does not guarantee every aspect of a provider's service.</small></article>
        </div>

        <div class="guide-hub-panel"><div><span class="eyebrow">YOUR PROVIDER HUB</span><h2>Everything you need in one place.</h2><p>See activity at a glance and manage every part of your marketplace business.</p></div><div class="hub-items"><span>Overview</span><span>Listings</span><span>Bookings</span><span>Availability</span><span>Transactions</span><span>Profile</span></div></div>

        <div class="guide-faq guide-section"><div class="guide-section-heading"><span class="eyebrow">FREQUENTLY ASKED QUESTIONS</span><h2>Good questions. Clear answers.</h2></div><div class="faq-grid"><details><summary>Who can become an EasyService provider?</summary><p>Hotels, rental businesses, event organizers, experience providers, shops, and other eligible service businesses can apply.</p></details><details><summary>Does it cost anything to join?</summary><p>Your first month is free, with no setup fee or listing fee.</p></details><details><summary>Can I create multiple listings?</summary><p>Yes, providers can create multiple listings where supported.</p></details><details><summary>Do I need to be verified?</summary><p>Yes. Provider verification is part of the marketplace trust system.</p></details><details><summary>Can I update availability?</summary><p>Yes. Keep availability accurate through Provider Hub to prevent booking conflicts.</p></details><details><summary>Can I stop being a provider?</summary><p>Yes. Provider accounts can be managed according to platform policies.</p></details></div></div>

        <div class="guide-final-cta"><span class="eyebrow">READY TO JOIN EASYSERVICE?</span><h2>Your business belongs here.</h2><p>Whether you run a hotel in Bishoftu, a rental fleet in Addis Ababa, an experience in Lalibela, or an artisan shop, EasyService gives your business a place to be discovered.</p><button class="btn-gold" on:click={() => activeTab = 'provider'}>Become a Provider →</button><strong>First month FREE</strong></div>
      </section>

    {:else if activeTab === 'provider'}
      <!-- Provider Onboarding & Management Workspace -->
      <section class="provider-workspace animate-fade-in">
        {#if !isCompanyRegistered}
          <!-- Step 1: Provider Company Onboarding Form -->
          <div class="marketplace-card provider-onboard-card">
            <div class="onboard-header">
              <span class="badge-verified">✓ FAYDA VERIFIED PROVIDER NETWORK</span>
              <h2>🏢 Register Your Provider Business Company</h2>
              <p>Enter your business details to unlock your provider management dashboard and start publishing rooms, vehicles, or artisan items.</p>
            </div>

            <div class="form-grid">
              <div class="form-group">
                <label for="onboardCompanyName">Company / Business Name *</label>
                <input id="onboardCompanyName" type="text" placeholder="e.g. Kuriftu Resort & Spa" bind:value={companyName} class="input-field" />
              </div>

              <div class="form-group">
                <label for="onboardCategory">Primary Category *</label>
                <select id="onboardCategory" bind:value={companyCategory} class="input-field">
                  <option value="HOTEL">Hotels & Resorts</option>
                  <option value="CAR_RENTAL">Car Rentals & Fleets</option>
                  <option value="EVENT">Events & Passes</option>
                  <option value="STORE">Store Products & Crafts</option>
                </select>
              </div>

              <div class="form-group">
                <label for="onboardLocation">Location / City Spot *</label>
                <input id="onboardLocation" type="text" placeholder="e.g. Bishoftu Lakefront" bind:value={companyLocation} class="input-field" />
              </div>

              <div class="form-group">
                <label for="onboardPhone">Contact Phone *</label>
                <input id="onboardPhone" type="text" bind:value={companyPhone} class="input-field" />
              </div>
            </div>

            <button class="btn-gold onboard-btn" on:click={handleRegisterCompany} disabled={!companyName}>
              Register Company & Open Dashboard →
            </button>
          </div>
        {:else}
          <!-- Step 2: Active Provider Dashboard -->
          <div class="provider-dashboard-active">
            <div class="marketplace-card provider-dash-header">
              <div class="company-title-block">
                <div>
                  <span class="badge-verified">✓ ACTIVE PROVIDER</span>
                  <h2>{companyName}</h2>
                  <p class="company-sub"><Icon name="mappin" size={13} color="var(--accent-gold)" /> {companyLocation} • {companyCategory}</p>
                </div>
                
                <button class="btn-gold add-listing-btn" on:click={() => showAddListingModal = true}>
                  ➕ Add New Listing / Room / Variant
                </button>
              </div>

              <div class="provider-stats-row">
                <div class="stat-box">
                  <span class="stat-lbl">Active Options</span>
                  <span class="stat-val">{providerListingsForDashboard.length} Listings</span>
                </div>
                <div class="stat-box">
                  <span class="stat-lbl">Total Reservations</span>
                  <span class="stat-val">{providerBookings.length} Bookings</span>
                </div>
                <div class="stat-box">
                  <span class="stat-lbl">Simulated Revenue</span>
                  <span class="stat-val">ETB {providerRevenue.toLocaleString()}</span>
                </div>
              </div>
            </div>

            <section class="provider-bookings-panel">
              <div class="panel-heading-row">
                <div>
                  <span class="sub-pill">BOOKING CONTROL CENTER</span>
                  <h3>Customer bookings</h3>
                  <p class="subtext">Review requests, confirm availability, and keep your calendar up to date.</p>
                </div>
                <span class="booking-count-label">{providerBookings.length} booking{providerBookings.length === 1 ? '' : 's'}</span>
              </div>

              {#if providerBookings.length === 0}
                <div class="booking-empty"><Icon name="calendar" size={24} color="var(--accent-gold)" /><span>No customer bookings yet.</span><small>New reservations for your listings will appear here.</small></div>
              {:else}
                <div class="provider-booking-layout">
                  <div class="provider-booking-list">
                    {#each providerBookings as booking}
                      <article class="provider-booking-row">
                        <div class="booking-date-tile"><strong>{booking.startDate || '—'}</strong><small>{booking.category === 'CAR_RENTAL' ? `${booking.pickupTime || '09:00'} pickup` : 'scheduled date'}</small></div>
                        <div class="booking-row-main">
                          <strong>{booking.listingTitle}</strong>
                          <span>Booked by <strong>{booking.customerName || 'Customer'}</strong> · {booking.quantity} unit(s) · {booking.category}</span>
                          <small>{booking.startDate || 'Date pending'}{booking.endDate && booking.endDate !== booking.startDate ? ` → ${booking.endDate}` : ''} · {booking.totalAmount ? `ETB ${Number(booking.totalAmount).toLocaleString()}` : 'amount pending'}</small>
                          {#if booking.category === 'CAR_RENTAL'}<small>{booking.driverOption === 'WITH_DRIVER' ? 'With driver' : 'Without driver'} · return {booking.returnTime || '18:00'}</small>{/if}
                        </div>
                        <div class="booking-row-actions">
                          <span class="booking-status {booking.providerStatus?.toLowerCase() || 'pending'}">{booking.providerStatus || 'PENDING'}</span>
                          {#if !booking.providerStatus || booking.providerStatus === 'PENDING'}
                            <button class="booking-accept" on:click={() => handleProviderStatus(booking.id, 'ACCEPTED')}>Accept</button>
                            <button class="booking-decline" on:click={() => handleProviderStatus(booking.id, 'DECLINED')}>Decline</button>
                          {:else if booking.status === 'CONFIRMED'}
                            <select class="input-field provider-cancel-reason" bind:value={providerCancellationReasons[booking.id]} aria-label="Provider cancellation reason">
                              <option value="Provider unavailable">Provider unavailable</option>
                              <option value="Inventory or schedule conflict">Inventory or schedule conflict</option>
                              <option value="Maintenance or closure">Maintenance or closure</option>
                              <option value="Customer request">Customer request</option>
                              <option value="Safety or weather concern">Safety or weather concern</option>
                            </select>
                            <button class="booking-decline" on:click={() => handleProviderStatus(booking.id, 'CANCELLED', providerCancellationReasons[booking.id] || 'Provider unavailable')}>Cancel booking</button>
                            <button class="booking-accept" on:click={() => handleProviderStatus(booking.id, 'COMPLETED')}>Mark finished</button>
                          {/if}
                        </div>
                      </article>
                    {/each}
                  </div>
                  <aside class="provider-calendar">
                    <h4><Icon name="calendar" size={15} /> Booked dates</h4>
                    {#each Object.entries(providerCalendar) as [date, count]}
                      <div class="calendar-day"><span>{date}</span><strong>{count} booked</strong></div>
                    {/each}
                  </aside>
                </div>
              {/if}
            </section>

            {#if providerSuccessMsg}
              <div class="alert-success">{providerSuccessMsg}</div>
            {/if}

            <!-- Company Active Listings Table / Grid -->
            <div class="marketplace-card company-listings-card">
              <h3>Management Inventory ({companyName})</h3>
              <p class="subtext">Rooms, vehicle options, or items published by your business:</p>

              {#if providerListingsForDashboard.length === 0}
                <div class="inventory-empty">
                  <Icon name="bag" size={28} color="var(--accent-gold)" />
                  <strong>No listings published yet</strong>
                  <span>Create your first listing to see it here.</span>
                  <button class="btn-gold" on:click={() => showAddListingModal = true}>Add Your First Listing →</button>
                </div>
              {:else}
                <div class="provider-listings-grid">
                  {#each providerListingsForDashboard as listing, index}
                    {@const listingBookings = getListingBookings($userBookings, listing.id)}
                    <article class="provider-listing-card">
                      <div class="provider-listing-number">{String(index + 1).padStart(2, '0')}</div>
                      <img src={listing.imageUrl} alt={listing.title} class="provider-listing-image" />
                      <div class="provider-listing-body">
                        <div class="provider-listing-meta"><span>{listing.category}</span><span class="badge-verified">PUBLISHED</span></div>
                        <h4>{listing.title}</h4>
                        <p>{listing.description}</p>
                        <div class="provider-listing-footer"><strong>ETB {listing.price.toLocaleString()}</strong><span>{listing.availableQuantity} available</span></div>
                        <button class="btn-outline" on:click={() => editProviderListing(listing)}>Edit listing details</button>
                        <div class="listing-booking-summary">
                          <div class="listing-booking-heading"><strong>{listingBookings.length} booking{listingBookings.length === 1 ? '' : 's'}</strong><span>Reservation history</span></div>
                          {#if listingBookings.length === 0}
                            <small class="no-listing-bookings">No bookings yet.</small>
                          {:else}
                            <div class="listing-booking-history">
                              {#each listingBookings as lb, lbIndex}
                                <div class="listing-booking-item">
                                  <span class="listing-booking-index">#{lbIndex + 1}</span>
                                  <div><strong>{lb.customerName || 'Customer'}</strong><small>{lb.startDate || 'Date pending'}{lb.endDate && lb.endDate !== lb.startDate ? ` → ${lb.endDate}` : ''} · {lb.bookingDate || 'recent'}</small></div>
                                  <span class="listing-booking-status">{lb.providerStatus || lb.status}</span>
                                </div>
                                {#if lb.rating}
                                  <div class="listing-review-item"><strong>{'★'.repeat(lb.rating)}{'☆'.repeat(5 - lb.rating)}</strong><span>{lb.reviewText || 'Customer left a rating.'}</span></div>
                                {/if}
                              {/each}
                            </div>
                          {/if}
                        </div>
                      </div>
                    </article>
                  {/each}
                </div>
              {/if}
            </div>
          </div>
        {/if}
      </section>
    {/if}
  </div>

  <!-- Provider Add Listing Modal -->
  {#if showAddListingModal}
    <!-- svelte-ignore a11y-no-noninteractive-element-interactions -->
    <!-- svelte-ignore a11y-click-events-have-key-events -->
    <div class="modal-backdrop" on:click|self={() => showAddListingModal = false} role="dialog" aria-modal="true">
      <div class="marketplace-card modal-content add-modal animate-fade-in">
        <div class="modal-header">
          <h2>➕ Add New Listing / Room Variant ({companyName})</h2>
          <button class="close-btn" on:click={() => showAddListingModal = false}>✕</button>
        </div>

        <div class="form-grid">
          <div class="form-group">
            <label for="modalListingTitle">Listing / Room / Option Name *</label>
            <input id="modalListingTitle" type="text" placeholder="e.g. Master Presidential Suite" bind:value={newTitle} class="input-field" />
          </div>

          {#if companyCategory === 'HOTEL'}
            <div class="form-group">
              <label for="modalAccommodationType">Accommodation Type *</label>
              <select id="modalAccommodationType" bind:value={newAccommodationType} class="input-field">
                {#each accommodationTypes as type}<option value={type}>{type}</option>{/each}
              </select>
            </div>
            <div class="form-group">
              <label for="modalRoomType">Room / Unit Type *</label>
              <select id="modalRoomType" bind:value={newRoomType} class="input-field">
                {#each roomTypes as type}<option value={type}>{type}</option>{/each}
              </select>
            </div>
          {:else if companyCategory === 'CAR_RENTAL'}
            <div class="form-group">
              <label for="modalVehicleType">Vehicle Type *</label>
              <select id="modalVehicleType" bind:value={newVehicleType} class="input-field">
                {#each vehicleTypes as type}<option value={type}>{type}</option>{/each}
              </select>
            </div>
            <div class="form-group">
              <label for="modalVehicleBrand">Vehicle Brand *</label>
              <select id="modalVehicleBrand" bind:value={newVehicleBrand} class="input-field">
                {#each vehicleBrands as brand}<option value={brand}>{brand}</option>{/each}
              </select>
            </div>
            <div class="form-group">
              <label for="modalVehicleModel">Vehicle Model *</label>
              <select id="modalVehicleModel" bind:value={newVehicleModel} class="input-field">
                {#each availableVehicleModels as model}<option value={model}>{model}</option>{/each}
              </select>
            </div>
          {:else if companyCategory === 'STORE'}
            <div class="form-group">
              <label for="modalShopCategory">Shop Category *</label>
              <select id="modalShopCategory" bind:value={newShopCategory} class="input-field">
                {#each shopCategories as category}<option value={category}>{category}</option>{/each}
              </select>
            </div>
          {:else if companyCategory === 'EVENT'}
            <div class="form-group">
              <label for="modalEventCategory">Event Category *</label>
              <select id="modalEventCategory" bind:value={newEventCategory} class="input-field">
                {#each eventCategories as category}<option value={category}>{category}</option>{/each}
              </select>
            </div>
            <div class="form-group">
              <label for="modalEventDate">Event Date *</label>
              <input id="modalEventDate" type="date" bind:value={newEventDate} class="input-field" />
            </div>
            <div class="form-group">
              <label for="modalEventStartTime">Start Time</label>
              <input id="modalEventStartTime" type="time" bind:value={newEventStartTime} class="input-field" />
            </div>
            <div class="form-group">
              <label for="modalEventEndTime">End Time</label>
              <input id="modalEventEndTime" type="time" bind:value={newEventEndTime} class="input-field" />
            </div>
          {/if}

          <div class="form-group">
            <label for="modalListingPrice">Price (ETB) *</label>
            <input id="modalListingPrice" type="number" bind:value={newPrice} class="input-field" />
          </div>

          <div class="form-group">
            <label for="modalListingStock">Available Stock / Quantity *</label>
            <input id="modalListingStock" type="number" min="1" step="1" bind:value={newCapacity} class="input-field" />
          </div>
        </div>

        <div class="form-group">
          <label for="modalListingImage">Picture Image URL</label>
          <input id="modalListingImage" type="text" bind:value={newImage} class="input-field" />
          <label for="modalListingImageFile">Or upload an image</label>
          <input id="modalListingImageFile" type="file" accept="image/*" on:change={handleListingImageUpload} class="input-field" />
        </div>

        <div class="form-group">
          <label for="modalListingDesc">Description</label>
          <textarea id="modalListingDesc" rows="2" placeholder="Describe the room, vehicle, or product details..." bind:value={newDesc} class="input-field"></textarea>
        </div>

        {#if companyCategory === 'HOTEL'}
          <div class="form-grid">
            <div class="form-group amenity-options"><span class="form-label">Amenities</span><label><input type="checkbox" bind:checked={amenityWifi} /> Wi-Fi</label><label><input type="checkbox" bind:checked={amenityParking} /> Parking</label><label><input type="checkbox" bind:checked={amenityRestaurant} /> Restaurant</label><label><input type="checkbox" bind:checked={amenityPool} /> Swimming pool</label><label><input type="checkbox" bind:checked={amenitySpa} /> Spa & wellness</label><label><input type="checkbox" bind:checked={amenityGym} /> Gym</label></div>
            <div class="form-group"><label for="modalCancellation">Cancellation policy</label><input id="modalCancellation" type="text" bind:value={newCancellationPolicy} class="input-field" /></div>
            <div class="form-group"><label for="modalCheckIn">Check-in</label><input id="modalCheckIn" type="time" bind:value={newCheckIn} class="input-field" /></div>
            <div class="form-group"><label for="modalCheckOut">Check-out</label><input id="modalCheckOut" type="time" bind:value={newCheckOut} class="input-field" /></div>
          </div>
        {/if}

        <div class="modal-actions">
          <button class="btn-outline" on:click={() => showAddListingModal = false}>Cancel</button>
          <button class="btn-gold" id="btnPublishListing" on:click={handleCreateProviderListing} disabled={!newTitle}>
            {editingListingId ? 'Save Listing Changes ✓' : 'Save & Publish Listing ✓'}
          </button>
        </div>
      </div>
    </div>
  {/if}

  <!-- Provider CTA and footer -->
  <section class="provider-cta">
    <div class="provider-cta-content">
      <span class="eyebrow"><Icon name="sparkles" size={12} /> {footerLabels.providerEyebrow}</span>
      <h2>{footerLabels.providerTitle} <Icon name="shield" size={22} color="var(--accent-gold)" /></h2>
      <p>{footerLabels.providerDesc}</p>

      <div class="provider-benefits">
        <div><span class="provider-benefit-icon"><Icon name="shield" size={17} /></span><span><strong>{footerLabels.verified}</strong><small>{footerLabels.reachCustomers}</small></span></div>
        <div><span class="provider-benefit-icon"><Icon name="sparkles" size={17} /></span><span><strong>{footerLabels.management}</strong><small>{footerLabels.managementDesc}</small></span></div>
        <div><span class="provider-benefit-icon"><Icon name="calendar" size={17} /></span><span><strong>{footerLabels.securePayments}</strong><small>{footerLabels.paymentsDesc}</small></span></div>
        <div><span class="provider-benefit-icon"><Icon name="user" size={17} /></span><span><strong>{footerLabels.growth}</strong><small>{footerLabels.growthDesc}</small></span></div>
      </div>

      <div class="provider-cta-actions">
        <button class="btn-gold" on:click={() => activeTab = 'provider'}><Icon name="user" size={16} /> {footerLabels.become} <span aria-hidden="true">→</span></button>
        <button class="btn-outline provider-how-btn" on:click={() => activeTab = 'provider-how'}><Icon name="sparkles" size={15} /> {footerLabels.how}</button>
      </div>
      <span class="provider-proof"><Icon name="shield" size={14} /> {footerLabels.free}</span>
    </div>
    <div class="provider-cta-visual">
      <img src="https://images.unsplash.com/photo-1556761175-b413da4baf72?auto=format&fit=crop&w=1100&q=85" alt="EasyService provider preparing products for customers" />
      <div class="provider-cta-stat"><span class="provider-stat-icon"><Icon name="user" size={18} /></span><strong>10,000+<small>{footerLabels.activeProviders}</small></strong><span>{footerLabels.growing}</span></div>
    </div>
  </section>

  <footer class="footer">
    <div class="footer-container">
      <div class="footer-col">
        <img class="footer-logo" src={currentTheme === 'dark' ? '/easyservice-logo-dark.svg' : '/easyservice-logo.svg'} alt="EasyService, purely simple" />
        <p class="footer-desc">{footerLabels.description}</p>
        <div class="footer-socials" aria-label="Social links">
          <button type="button" disabled title="Facebook profile coming soon" aria-label="Facebook profile coming soon"><Facebook size={15} strokeWidth={2} /></button><button type="button" disabled title="Instagram profile coming soon" aria-label="Instagram profile coming soon"><Instagram size={15} strokeWidth={2} /></button><button type="button" disabled title="X profile coming soon" aria-label="X profile coming soon"><Twitter size={15} strokeWidth={2} /></button><button type="button" disabled title="YouTube channel coming soon" aria-label="YouTube channel coming soon"><Youtube size={15} strokeWidth={2} /></button>
        </div>
      </div>

      <div class="footer-col">
        <h4>{footerLabels.discover}</h4>
        <ul>
          <li><a href="#explore" on:click|preventDefault={() => navigateToSitePage('explore')}>{footerLabels.explore}</a></li><li><a href="#stays" on:click|preventDefault={() => navigateToSitePage('stays')}>{footerLabels.stays}</a></li><li><a href="#drive" on:click|preventDefault={() => navigateToSitePage('drive')}>{footerLabels.drive}</a></li><li><a href="#experiences" on:click|preventDefault={() => navigateToSitePage('experiences')}>{footerLabels.experiences}</a></li><li><a href="#shop" on:click|preventDefault={() => navigateToSitePage('shop')}>{footerLabels.shop}</a></li>
        </ul>
      </div>

      <div class="footer-col">
        <h4>{footerLabels.customers}</h4>
        <ul>
          <li><a href="#bookings" on:click|preventDefault={() => navigateToSitePage('bookings')}>{footerLabels.bookings}</a></li><li><a href="#passport" on:click|preventDefault={() => navigateToSitePage('passport')}>{footerLabels.passport}</a></li><li><a href="#wallet" on:click|preventDefault={() => navigateToSitePage('wallet')}>{footerLabels.wallet}</a></li><li><a href="#help" on:click|preventDefault={() => navigateToSitePage('help')}>{footerLabels.help}</a></li>
        </ul>
      </div>

      <div class="footer-col">
        <h4>{footerLabels.providers}</h4>
        <ul>
          <li><a href="#become-provider" on:click|preventDefault={() => navigateToSitePage('become-provider')}>{footerLabels.become}</a></li><li><a href="#provider" on:click|preventDefault={() => navigateToSitePage('provider')}>{footerLabels.providerHub}</a></li><li><a href="#list-service" on:click|preventDefault={() => navigateToSitePage('list-service')}>{footerLabels.listService}</a></li><li><a href="#provider-support" on:click|preventDefault={() => navigateToSitePage('provider-support')}>{footerLabels.support}</a></li>
        </ul>
      </div>

      <div class="footer-col">
        <h4>{footerLabels.destinations}</h4>
        <ul>
          <li><a href="#addis" on:click|preventDefault={() => navigateToSitePage('addis')}>{currentLanguage === 'am' ? 'አዲስ አበባ' : 'Addis Ababa'}</a></li><li><a href="#bishoftu" on:click|preventDefault={() => navigateToSitePage('bishoftu')}>{currentLanguage === 'am' ? 'ቢሾፍቱ' : 'Bishoftu'}</a></li><li><a href="#hawassa" on:click|preventDefault={() => navigateToSitePage('hawassa')}>{currentLanguage === 'am' ? 'ሐዋሳ' : 'Hawassa'}</a></li><li><a href="#lalibela" on:click|preventDefault={() => navigateToSitePage('lalibela')}>{currentLanguage === 'am' ? 'ላሊበላ' : 'Lalibela'}</a></li><li><a href="#bahir-dar" on:click|preventDefault={() => navigateToSitePage('bahir-dar')}>{currentLanguage === 'am' ? 'ባሕር ዳር' : 'Bahir Dar'}</a></li>
        </ul>
      </div>

      <div class="footer-col">
        <h4>{footerLabels.company}</h4>
        <ul>
          <li><a href="#about" on:click|preventDefault={() => navigateToSitePage('about')}>{footerLabels.about}</a></li><li><a href="#how-it-works" on:click|preventDefault={() => navigateToSitePage('how-it-works')}>{footerLabels.how}</a></li><li><a href="#trust" on:click|preventDefault={() => navigateToSitePage('trust')}>{footerLabels.trust}</a></li><li><a href="#terms" on:click|preventDefault={() => navigateToSitePage('terms')}>{footerLabels.terms}</a></li><li><a href="#privacy" on:click|preventDefault={() => navigateToSitePage('privacy')}>{footerLabels.privacy}</a></li>
        </ul>
      </div>

      <div class="footer-connect">
        <h4>{footerLabels.stayConnected}</h4>
        <p>{footerLabels.deals}</p>
        <form class="subscribe-form" on:submit|preventDefault={handleNewsletterSubmit}>
          <label class="sr-only" for="footerEmail">Email address</label>
          <input id="footerEmail" name="email" type="email" placeholder={footerLabels.email} required />
          <button type="submit" aria-label={footerLabels.subscribe}>→</button>
        </form>
        {#if newsletterMessage}<small class="footer-message">{newsletterMessage}</small>{/if}
        <span class="app-label">{footerLabels.app}</span>
        <div class="app-buttons"><button type="button" disabled title="Google Play app coming soon">Google Play</button><button type="button" disabled title="App Store app coming soon">App Store</button></div>
      </div>
    </div>

    <div class="footer-trust">
      <div><Icon name="shield" size={21} /><span><strong>{footerLabels.secure}</strong><small>{footerLabels.secureDesc}</small></span></div>
      <div><Icon name="calendar" size={21} /><span><strong>{footerLabels.securePayments}</strong><small>{footerLabels.paymentsDesc}</small></span></div>
      <div><Icon name="bell" size={21} /><span><strong>{footerLabels.support24}</strong><small>{footerLabels.supportDesc}</small></span></div>
      <div><Icon name="heart" size={21} /><span><strong>{footerLabels.made}</strong><small>{footerLabels.growing}</small></span></div>
    </div>

    <div class="footer-bottom">
      <span>© 2026 EasyService Marketplace Inc. {currentLanguage === 'am' ? 'መብቱ በሙሉ የተጠበቀ ነው።' : 'All rights reserved.'}</span><span><a href="#about" on:click|preventDefault={() => navigateToSitePage('about')}>{footerLabels.about}</a> · <a href="#how-it-works" on:click|preventDefault={() => navigateToSitePage('how-it-works')}>{footerLabels.how}</a> · <a href="#trust" on:click|preventDefault={() => navigateToSitePage('trust')}>{footerLabels.trust}</a> · <a href="#terms" on:click|preventDefault={() => navigateToSitePage('terms')}>{footerLabels.terms}</a> · <a href="#privacy" on:click|preventDefault={() => navigateToSitePage('privacy')}>{footerLabels.privacy}</a></span><span><select value={currentLanguage} on:change={(event) => { currentLanguage = event.currentTarget.value; localStorage.setItem('easyservice_language', currentLanguage); }} aria-label="Language"><option value="en">English</option><option value="am">አማርኛ</option></select>&nbsp;·&nbsp;<select bind:value={currency} on:change={() => localStorage.setItem('easyservice_currency', currency)} aria-label="Currency"><option>ETB</option><option>USD</option><option>EUR</option><option>GBP</option></select></span>
    </div>
  </footer>

  <!-- Modals -->
  <BookingModal 
    listing={selectedListing} 
    currentLanguage={currentLanguage}
    onClose={() => selectedListing = null} 
    onBookingSuccess={(listingId, qty, variantId) => {
      persistProviderInventory(listingId, qty, variantId);
      listings = listings.map(l => l.id === listingId ? { ...l, availableQuantity: Math.max(0, l.availableQuantity - qty) } : l);
      if (selectedProviderListing && selectedProviderListing.id === listingId) {
        selectedProviderListing = { ...selectedProviderListing, availableQuantity: Math.max(0, selectedProviderListing.availableQuantity - qty) };
      }
      void refreshCustomerListings();
    }} 
  />
  <BookingDetailsModal booking={selectedBookingPass} currentLanguage={currentLanguage} onClose={() => selectedBookingPass = null} onCancel={handleCancelBooking} onComplete={handleCompleteBooking} onReview={handleBookingReview} />
  <SpinWheelModal show={showSpinModal} currentLanguage={currentLanguage} onClose={() => showSpinModal = false} />
  <EasyToolsModal show={showToolsModal} currentLanguage={currentLanguage} onClose={() => showToolsModal = false} />
  <RegisterModal show={showRegisterModal} onClose={() => showRegisterModal = false} />
  
  {#if !sitePage}
    <LoginModal show={!$currentUser} />
  {/if}
</main>

<style>
  .app-container {
    min-height: 100vh;
    display: flex;
    flex-direction: column;
    width: 100%;
    position: relative;
  }

  .category-transition-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;
    background: rgba(11, 19, 32, 0.85);
    backdrop-filter: blur(10px);
    z-index: 1000;
    display: flex;
    justify-content: center;
    align-items: center;
    pointer-events: none;
  }

  .moving-vehicle {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
    color: #ffffff;
    font-weight: 800;
    font-size: 1.2rem;
    animation: vehiclePass 0.7s cubic-bezier(0.2, 0.8, 0.2, 1) forwards;
  }

  @keyframes vehiclePass {
    0% { transform: translateX(-100vw) scale(0.8); opacity: 0; }
    50% { transform: translateX(0) scale(1.1); opacity: 1; }
    100% { transform: translateX(100vw) scale(0.8); opacity: 0; }
  }

  .main-content {
    width: 100%;
    margin: 0 auto;
    padding: 0 32px 48px;
    flex-grow: 1;
  }

  .hero-section {
    position: relative;
    border-radius: 0;
    overflow: hidden;
    margin-bottom: 42px;
    margin-left: -32px;
    margin-right: -32px;
    width: calc(100% + 64px);
    min-height: 660px;
    padding: 68px 32px 48px;
    border: 0;
    transition: all 0.4s ease;
  }

  .hero-bg-wrapper {
    position: absolute;
    inset: 0;
    z-index: 1;
  }

  .hero-bg-img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    object-position: center center;
    display: block;
    transform: scale(1.02);
    transition: transform 0.6s ease;
  }

  .hero-overlay {
    position: absolute;
    inset: 0;
    z-index: 1;
    pointer-events: none;

    background:
      linear-gradient(
        180deg,
        rgba(4, 24, 47, 0.08) 0%,
        rgba(4, 24, 47, 0.18) 46%,
        rgba(4, 24, 47, 0.48) 100%
      );
  }

  .hero-content-box {
    position: relative;
    z-index: 2;
    max-width: 1060px;
    margin: 0 auto;
    text-align: center;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 16px;
  }

  .hero-pill-badge {
    display: inline-flex;
    align-items: center;
    justify-content: center;

    padding: 9px 18px;

    border: 1px solid var(--accent-gold, #d4af37);
    border-radius: 999px;

    background: rgba(11, 42, 76, 0.92);

    color: var(--accent-gold, #d4af37);

    font-size: 0.78rem;
    font-weight: 900;
    letter-spacing: 0.1em;
    text-transform: uppercase;

    box-shadow:
      0 6px 18px rgba(0, 0, 0, 0.22),
      inset 0 0 0 1px rgba(255, 255, 255, 0.05);

    backdrop-filter: blur(6px);

    position: relative;
    z-index: 3;
  }

  .hero-headline {
    max-width: 920px;
    font-size: clamp(2.5rem, 4.2vw, 3.45rem);
    font-weight: 900;
    color: #ffffff;
    line-height: 1.08;
    letter-spacing: -0.02em;
  }

  .hero-subtext {
    font-size: 1.08rem;
    color: #E2DDD5;
    max-width: 720px;
  }

  .category-experience-tabs {
    display: flex;
    gap: 8px;
    margin-top: 12px;
    background: rgba(8, 35, 66, 0.9);
    backdrop-filter: blur(14px);
    padding: 6px;
    border-radius: 9999px;
    border: 1px solid rgba(255, 255, 255, 0.18);
    box-shadow: 0 10px 28px rgba(2, 18, 38, 0.26);
  }

  .experience-tab {
    background: transparent;
    border: none;
    color: #E2DDD5;
    font-weight: 700;
    font-size: 0.84rem;
    padding: 9px 17px;
    border-radius: 9999px;
    cursor: pointer;
    transition: all 0.25s ease;
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .experience-tab:hover, .experience-tab.active {
    background: var(--accent-gold);
    color: #FFFFFF;
    box-shadow: 0 4px 14px rgba(200, 155, 60, 0.4);
  }

  .search-module {
    margin-top: 18px;
    background: var(--bg-surface);
    padding: 17px 22px;
    display: flex;
    align-items: center;
    gap: 18px;
    width: 100%;
    box-shadow: 0 18px 40px rgba(3, 24, 45, 0.28);
    border-radius: 16px;
  }

  .search-field {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 4px;
    flex-grow: 1;
  }

  .search-field label {
    font-size: 0.7rem;
    font-weight: 800;
    color: var(--text-muted);
    text-transform: uppercase;
  }

  .input-with-icon {
    display: flex;
    align-items: center;
    gap: 6px;
    width: 100%;
  }

  .search-select, .search-text-input {
    background: transparent;
    border: none;
    font-size: 0.92rem;
    font-weight: 600;
    color: var(--text-main);
    width: 100%;
    outline: none;
  }

  .search-divider {
    width: 1px;
    height: 36px;
    background: var(--border-subtle);
  }

  .search-btn {
    padding: 12px 24px;
    font-size: 0.95rem;
    white-space: nowrap;
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .location-chips-row {
    display: flex;
    align-items: center;
    gap: 8px;
    margin-top: 10px;
    flex-wrap: wrap;
    justify-content: center;
  }

  .chips-label {
    font-size: 0.78rem;
    color: #9CA3AF;
    font-weight: 600;
  }

  .location-chip {
    background: rgba(255, 255, 255, 0.08);
    border: 1px solid rgba(255, 255, 255, 0.15);
    color: #E2DDD5;
    font-size: 0.78rem;
    font-weight: 600;
    padding: 4px 12px;
    border-radius: 9999px;
    cursor: pointer;
    transition: all 0.2s ease;
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .location-chip:hover, .location-chip.active {
    background: var(--accent-gold);
    color: #fff;
    border-color: var(--accent-gold);
  }

  .discovery-curated-section {
    margin-bottom: 40px;
  }

  .section-header-row {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-bottom: 20px;
  }

  .sub-pill {
    font-size: 0.7rem;
    font-weight: 800;
    color: var(--accent-gold);
    text-transform: uppercase;
    letter-spacing: 0.08em;
  }

  .section-title {
    font-size: 1.6rem;
    font-weight: 800;
    color: var(--text-main);
  }

  .see-all-btn {
    padding: 8px 16px;
    font-size: 0.85rem;
  }

  .trending-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
    gap: 24px;
  }

  .marketplace-browsing-section {
    display: grid;
    grid-template-columns: 260px 1fr;
    gap: 28px;
    margin-bottom: 48px;
  }

  .filters-sidebar {
    padding: 20px;
    display: flex;
    flex-direction: column;
    gap: 18px;
    height: fit-content;
  }

  .filter-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .filter-header h3 {
    font-size: 1rem;
    font-weight: 800;
  }

  .clear-btn {
    background: transparent;
    border: none;
    color: var(--accent-terracotta);
    font-size: 0.78rem;
    font-weight: 700;
    cursor: pointer;
  }

  .filter-label {
    font-size: 0.75rem;
    font-weight: 700;
    color: var(--text-muted);
    text-transform: uppercase;
  }

  .listings-main-area {
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .sort-header-row {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
  }

  .browse-title {
    font-size: 1.5rem;
    font-weight: 800;
    color: var(--text-main);
  }

  .results-count {
    font-size: 0.85rem;
    color: var(--text-muted);
  }

  .sort-controls {
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .sort-label {
    font-size: 0.8rem;
    font-weight: 600;
    color: var(--text-muted);
  }

  .sort-select {
    padding: 6px 12px;
    font-size: 0.85rem;
  }

  .listings-grid {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 24px;
  }

  .empty-state-box {
    padding: 48px;
    text-align: center;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 12px;
  }

  /* Customer Profile Analytics & Booklet */
  .passport-dashboard {
    display: flex;
    flex-direction: column;
    gap: 24px;
  }

  .profile-summary-card {
    padding: 24px 32px;
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .profile-main-meta {
    display: flex;
    align-items: center;
    gap: 16px;
  }

  .avatar-large {
    width: 56px;
    height: 56px;
    background: var(--accent-gold);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .user-title-row {
    display: flex;
    align-items: center;
    gap: 10px;
  }

  .user-title-row h2 {
    font-size: 1.5rem;
    font-weight: 900;
    color: var(--text-main);
  }

  .user-sub-email {
    font-size: 0.85rem;
    color: var(--text-muted);
  }

  .spending-analytics-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
    gap: 14px;
  }

  .spending-box {
    background: var(--bg-surface-secondary);
    border: 1px solid var(--border-subtle);
    padding: 14px 16px;
    border-radius: var(--radius-md);
    display: flex;
    flex-direction: column;
    gap: 4px;
  }

  .spending-box.total-box {
    border-color: var(--accent-gold);
    background: var(--accent-gold-light);
  }

  .sp-lbl {
    font-size: 0.76rem;
    font-weight: 800;
    color: var(--text-muted);
    display: flex;
    align-items: center;
    gap: 6px;
  }

  .sp-qty {
    font-size: 0.72rem;
    color: var(--text-muted);
  }

  .sp-val {
    font-size: 1.15rem;
    font-weight: 900;
    color: var(--text-main);
    margin-top: 2px;
  }

  .total-val {
    color: var(--accent-gold);
  }

  .profile-tabs-header {
    display: flex;
    gap: 12px;
    border-bottom: 2px solid var(--border-subtle);
    padding-bottom: 8px;
  }

  .profile-sub-tab {
    background: transparent;
    border: none;
    font-size: 0.95rem;
    font-weight: 800;
    color: var(--text-muted);
    padding: 8px 16px;
    border-radius: var(--radius-md);
    cursor: pointer;
    transition: all 0.2s ease;
    display: flex;
    align-items: center;
    gap: 8px;
  }

  .profile-sub-tab:hover, .profile-sub-tab.active {
    color: var(--text-main);
    background: var(--bg-surface-secondary);
  }

  .booklet-pages-list {
    display: flex;
    flex-direction: column;
    gap: 24px;
  }

  .passport-page-spread {
    display: grid;
    grid-template-columns: 1fr 12px 1fr;
    cursor: pointer;
    position: relative;
    border: 2px solid var(--border-subtle);
    background: var(--bg-surface);
  }

  .passport-page-spread:hover {
    border-color: var(--accent-gold);
    transform: translateY(-4px);
  }

  .page-left {
    padding: 24px;
    display: flex;
    flex-direction: column;
    gap: 12px;
  }

  .page-header-stamp {
    display: flex;
    justify-content: space-between;
    align-items: center;
  }

  .stamp-number {
    font-size: 0.72rem;
    font-weight: 900;
    color: var(--accent-gold);
    letter-spacing: 0.08em;
  }

  .page-thumb-img {
    width: 100%;
    height: 140px;
    object-fit: cover;
    border-radius: var(--radius-md);
  }

  .page-title {
    font-size: 1.1rem;
    font-weight: 800;
  }

  .page-location {
    font-size: 0.85rem;
    color: var(--text-muted);
  }

  .page-date {
    font-size: 0.82rem;
    color: var(--text-main);
  }

  .book-spine {
    background: linear-gradient(90deg, rgba(0, 0, 0, 0.15) 0%, rgba(0, 0, 0, 0.02) 50%, rgba(0, 0, 0, 0.15) 100%);
    border-left: 1px dashed var(--border-subtle);
    border-right: 1px dashed var(--border-subtle);
  }

  .page-right {
    padding: 24px;
    display: flex;
    flex-direction: column;
    justify-content: space-between;
    align-items: center;
    background: var(--bg-surface-secondary);
    text-align: center;
  }

  .pass-stub-header {
    width: 100%;
    display: flex;
    justify-content: space-between;
    font-size: 0.72rem;
    font-weight: 800;
    color: var(--text-muted);
  }

  .mini-qr-preview {
    background: #ffffff;
    padding: 10px;
    border-radius: var(--radius-md);
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 4px;
    border: 1px solid var(--border-subtle);
    margin: 8px 0;
  }

  .tap-hint {
    font-size: 0.65rem;
    font-weight: 700;
    color: #111827;
  }

  .stub-price-row {
    display: flex;
    gap: 16px;
    align-items: center;
    font-size: 0.9rem;
  }

  .open-pass-btn {
    width: 100%;
    padding: 10px;
    font-size: 0.88rem;
  }

  /* Provider Management Styles */
  .provider-workspace {
    display: flex;
    flex-direction: column;
    gap: 24px;
  }

  .provider-onboard-card {
    padding: 32px;
    display: flex;
    flex-direction: column;
    gap: 20px;
    max-width: 680px;
    margin: 0 auto;
    width: 100%;
  }

  .onboard-header h2 {
    font-size: 1.5rem;
    font-weight: 900;
    color: var(--text-main);
    margin-top: 6px;
  }

  .onboard-header p {
    font-size: 0.9rem;
    color: var(--text-muted);
    margin-top: 4px;
  }

  .form-grid {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 16px;
  }

  .form-group {
    display: flex;
    flex-direction: column;
    gap: 6px;
  }

  .form-group label {
    font-size: 0.78rem;
    font-weight: 800;
    color: var(--text-muted);
  }

  .onboard-btn {
    padding: 12px 24px;
    font-size: 0.95rem;
  }

  .provider-dashboard-active {
    display: flex;
    flex-direction: column;
    gap: 24px;
  }

  .provider-dash-header {
    padding: 24px 32px;
    display: flex;
    flex-direction: column;
    gap: 20px;
  }

  .company-title-block {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
  }

  .company-title-block h2 {
    font-size: 1.6rem;
    font-weight: 900;
    color: var(--text-main);
    margin-top: 4px;
  }

  .company-sub {
    font-size: 0.85rem;
    color: var(--text-muted);
  }

  .add-listing-btn {
    padding: 10px 20px;
    font-size: 0.9rem;
  }

  .provider-stats-row {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
    gap: 16px;
  }

  .provider-bookings-panel {
    padding: 24px 32px;
    border: 1px solid var(--border-subtle);
    background: var(--bg-surface);
    border-radius: var(--radius-lg);
  }

  .panel-heading-row { display: flex; justify-content: space-between; align-items: flex-start; gap: 18px; }
  .panel-heading-row h3 { margin: 5px 0 3px; font-size: 1.15rem; }
  .booking-count-label { color: var(--accent-gold); font-size: .78rem; font-weight: 800; white-space: nowrap; }
  .booking-empty { min-height: 120px; display: flex; flex-direction: column; align-items: center; justify-content: center; gap: 5px; color: var(--text-muted); text-align: center; }
  .booking-empty small { font-size: .75rem; }
  .provider-booking-layout { display: grid; grid-template-columns: minmax(0, 1fr) 210px; gap: 20px; margin-top: 18px; }
  .provider-booking-list { display: flex; flex-direction: column; gap: 10px; }
  .provider-booking-row { display: grid; grid-template-columns: 100px minmax(0, 1fr) auto; gap: 13px; align-items: center; padding: 12px; border: 1px solid var(--border-subtle); border-radius: var(--radius-md); }
  .booking-date-tile { display: flex; flex-direction: column; gap: 3px; padding: 8px; background: var(--bg-surface-secondary); color: var(--text-main); font-size: .72rem; }
  .booking-date-tile strong { color: var(--accent-gold); overflow-wrap: anywhere; }
  .booking-date-tile small, .booking-row-main span, .booking-row-main small { color: var(--text-muted); font-size: .68rem; }
  .booking-row-main { min-width: 0; display: flex; flex-direction: column; gap: 4px; }
  .booking-row-main > strong { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
  .booking-row-actions { display: flex; align-items: center; gap: 6px; flex-wrap: wrap; justify-content: flex-end; }
  .booking-status { font-size: .65rem; font-weight: 900; padding: 4px 7px; background: var(--bg-surface-secondary); }
  .booking-status.accepted { color: var(--status-success-text); }
  .booking-status.declined { color: #b42318; }
  .booking-accept, .booking-decline { border: 0; padding: 6px 8px; border-radius: var(--radius-sm); font-size: .68rem; font-weight: 800; cursor: pointer; }
  .booking-accept { background: var(--status-success-bg); color: var(--status-success-text); }
  .booking-decline { background: #fff0ed; color: #b42318; }
  .provider-calendar { padding: 14px; background: var(--bg-surface-secondary); border-radius: var(--radius-md); }
  .provider-calendar h4 { display: flex; align-items: center; gap: 6px; font-size: .82rem; margin-bottom: 10px; }
  .calendar-day { display: flex; flex-direction: column; gap: 2px; padding: 8px 0; border-top: 1px solid var(--border-subtle); font-size: .72rem; }
  .calendar-day strong { color: var(--accent-gold); font-size: .68rem; }

  .stat-box {
    background: var(--bg-surface-secondary);
    border: 1px solid var(--border-subtle);
    padding: 14px 18px;
    border-radius: var(--radius-md);
    display: flex;
    flex-direction: column;
    gap: 2px;
  }

  .stat-lbl {
    font-size: 0.75rem;
    font-weight: 800;
    color: var(--text-muted);
  }

  .stat-val {
    font-size: 1.25rem;
    font-weight: 900;
    color: var(--text-main);
  }

  .company-listings-card {
    padding: 24px 32px;
  }

  .listings-table-wrapper {
    overflow-x: auto;
    margin-top: 14px;
  }

  .listings-table {
    width: 100%;
    border-collapse: collapse;
    font-size: 0.88rem;
  }

  :global(.listings-table th), :global(.listings-table td) {
    padding: 12px 16px;
    text-align: left;
    border-bottom: 1px solid var(--border-subtle);
  }

  :global(.listings-table th) {
    font-size: 0.75rem;
    font-weight: 800;
    color: var(--text-muted);
    text-transform: uppercase;
  }

  .table-thumb {
    width: 48px;
    height: 36px;
    object-fit: cover;
    border-radius: var(--radius-sm);
  }

  .stock-chip {
    background: rgba(16, 185, 129, 0.15);
    color: #10b981;
    font-weight: 800;
    padding: 2px 8px;
    border-radius: var(--radius-sm);
    font-size: 0.78rem;
  }

  .alert-success {
    background: var(--accent-gold-light);
    border: 1px solid var(--accent-gold);
    color: var(--text-main);
    padding: 12px 18px;
    border-radius: var(--radius-md);
    font-weight: 700;
    font-size: 0.88rem;
  }

  .add-modal {
    width: min(760px, calc(100% - 32px));
    max-height: min(820px, calc(100vh - 48px));
    overflow-y: auto;
    padding: 28px;
  }

  .provider-guide { max-width: 1280px; margin: 0 auto; }
  .provider-guide-hero { display: grid; grid-template-columns: minmax(0, 1.5fr) 280px; gap: 30px; align-items: center; min-height: 320px; padding: 48px clamp(24px, 5vw, 68px); background: linear-gradient(110deg, var(--bg-surface) 0%, var(--bg-surface-secondary) 100%); border: 1px solid var(--border-subtle); border-radius: var(--radius-lg); overflow: hidden; }
  .provider-guide-hero h1 { max-width: 660px; margin: 10px 0 12px; font-size: clamp(2rem, 4vw, 3.5rem); line-height: 1.08; }
  .provider-guide-hero p { max-width: 650px; color: var(--text-muted); font-size: 1rem; }
  .guide-proof { display: block; margin-top: 14px; color: var(--text-muted); font-size: .75rem; font-weight: 700; }
  .guide-hero-mark { width: 220px; height: 220px; margin-left: auto; display: flex; flex-direction: column; justify-content: flex-end; padding: 24px; border: 1px solid var(--accent-gold); background: var(--accent-gold-light); color: var(--text-main); transform: rotate(3deg); }
  .guide-hero-mark span { color: var(--accent-gold); font-size: 4rem; font-weight: 900; line-height: 1; }
  .guide-hero-mark strong { font-size: 1.2rem; margin-top: 12px; }
  .guide-hero-mark small { color: var(--text-muted); }
  .guide-section { padding: 70px 0 0; }
  .guide-section-heading { margin-bottom: 24px; }
  .guide-section-heading h2, .guide-free-month h2, .guide-info-panel h2, .guide-hub-panel h2 { margin-top: 7px; font-size: clamp(1.45rem, 3vw, 2.2rem); line-height: 1.15; }
  .workflow-grid { display: grid; grid-template-columns: repeat(5, minmax(0, 1fr)); gap: 12px; }
  .workflow-grid article, .offer-grid article, .guide-info-panel, .guide-hub-panel, .guide-final-cta { padding: 20px; border: 1px solid var(--border-subtle); background: var(--bg-surface); border-radius: var(--radius-md); }
  .workflow-grid article { min-height: 210px; }
  .workflow-grid article > span { color: var(--accent-gold); font-size: 1.45rem; font-weight: 900; }
  .workflow-grid h3 { margin: 18px 0 7px; font-size: .95rem; }
  .workflow-grid p, .workflow-grid small { color: var(--text-muted); font-size: .76rem; line-height: 1.45; }
  .workflow-grid b { display: block; margin-top: 12px; color: var(--status-success-text); font-size: .75rem; }
  .workflow-grid strong { display: block; margin-top: 14px; color: var(--accent-gold); font-size: .74rem; }
  .guide-free-month { display: grid; grid-template-columns: 1.4fr 1fr auto; gap: 28px; align-items: center; margin-top: 70px; padding: 34px; background: var(--text-main); color: var(--text-inverse); border-radius: var(--radius-lg); }
  .guide-free-month p { max-width: 500px; margin-top: 8px; color: var(--text-muted); }
  .free-checks { display: grid; gap: 8px; color: var(--text-inverse); font-size: .78rem; }
  .free-checks span::first-letter { color: var(--accent-gold); }
  .offer-grid { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 14px; }
  .offer-grid article > span { font-size: 1.7rem; }
  .offer-grid h3 { margin: 12px 0 6px; font-size: 1rem; }
  .offer-grid p, .guide-info-panel p, .guide-hub-panel p { color: var(--text-muted); font-size: .82rem; line-height: 1.5; }
  .guide-two-column { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; padding-top: 70px; }
  .guide-info-panel ul { display: grid; gap: 8px; margin-top: 18px; padding-left: 18px; color: var(--text-muted); font-size: .82rem; }
  .verified-callout { display: inline-flex; margin: 18px 0 10px; padding: 8px 12px; background: var(--status-success-bg); color: var(--status-success-text); font-size: .8rem; font-weight: 800; }
  .guide-info-panel > small { color: var(--text-muted); font-size: .7rem; }
  .guide-hub-panel { display: grid; grid-template-columns: 1fr 1.4fr; align-items: center; gap: 30px; margin-top: 70px; padding: 32px; background: var(--bg-surface-secondary); }
  .hub-items { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; }
  .hub-items span { padding: 13px; border: 1px solid var(--border-subtle); background: var(--bg-surface); color: var(--text-main); font-size: .78rem; font-weight: 800; }
  .faq-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 8px; }
  .faq-grid details { border-bottom: 1px solid var(--border-subtle); padding: 14px 4px; }
  .faq-grid summary { cursor: pointer; font-size: .84rem; font-weight: 800; }
  .faq-grid p { padding: 10px 20px 0 0; color: var(--text-muted); font-size: .78rem; line-height: 1.5; }
  .guide-final-cta { margin: 70px 0; padding: 42px; text-align: center; background: var(--accent-gold-light); border-color: var(--accent-gold); }
  .guide-final-cta p { max-width: 720px; margin: 10px auto 20px; color: var(--text-muted); font-size: .9rem; }
  .guide-final-cta strong { display: block; margin-top: 12px; color: var(--status-success-text); font-size: .75rem; }

  .modal-backdrop {
    position: fixed;
    inset: 0;
    z-index: 1000;
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 24px;
    background: rgba(11, 19, 32, 0.66);
    backdrop-filter: blur(8px);
  }

  .modal-content {
    max-height: 100%;
  }

  .modal-header {
    display: flex;
    align-items: flex-start;
    justify-content: space-between;
    gap: 20px;
    margin-bottom: 24px;
  }

  .modal-header h2 {
    font-size: clamp(1.25rem, 2.5vw, 1.8rem);
    line-height: 1.2;
  }

  .close-btn {
    flex: 0 0 auto;
    width: 36px;
    height: 36px;
    border: 1px solid var(--border-subtle);
    border-radius: 50%;
    background: var(--bg-surface-secondary);
    color: var(--text-main);
    cursor: pointer;
    font-size: 1rem;
  }

  .modal-actions {
    display: flex;
    justify-content: flex-end;
    gap: 10px;
    margin-top: 8px;
  }

  .inventory-empty {
    min-height: 190px;
    border: 1px dashed var(--border-subtle);
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 8px;
    text-align: center;
    color: var(--text-muted);
  }

  .inventory-empty strong { color: var(--text-main); }
  .inventory-empty .btn-gold { margin-top: 8px; }

  .provider-listings-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 16px;
    margin-top: 18px;
  }

  .provider-listing-card {
    position: relative;
    overflow: hidden;
    border: 1px solid var(--border-subtle);
    background: var(--bg-surface);
    border-radius: var(--radius-md);
  }

  .provider-listing-number {
    position: absolute;
    top: 10px;
    left: 10px;
    z-index: 1;
    padding: 4px 7px;
    background: var(--accent-gold);
    color: #fff;
    font-size: .7rem;
    font-weight: 900;
  }

  .provider-listing-image { width: 100%; height: 140px; display: block; object-fit: cover; background: var(--bg-surface-secondary); }
  .provider-listing-body { padding: 14px; }
  .provider-listing-meta, .provider-listing-footer { display: flex; justify-content: space-between; align-items: center; gap: 8px; }
  .provider-listing-meta { color: var(--accent-gold); font-size: .68rem; font-weight: 800; }
  .provider-listing-meta .badge-verified { color: var(--status-success-text); font-size: .62rem; }
  .provider-listing-body h4 { margin: 9px 0 5px; font-size: .98rem; line-height: 1.25; }
  .provider-listing-body p { min-height: 38px; color: var(--text-muted); font-size: .76rem; line-height: 1.4; }

  .listing-booking-summary { margin-top: 12px; padding-top: 11px; border-top: 1px solid var(--border-subtle); }
  .listing-booking-heading { display: flex; justify-content: space-between; gap: 8px; align-items: center; margin-bottom: 7px; font-size: .72rem; }
  .listing-booking-heading strong { color: var(--accent-gold); }
  .listing-booking-heading span, .no-listing-bookings { color: var(--text-muted); font-size: .68rem; }
  .listing-booking-history { display: flex; flex-direction: column; gap: 6px; max-height: 150px; overflow: auto; }
  .listing-booking-item { display: grid; grid-template-columns: 24px minmax(0, 1fr) auto; gap: 7px; align-items: center; padding: 6px 0; border-top: 1px dashed var(--border-subtle); font-size: .68rem; }
  .listing-booking-item > div { min-width: 0; display: flex; flex-direction: column; gap: 2px; }
  .listing-booking-item small { color: var(--text-muted); overflow-wrap: anywhere; }
  .listing-booking-index { color: var(--accent-gold); font-weight: 900; }
  .listing-booking-status { font-size: .6rem; font-weight: 900; text-transform: uppercase; color: var(--text-muted); }
  .provider-listing-footer { margin-top: 14px; font-size: .75rem; }
  .provider-listing-footer strong { color: var(--accent-gold); }
  .provider-listing-footer span { color: var(--status-success-text); font-weight: 700; }

  .footer {
    background: #fffdfa;
    color: var(--text-main);
    border-top: 1px solid var(--border-subtle);
    padding: 56px clamp(20px, 5vw, 72px) 24px;
    margin-top: 0;
    width: 100%;
  }

  .footer-container {
    width: 100%;
    margin: 0 auto;
    display: grid;
    max-width: 1440px;
    grid-template-columns: 1.75fr repeat(4, 1fr) 1.55fr;
    gap: 32px;
  }

  :global([data-theme="dark"]) .footer { background: #0d1824; color: #f7f4ee; }

  .provider-cta {
    max-width: 1440px;
    margin: 72px auto 0;
    min-height: 300px;
    padding: 0;
    display: flex;
    justify-content: space-between;
    gap: 0;
    overflow: hidden;
    background: var(--bg-surface);
    border: 1px solid var(--border-subtle);
    border-radius: var(--radius-lg);
    box-shadow: var(--card-shadow);
  }

  .provider-cta-content { width: 58%; padding: 32px clamp(24px, 4vw, 58px); }
  .provider-cta h2 { display: flex; align-items: center; gap: 10px; font-size: clamp(1.6rem, 3vw, 2.35rem); margin: 8px 0 8px; }
  .provider-cta p { max-width: 620px; color: var(--text-muted); font-size: .93rem; }
  .provider-cta-action { display: none; }
  .provider-cta-actions { display: flex; gap: 10px; margin-top: 20px; }
  .provider-how-btn { padding-inline: 16px; }
  .provider-proof { display: flex; align-items: center; gap: 6px; color: var(--text-muted); font-size: .74rem; white-space: nowrap; }
  .provider-proof :global(svg) { color: var(--status-success-text); }

  .provider-benefits { display: grid; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 18px; margin-top: 24px; }
  .provider-benefits > div { display: flex; align-items: flex-start; gap: 9px; min-width: 0; }
  .provider-benefit-icon, .provider-stat-icon { display: grid; place-items: center; flex: 0 0 auto; width: 34px; height: 34px; border-radius: 50%; background: var(--accent-gold-light); color: var(--accent-gold); }
  .provider-benefits > div > span:last-child { display: flex; flex-direction: column; gap: 2px; }
  .provider-benefits strong { font-size: .7rem; }
  .provider-benefits small { color: var(--text-muted); font-size: .66rem; line-height: 1.3; }
  .provider-cta-visual { position: relative; width: 42%; min-height: 300px; overflow: hidden; }
  .provider-cta-visual::after { content: ''; position: absolute; inset: 0; background: linear-gradient(90deg, var(--bg-surface) 0%, rgba(255,255,255,0) 28%), linear-gradient(0deg, rgba(11,19,32,.26), transparent 50%); pointer-events: none; }
  .provider-cta-visual img { width: 100%; height: 100%; display: block; object-fit: cover; object-position: center; }
  .provider-cta-stat { position: absolute; right: 24px; bottom: 22px; z-index: 1; display: grid; grid-template-columns: auto 1fr; gap: 4px 9px; min-width: 170px; padding: 14px; color: var(--text-main); background: rgba(255,255,255,.92); border-radius: var(--radius-md); box-shadow: 0 8px 24px rgba(11,19,32,.14); }
  .provider-cta-stat strong { display: flex; flex-direction: column; font-size: 1.05rem; line-height: 1.1; }
  .provider-cta-stat strong small { font-size: .68rem; margin-top: 3px; }
  .provider-cta-stat > span:last-child { grid-column: 2; color: var(--text-muted); font-size: .62rem; }
  :global([data-theme="dark"]) .provider-cta-visual::after { background: linear-gradient(90deg, var(--bg-surface) 0%, rgba(13,24,36,0) 28%), linear-gradient(0deg, rgba(0,0,0,.35), transparent 50%); }
  :global([data-theme="dark"]) .provider-cta-stat { background: rgba(24,32,42,.92); color: var(--text-main); }

  .eyebrow { color: var(--accent-gold); font-size: .7rem; font-weight: 800; letter-spacing: .12em; }
  .footer-logo { width: 176px; height: auto; display: block; opacity: .96; }

  .footer-desc {
    font-size: 0.88rem;
    color: var(--text-muted);
    margin-top: 10px;
    max-width: 380px;
  }

  .footer-socials { display: flex; gap: 8px; margin-top: 20px; }
  .footer-socials button { width: 30px; height: 30px; display: grid; place-items: center; border: 1px solid var(--border-subtle); color: var(--text-main); font-size: .66rem; font-weight: 800; text-decoration: none; background: transparent; }
  .footer-socials button:not(:disabled):hover { border-color: var(--accent-gold); color: var(--accent-gold); }
  .footer-socials button:disabled, .app-buttons button:disabled { cursor: not-allowed; opacity: .55; }

  .footer-col h4 {
    font-size: 0.95rem;
    font-weight: 800;
    margin-bottom: 16px;
    color: var(--text-main);
  }

  .footer-col ul {
    list-style: none;
    display: flex;
    flex-direction: column;
    gap: 8px;
    font-size: 0.85rem;
    color: var(--text-muted);
  }

  .footer-col li a { color: inherit; text-decoration: none; transition: color .2s ease; }
  .footer-col li a:hover { color: var(--accent-gold); }

  .footer-connect h4 { color: var(--text-main); font-size: .95rem; text-transform: uppercase; letter-spacing: .04em; margin-bottom: 12px; }
  .footer-connect p { color: var(--text-muted); font-size: .78rem; margin-bottom: 12px; }
  .subscribe-form { display: flex; border: 1px solid var(--border-subtle); background: var(--bg-surface); overflow: hidden; }
  .subscribe-form input { min-width: 0; width: 100%; border: 0; outline: 0; padding: 10px 11px; background: transparent; color: var(--text-main); font: inherit; font-size: .74rem; }
  .subscribe-form button { width: 40px; border: 0; background: var(--accent-gold); color: #fff; font-size: 1.1rem; cursor: pointer; }
  .app-label { display: block; margin: 18px 0 8px; color: var(--text-main); font-size: .7rem; font-weight: 800; text-transform: uppercase; }
  .app-buttons { display: flex; gap: 6px; }
  .app-buttons button { border: 1px solid var(--border-subtle); color: var(--text-main); padding: 8px 9px; font-size: .68rem; text-decoration: none; background: transparent; }
  .app-buttons button:not(:disabled):hover { border-color: var(--accent-gold); color: var(--accent-gold); }
  .footer-message { display: block; margin-top: 8px; color: var(--status-success-text); }

  .footer-trust { max-width: 1440px; margin: 36px auto 0; padding: 22px 0 0; border-top: 1px solid var(--border-subtle); display: grid; grid-template-columns: repeat(4, 1fr); gap: 18px; }
  .footer-trust > div { display: flex; align-items: flex-start; gap: 10px; padding-right: 18px; border-right: 1px solid var(--border-subtle); }
  .footer-trust > div:last-child { border-right: 0; }
  .footer-trust :global(svg) { flex: 0 0 auto; color: var(--accent-gold); }
  .footer-trust span { display: flex; flex-direction: column; gap: 3px; }
  .footer-trust strong { font-size: .72rem; }
  .footer-trust small { color: var(--text-muted); font-size: .68rem; line-height: 1.35; }

  .footer-bottom {
    width: 100%;
    margin: 32px auto 0;
    padding-top: 20px;
    border-top: 1px solid var(--border-subtle);
    text-align: center;
    font-size: 0.8rem;
    color: var(--text-muted);
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 18px;
  }

  .footer-bottom span:nth-child(2) { text-align: center; }
  .footer-bottom a { color: inherit; text-decoration: none; }
  .footer-bottom a:hover { color: var(--accent-gold); }
  .footer-bottom select { border: 0; background: transparent; color: inherit; font: inherit; cursor: pointer; }

  .sr-only { position: absolute; width: 1px; height: 1px; padding: 0; margin: -1px; overflow: hidden; clip: rect(0,0,0,0); white-space: nowrap; border: 0; }

  @media (max-width: 900px) {
    .provider-cta { margin-top: 48px; flex-direction: column; }
    .provider-cta-content, .provider-cta-visual { width: 100%; }
    .provider-cta-visual { min-height: 230px; }
    .footer-container { grid-template-columns: repeat(3, 1fr); }
    .footer-container .footer-col:first-child { grid-column: 1 / -1; }
    .footer-connect { grid-column: 1 / -1; max-width: 360px; }
    .footer-trust { grid-template-columns: repeat(2, 1fr); }
    .footer-trust > div:nth-child(2) { border-right: 0; }
    .provider-guide-hero { grid-template-columns: 1fr; }
    .guide-hero-mark { display: none; }
    .workflow-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
    .guide-free-month { grid-template-columns: 1fr 1fr; }
    .guide-free-month .btn-gold { grid-column: 1 / -1; justify-self: start; }
    .offer-grid { grid-template-columns: repeat(2, minmax(0, 1fr)); }
    .guide-hub-panel { grid-template-columns: 1fr; }
  }

  @media (max-width: 520px) {
    .provider-cta-content { padding: 28px 20px; }
    .provider-benefits { grid-template-columns: 1fr 1fr; gap: 14px 10px; }
    .provider-benefits strong { font-size: .66rem; }
    .provider-benefits small { font-size: .62rem; }
    .provider-cta-actions { flex-direction: column; }
    .provider-cta-actions button { width: 100%; }
    .provider-cta-visual { min-height: 210px; }
    .provider-proof { white-space: normal; flex-wrap: wrap; }
    .footer-container { grid-template-columns: 1fr 1fr; gap: 28px 18px; }
    .footer-container .footer-col:first-child { grid-column: 1 / -1; }
    .footer-bottom { flex-direction: column; gap: 6px; text-align: left; }
    .footer-trust { grid-template-columns: 1fr; gap: 16px; }
    .footer-trust > div { border-right: 0; }
    .footer-bottom span:nth-child(2) { text-align: left; }
    .provider-guide-hero { padding: 30px 20px; }
    .provider-guide-hero h1 { font-size: 2rem; }
    .guide-section, .guide-two-column { padding-top: 46px; }
    .workflow-grid, .offer-grid, .guide-two-column, .faq-grid, .guide-free-month { grid-template-columns: 1fr; }
    .guide-free-month { margin-top: 46px; padding: 24px 20px; }
    .guide-free-month .btn-gold { grid-column: auto; }
    .guide-final-cta { margin: 46px 0; padding: 30px 20px; }
  }
  .hot-deals-section {
    display: flex;
    flex-direction: column;
    gap: 24px;
  }

  @media (max-width: 600px) {
    .main-content { padding: 12px 12px 32px; }
    .hero-section { padding: 48px 14px 24px; margin: -12px -12px 22px; width: calc(100% + 24px); min-height: 620px; }
    .hero-content-box { width: 100%; gap: 10px; }
    .hero-pill-badge { max-width: 100%; font-size: .62rem; padding-inline: 9px; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
    .hero-headline { font-size: clamp(1.65rem, 8vw, 2.2rem); line-height: 1.1; }
    .hero-subtext { font-size: .86rem; line-height: 1.45; }
    .category-experience-tabs { width: 100%; justify-content: flex-start; overflow-x: auto; border-radius: var(--radius-md); scrollbar-width: none; }
    .category-experience-tabs::-webkit-scrollbar { display: none; }
    .experience-tab { flex: 0 0 auto; font-size: .72rem; padding: 8px 10px; }
    .search-module { flex-direction: column; align-items: stretch; gap: 11px; padding: 13px; border-radius: var(--radius-lg); }
    .search-divider { width: 100%; height: 1px; }
    .search-field { width: 100%; }
    .search-btn { width: 100%; justify-content: center; padding: 11px 14px; font-size: .84rem; }
    .location-chips-row { justify-content: flex-start; flex-wrap: nowrap; overflow-x: auto; padding-bottom: 3px; scrollbar-width: none; }
    .location-chips-row::-webkit-scrollbar { display: none; }
    .chips-label, .location-chip { flex: 0 0 auto; }
    .marketplace-browsing-section { display: block; margin-bottom: 28px; }
    .filters-sidebar { display: none; }
    .listings-main-area { width: 100%; gap: 14px; }
    .sort-header-row { align-items: flex-start; flex-direction: column; gap: 10px; }
    .browse-title { font-size: 1.12rem; line-height: 1.25; }
    .results-count { font-size: .76rem; }
    .sort-controls { width: 100%; justify-content: space-between; }
    .sort-select { max-width: 190px; }
    .listings-grid, .trending-grid, .deals-grid { grid-template-columns: minmax(0, 1fr); gap: 14px; }
    .empty-state-box { padding: 30px 18px; }
    .form-grid { grid-template-columns: minmax(0, 1fr); }
    .passport-page-spread { grid-template-columns: minmax(0, 1fr); }
    .book-spine { display: none; }
    .page-left, .page-right { padding: 16px; }
    .company-title-block { flex-direction: column; gap: 12px; }
    .provider-dash-header, .company-listings-card, .provider-onboard-card, .provider-bookings-panel { padding: 18px; }
    .provider-booking-layout { grid-template-columns: 1fr; }
    .provider-booking-row { grid-template-columns: 1fr; gap: 9px; }
    .booking-row-main > strong { white-space: normal; }
    .booking-row-actions { justify-content: flex-start; }
    .provider-cta { margin: 26px 0 0; padding: 28px 18px; }
    .provider-cta h2 { font-size: 1.55rem; }
    .provider-cta-action { width: 100%; }
    :global(.provider-cta-action .btn-gold) { width: 100%; }
    .footer { padding: 38px 18px 20px; }
    .footer-container { grid-template-columns: minmax(0, 1fr) minmax(0, 1fr); gap: 24px 16px; }
    .footer-logo { width: 150px; }
    .footer-desc { font-size: .8rem; }
    .footer-col h4 { font-size: .78rem; margin-bottom: 11px; }
    .footer-col ul { font-size: .74rem; gap: 7px; }
    .footer-connect { grid-column: 1 / -1; max-width: none; }
    .footer-bottom { font-size: .7rem; overflow-wrap: anywhere; }
  }

  .deals-hero-banner {
    background: linear-gradient(135deg, var(--bg-surface-secondary) 0%, rgba(200, 155, 60, 0.12) 100%);
    border: 1px solid var(--border-subtle);
    padding: 32px;
    border-radius: var(--radius-xl);
    border-left: 4px solid var(--accent-terracotta);
  }

  .deals-hero-banner h2 {
    font-size: 1.8rem;
    font-weight: 900;
    margin-top: 4px;
  }

  .deals-hero-banner p {
    font-size: 0.95rem;
    color: var(--text-muted);
    margin-top: 6px;
  }

  .deals-grid {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 20px;
  }

  .deal-card {
    position: relative;
    overflow: hidden;
    display: flex;
    flex-direction: column;
  }

  .deal-badge-overlay {
    position: absolute;
    top: 14px;
    right: 14px;
    background: var(--accent-terracotta);
    color: #ffffff;
    font-weight: 900;
    font-size: 0.8rem;
    padding: 6px 12px;
    border-radius: var(--radius-sm);
    z-index: 10;
    box-shadow: 0 4px 12px rgba(184, 92, 56, 0.3);
  }

  .deal-img {
    width: 100%;
    height: 190px;
    object-fit: cover;
  }

  .deal-body {
    padding: 20px;
    display: flex;
    flex-direction: column;
    gap: 8px;
    flex-grow: 1;
  }

  .deal-category {
    font-size: 0.7rem;
    font-weight: 900;
    color: var(--accent-terracotta);
    letter-spacing: 0.08em;
  }

  .deal-body h3 {
    font-size: 1.15rem;
    font-weight: 900;
    color: var(--text-main);
  }

  .deal-host {
    font-size: 0.82rem;
    color: var(--text-muted);
  }

  .timer-box {
    background: var(--bg-surface-secondary);
    border: 1px solid var(--border-subtle);
    padding: 8px 12px;
    border-radius: var(--radius-sm);
    display: flex;
    justify-content: space-between;
    align-items: center;
    font-size: 0.8rem;
  }

  .timer-val {
    color: var(--accent-gold);
    font-weight: 900;
  }

  .deal-price-row {
    display: flex;
    justify-content: space-between;
    align-items: flex-end;
    margin-top: 10px;
    padding-top: 10px;
    border-top: 1px solid var(--border-subtle);
  }

  .price-strikethrough-box {
    display: flex;
    flex-direction: column;
  }

  .orig-price {
    font-size: 0.8rem;
    text-decoration: line-through;
    color: var(--text-muted);
  }

  .discounted-price {
    font-size: 1.18rem;
    font-weight: 900;
    color: var(--accent-gold);
  }

  .easyservice-marketplace-section {
    margin-top: 56px;
    padding-top: 40px;
    border-top: 1px solid var(--border-subtle);
  }

  .marketplace-section-header {
    display: flex;
    align-items: flex-end;
    justify-content: space-between;
    gap: 24px;
    margin-bottom: 24px;
  }

  .marketplace-eyebrow {
    display: inline-block;
    margin-bottom: 7px;
    font-size: 0.72rem;
    font-weight: 900;
    letter-spacing: 0.12em;
    color: var(--accent-gold);
  }

  .marketplace-section-header h2 {
    margin: 0;
    font-size: 1.55rem;
    color: var(--text-primary);
  }

  .marketplace-section-header p {
    margin: 7px 0 0;
    color: var(--text-muted);
    font-size: 0.92rem;
  }

  .marketplace-count {
    flex-shrink: 0;
    padding: 7px 12px;
    border: 1px solid var(--border-subtle);
    border-radius: 999px;
    background: var(--bg-surface);
    color: var(--text-muted);
    font-size: 0.8rem;
    font-weight: 800;
  }

  .easyservice-empty-state {
    margin-top: 56px;
    padding: 42px 24px;
    text-align: center;
    border: 1px dashed var(--border-subtle);
    border-radius: 18px;
    background: var(--bg-surface);
  }

  .empty-state-icon {
    width: 46px;
    height: 46px;
    margin: 0 auto 14px;
    display: grid;
    place-items: center;
    border-radius: 50%;
    background: var(--bg-surface-secondary);
    color: var(--accent-gold);
    font-size: 1.4rem;
  }

  .easyservice-empty-state h3 {
    margin: 0;
    color: var(--text-primary);
  }

  .easyservice-empty-state p {
    max-width: 600px;
    margin: 9px auto 0;
    color: var(--text-muted);
    line-height: 1.6;
  }

  /* =========================================================
     PUBLIC SOURCE DISCOVERY
     EasyService branded discovery section
     ========================================================= */

  .discovery-section {
    width: 100%;
    margin-top: 42px;
  }


  /* ---------------------------------------------------------
     HERO HEADER
     --------------------------------------------------------- */

  .discovery-hero {
    position: relative;
    overflow: hidden;

    min-height: 235px;

    display: flex;
    align-items: center;

    margin-bottom: 30px;
    padding: 38px 42px;

    border-radius: 24px;

    background:
      linear-gradient(
        135deg,
        #071d3b 0%,
        #0b2b52 55%,
        #123b68 100%
      );

    border: 1px solid rgba(212, 175, 55, 0.75);

    box-shadow:
      0 14px 36px rgba(7, 29, 59, 0.18);
  }


  .discovery-hero::before {
    content: '';

    position: absolute;
    inset: 0;

    background:
      radial-gradient(
        circle at 85% 25%,
        rgba(212, 175, 55, 0.14),
        transparent 32%
      );

    pointer-events: none;
  }


  .discovery-hero-content {
    position: relative;
    z-index: 2;

    max-width: 760px;
  }


  /* ---------------------------------------------------------
     EYEBROW
     --------------------------------------------------------- */

  .discovery-eyebrow {
    display: inline-flex;
    align-items: center;
    gap: 9px;

    margin-bottom: 16px;

    color: #f4c84a;

    font-size: 0.74rem;
    font-weight: 900;
    letter-spacing: 0.14em;
  }


  .discovery-eyebrow-icon {
    width: 28px;
    height: 28px;

    display: inline-flex;
    align-items: center;
    justify-content: center;

    border: 1px solid rgba(244, 200, 74, 0.85);
    border-radius: 50%;

    color: #f4c84a;
  }


  /* ---------------------------------------------------------
     MAIN TITLE
     --------------------------------------------------------- */

  .discovery-hero h2 {
    margin: 0;

    color: #ffffff;

    font-size: clamp(2rem, 4vw, 3.2rem);
    line-height: 1.05;
    font-weight: 900;

    letter-spacing: -0.035em;
  }


  .discovery-hero h2 span {
    color: #f4c84a;
  }


  .discovery-hero p {
    max-width: 720px;

    margin: 17px 0 22px;

    color: rgba(255, 255, 255, 0.86);

    font-size: 0.96rem;
    line-height: 1.6;
  }


  /* ---------------------------------------------------------
     FOUND COUNT
     --------------------------------------------------------- */

  .discovery-count {
    display: inline-flex;
    align-items: center;
    gap: 7px;

    min-height: 40px;

    padding: 5px 16px 5px 6px;

    border: 1px solid rgba(244, 200, 74, 0.85);
    border-radius: 999px;

    color: #ffffff;

    background: rgba(5, 22, 45, 0.52);

    font-size: 0.85rem;
  }


  .discovery-count strong {
    font-size: 0.95rem;
  }


  .discovery-count-icon {
    width: 30px;
    height: 30px;

    display: inline-flex;
    align-items: center;
    justify-content: center;

    border-radius: 50%;

    background: #f4c84a;
    color: #071d3b;
  }


  /* ---------------------------------------------------------
     DECORATIVE ES MARK
     --------------------------------------------------------- */

  .discovery-hero-mark {
    position: absolute;

    right: 48px;
    top: 50%;

    transform: translateY(-50%);

    color: rgba(244, 200, 74, 0.10);

    font-size: 9rem;
    line-height: 1;
    font-weight: 1000;
    letter-spacing: -0.12em;

    user-select: none;
    pointer-events: none;
  }


  .discovery-hero-line {
    position: absolute;

    right: 115px;
    bottom: -45px;

    width: 3px;
    height: 270px;

    background: rgba(244, 200, 74, 0.55);

    transform: rotate(35deg);

    pointer-events: none;
  }


  /* ---------------------------------------------------------
     RESULTS HEADER
     --------------------------------------------------------- */

  .discovery-results-header {
    display: flex;
    align-items: flex-end;
    justify-content: space-between;
    gap: 24px;

    margin: 0 4px 20px;
  }


  .discovery-results-label {
    display: block;

    margin-bottom: 6px;

    color: #c99624;

    font-size: 0.72rem;
    font-weight: 900;

    letter-spacing: 0.12em;
  }


  .discovery-results-header h3 {
    margin: 0;

    color: var(--text-main, #071d3b);

    font-size: 1.65rem;
    font-weight: 900;

    letter-spacing: -0.025em;
  }


  .discovery-results-header p {
    max-width: 720px;

    margin: 7px 0 0;

    color: var(--text-muted, #64748b);

    font-size: 0.88rem;
    line-height: 1.5;
  }


  .discovery-results-count {
    flex-shrink: 0;

    padding: 10px 16px;

    border: 1px solid rgba(212, 175, 55, 0.35);
    border-radius: 999px;

    background: var(--bg-surface);

    color: var(--text-main);

    font-size: 0.82rem;
    font-weight: 900;

    box-shadow: 0 4px 14px rgba(15, 23, 42, 0.06);
  }


  /* ---------------------------------------------------------
     DISCOVERY GRID
     --------------------------------------------------------- */

  .discovery-grid {
    display: grid;

    grid-template-columns:
      repeat(3, minmax(0, 1fr));

    gap: 24px;
  }


  /* ---------------------------------------------------------
     TABLET
     --------------------------------------------------------- */

  @media (max-width: 1050px) {

    .discovery-hero {
      padding: 32px;
    }

    .discovery-hero-mark {
      right: 25px;
      font-size: 7rem;
    }

    .discovery-grid {
      grid-template-columns:
        repeat(2, minmax(0, 1fr));
    }
  }


  /* ---------------------------------------------------------
     MOBILE
     --------------------------------------------------------- */

  @media (max-width: 640px) {

    .discovery-section {
      margin-top: 30px;
    }

    .discovery-hero {
      min-height: auto;

      padding: 28px 22px;

      border-radius: 20px;
    }

    .discovery-eyebrow {
      font-size: 0.67rem;
    }

    .discovery-hero h2 {
      font-size: 2rem;
    }

    .discovery-hero p {
      font-size: 0.86rem;
    }

    .discovery-hero-mark {
      right: -8px;
      top: 42px;

      font-size: 6rem;
    }

    .discovery-hero-line {
      display: none;
    }

    .discovery-results-header {
      align-items: flex-start;
      flex-direction: column;
      gap: 12px;

      margin-left: 2px;
      margin-right: 2px;
    }

    .discovery-results-header h3 {
      font-size: 1.35rem;
    }

    .discovery-results-count {
      align-self: flex-start;
    }

    .discovery-grid {
      grid-template-columns: 1fr;
      gap: 18px;
    }
  }
</style>
