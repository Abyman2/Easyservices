package com.easyservice.backend.service;

import com.easyservice.backend.dto.RegisterRequest;
import com.easyservice.backend.infrastructure.FakeIdentityVerificationService;
import com.easyservice.backend.infrastructure.FakeNotificationService;
import com.easyservice.backend.infrastructure.FakePaymentService;
import com.easyservice.backend.model.Listing;
import com.easyservice.backend.model.Promotion;
import com.easyservice.backend.model.Transaction;
import com.easyservice.backend.model.User;
import com.easyservice.backend.model.enums.CustomerType;
import com.easyservice.backend.model.enums.IdentityStatus;
import com.easyservice.backend.model.enums.IdentityType;
import com.easyservice.backend.model.enums.ListingCategory;
import com.easyservice.backend.model.enums.ListingStatus;
import com.easyservice.backend.model.enums.PromotionStatus;
import com.easyservice.backend.model.enums.TransactionStatus;
import com.easyservice.backend.repository.InMemoryListingRepository;
import com.easyservice.backend.repository.InMemoryPromotionRepository;
import com.easyservice.backend.repository.InMemoryTransactionRepository;
import com.easyservice.backend.repository.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class CoreBusinessLogicCoverageTest {

    private InMemoryUserRepository users;
    private InMemoryListingRepository listings;
    private InMemoryTransactionRepository transactions;
    private BookingTransactionService booking;
    private ListingService listingService;
    private PromotionService promotions;
    private RegistrationService registration;
    private EasyToolsService tools;

    @BeforeEach
    void setUp() {
        users = new InMemoryUserRepository();
        listings = new InMemoryListingRepository();
        transactions = new InMemoryTransactionRepository();
        promotions = new PromotionService(new InMemoryPromotionRepository());
        booking = new BookingTransactionService(
                users, listings, transactions,
                new FakePaymentService(users), promotions,
                new FakeNotificationService());
        listingService = new ListingService(listings);
        registration = new RegistrationService(users, new FakeIdentityVerificationService());
        tools = new EasyToolsService(size -> 0);

        users.save(user("cust1", IdentityStatus.VERIFIED, new BigDecimal("5000")));
        listings.save(new Listing("list1", "provider1", "Test Hotel", ListingCategory.HOTEL,
                "Test", new BigDecimal("500"), 5, 5, ListingStatus.PUBLISHED));
    }

    @Test
    void bookingCoversLookupValidationAndDateBranches() {
        assertThrows(IllegalArgumentException.class, () -> booking.createBooking("missing", "list1", 1, null));
        assertThrows(IllegalArgumentException.class, () -> booking.createBooking("cust1", "missing", 1, null));
        assertThrows(IllegalArgumentException.class, () -> booking.createBooking("cust1", "list1", 0, null));

        Listing draft = new Listing("draft", "provider1", "Draft", ListingCategory.HOTEL,
                "Test", new BigDecimal("100"), 2, 2, ListingStatus.DRAFT);
        listings.save(draft);
        assertThrows(IllegalStateException.class, () -> booking.createBooking("cust1", "draft", 1, null));

        LocalDate start = LocalDate.now().plusDays(5);
        assertThrows(IllegalArgumentException.class, () -> booking.createBooking(
                "cust1", "list1", 1, null, start, start.minusDays(1), null, null, null, null, null, null));

        Transaction first = booking.createBooking(
                "cust1", "list1", 1, null, start, start.plusDays(1), null, null, null, null, null, null);
        assertEquals(TransactionStatus.CONFIRMED, first.getStatus());
        assertThrows(IllegalStateException.class, () -> booking.createBooking(
                "cust1", "list1", 1, null, start, start.plusDays(1), null, null, null, null, null, null));

        assertEquals(1, booking.getCustomerBookings("cust1").size());
        assertEquals(1, booking.getProviderBookings("provider1").size());
        assertTrue(booking.getProviderBookings(null).isEmpty());
        assertEquals(1, booking.getListingBookings("list1").size());
    }

    @Test
    void bookingCoversProviderStatusAndCancellationGuards() {
        Transaction confirmed = booking.createBooking("cust1", "list1", 1, null);
        assertEquals("ACCEPTED", booking.updateProviderStatus(confirmed.getId(), "provider1", "ACCEPTED").getProviderStatus());
        assertThrows(IllegalArgumentException.class, () -> booking.updateProviderStatus(confirmed.getId(), "provider1", "PENDING"));
        assertThrows(SecurityException.class, () -> booking.updateProviderStatus(confirmed.getId(), "other", "ACCEPTED"));
        assertThrows(IllegalArgumentException.class, () -> booking.updateProviderStatus("missing", "provider1", "ACCEPTED"));
        assertThrows(SecurityException.class, () -> booking.cancelTransaction(confirmed.getId(), "other"));
        assertThrows(IllegalArgumentException.class, () -> booking.completeTransaction("missing"));

        Transaction pending = new Transaction("pending", "cust1", "list1", "provider1", "Name", "email", "phone",
                1, BigDecimal.TEN, TransactionStatus.PENDING, "PENDING", null, null, null, null, null, LocalDateTime.now());
        transactions.save(pending);
        assertEquals(TransactionStatus.CANCELLED, booking.cancelTransaction("pending", "cust1").getStatus());
        booking.completeTransaction(confirmed.getId());
        assertThrows(IllegalStateException.class, () -> booking.cancelTransaction(confirmed.getId(), "cust1"));
    }

    @Test
    void listingCoversNullsAndProviderOperations() {
        assertThrows(IllegalArgumentException.class, () -> listingService.createListing("p", "Title", ListingCategory.HOTEL, "d", null, 1));
        assertThrows(IllegalArgumentException.class, () -> listingService.createListing("p", null, ListingCategory.HOTEL, "d", BigDecimal.ONE, 1));
        assertThrows(IllegalArgumentException.class, () -> listingService.createListing("p", " ", ListingCategory.HOTEL, "d", BigDecimal.ONE, 1));
        assertThrows(IllegalArgumentException.class, () -> listingService.publishListing("missing", "p"));
        Listing invalid = new Listing("invalid", "p", "Invalid", ListingCategory.HOTEL, "d", null, 1, 1, ListingStatus.DRAFT);
        listings.save(invalid);
        assertThrows(IllegalStateException.class, () -> listingService.publishListing("invalid", "p"));
        assertThrows(IllegalArgumentException.class, () -> listingService.unpublishListing("missing", "p"));
        assertThrows(SecurityException.class, () -> listingService.unpublishListing("invalid", "other"));
        assertEquals(1, listingService.getProviderListings("provider1").size());
        assertEquals(ListingStatus.UNPUBLISHED, listingService.unpublishListing("list1", "provider1").getStatus());
    }

    @Test
    void registrationCoversEveryValidationBoundary() {
        RegisterRequest valid = registrationRequest();
        assertThrows(IllegalArgumentException.class, () -> registration.register(null));
        assertThrows(IllegalArgumentException.class, () -> registration.register(copy(valid, null, valid.getEmail(), valid.getPhone(), valid.getPassword(), valid.getCountry(), valid.getCustomerType(), valid.getIdentityType(), valid.getIdentityValue())));
        assertThrows(IllegalArgumentException.class, () -> registration.register(copy(valid, valid.getFullName(), "bad", valid.getPhone(), valid.getPassword(), valid.getCountry(), valid.getCustomerType(), valid.getIdentityType(), valid.getIdentityValue())));
        assertThrows(IllegalArgumentException.class, () -> registration.register(copy(valid, valid.getFullName(), valid.getEmail(), null, valid.getPassword(), valid.getCountry(), valid.getCustomerType(), valid.getIdentityType(), valid.getIdentityValue())));
        assertThrows(IllegalArgumentException.class, () -> registration.register(copy(valid, valid.getFullName(), valid.getEmail(), valid.getPhone(), null, valid.getCountry(), valid.getCustomerType(), valid.getIdentityType(), valid.getIdentityValue())));
        assertThrows(IllegalArgumentException.class, () -> registration.register(copy(valid, valid.getFullName(), valid.getEmail(), valid.getPhone(), valid.getPassword(), null, valid.getCustomerType(), valid.getIdentityType(), valid.getIdentityValue())));
        assertThrows(IllegalArgumentException.class, () -> registration.register(copy(valid, valid.getFullName(), valid.getEmail(), valid.getPhone(), valid.getPassword(), valid.getCountry(), null, valid.getIdentityType(), valid.getIdentityValue())));
        assertThrows(IllegalArgumentException.class, () -> registration.register(copy(valid, valid.getFullName(), valid.getEmail(), valid.getPhone(), valid.getPassword(), valid.getCountry(), valid.getCustomerType(), null, valid.getIdentityValue())));
        assertThrows(IllegalArgumentException.class, () -> registration.register(copy(valid, valid.getFullName(), valid.getEmail(), valid.getPhone(), valid.getPassword(), valid.getCountry(), valid.getCustomerType(), valid.getIdentityType(), null)));
    }

    @Test
    void promotionCoversCreationAndApplicabilityBranches() {
        LocalDate today = LocalDate.now();
        assertThrows(IllegalArgumentException.class, () -> promotions.createPromotion("list1", "BAD", 0, BigDecimal.ONE, today, today));
        assertThrows(IllegalArgumentException.class, () -> promotions.createPromotion("list1", "BAD", 101, BigDecimal.ONE, today, today));
        assertThrows(IllegalArgumentException.class, () -> promotions.createPromotion("list1", "BAD", 10, BigDecimal.ONE, today.plusDays(1), today));
        assertEquals(BigDecimal.TEN, promotions.calculateDiscountedTotal(null, "list1", BigDecimal.TEN, today));
        assertEquals(BigDecimal.TEN, promotions.calculateDiscountedTotal("", "list1", BigDecimal.TEN, today));
        assertThrows(IllegalArgumentException.class, () -> promotions.calculateDiscountedTotal("missing", "list1", BigDecimal.TEN, today));
        Promotion open = promotions.createPromotion("list1", "OPEN", 10, null, today.minusDays(1), today.plusDays(1));
        assertEquals(new BigDecimal("9.0"), promotions.calculateDiscountedTotal("OPEN", "list1", BigDecimal.TEN, today));
        assertThrows(IllegalArgumentException.class, () -> promotions.calculateDiscountedTotal("OPEN", "other", BigDecimal.TEN, today));
        open.setStatus(PromotionStatus.INACTIVE);
        assertThrows(IllegalStateException.class, () -> promotions.calculateDiscountedTotal("OPEN", "list1", BigDecimal.TEN, today));
    }

    @Test
    void toolsCoversNullAndEmptyInputs() {
        assertThrows(IllegalArgumentException.class, () -> tools.calculateEqualSplit(null, 2));
        assertThrows(IllegalArgumentException.class, () -> tools.calculateEqualSplit(BigDecimal.ZERO, 2));
        assertThrows(IllegalArgumentException.class, () -> tools.calculateEqualSplit(BigDecimal.ONE, 0));
        assertThrows(IllegalArgumentException.class, () -> tools.calculateItemizedSplit(null));
        assertThrows(IllegalArgumentException.class, () -> tools.calculateItemizedSplit(Map.of()));
        Map<String, List<BigDecimal>> mixedItems = new java.util.HashMap<>();
        mixedItems.put("Empty", null);
        mixedItems.put("Mixed", java.util.Arrays.asList(null, BigDecimal.ZERO));
        assertEquals(BigDecimal.ZERO, tools.calculateItemizedSplit(mixedItems).get("Mixed"));
        assertThrows(IllegalArgumentException.class, () -> tools.selectRandomPayer(null));
        assertThrows(IllegalArgumentException.class, () -> tools.selectRandomPayer(List.of()));
    }

    @Test
    void authCoversIdentityRulesAndVerificationOutcome() {
        AuthService auth = new AuthService(users, new FakeIdentityVerificationService());
        User valid = user("auth1", IdentityStatus.UNVERIFIED, BigDecimal.ZERO);
        assertEquals(IdentityStatus.VERIFIED, auth.registerUser(valid).getIdentityStatus());

        User missingType = user("auth2", IdentityStatus.UNVERIFIED, BigDecimal.ZERO);
        missingType.setCustomerType(null);
        assertThrows(IllegalArgumentException.class, () -> auth.registerUser(missingType));

        User ethiopianPassport = user("auth3", IdentityStatus.UNVERIFIED, BigDecimal.ZERO);
        ethiopianPassport.setIdentityType(IdentityType.PASSPORT);
        assertThrows(IllegalArgumentException.class, () -> auth.registerUser(ethiopianPassport));

        User foreignFayda = user("auth4", IdentityStatus.UNVERIFIED, BigDecimal.ZERO);
        foreignFayda.setCustomerType(CustomerType.FOREIGNER);
        foreignFayda.setIdentityType(IdentityType.FAYDA);
        assertThrows(IllegalArgumentException.class, () -> auth.registerUser(foreignFayda));

        User invalidIdentity = user("auth5", IdentityStatus.UNVERIFIED, BigDecimal.ZERO);
        invalidIdentity.setIdentityValue("INVALID");
        assertEquals(IdentityStatus.UNVERIFIED, auth.registerUser(invalidIdentity).getIdentityStatus());
    }

    private User user(String id, IdentityStatus status, BigDecimal balance) {
        return new User(id, "Test User", id + "@example.com", "+251900000000", "Password123!", "Ethiopia",
                CustomerType.ETHIOPIAN, IdentityType.FAYDA, "TEST-FAYDA-VALID", status, balance);
    }

    private RegisterRequest registrationRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("New User");
        request.setEmail("new-user@example.com");
        request.setPhone("+251911111111");
        request.setPassword("Password123!");
        request.setCountry("Ethiopia");
        request.setCustomerType(CustomerType.ETHIOPIAN);
        request.setIdentityType(IdentityType.FAYDA);
        request.setIdentityValue("TEST-FAYDA-VALID");
        return request;
    }

    private RegisterRequest copy(RegisterRequest source, String name, String email, String phone, String password,
                                 String country, CustomerType customerType, IdentityType identityType, String identityValue) {
        RegisterRequest copy = new RegisterRequest();
        copy.setFullName(name);
        copy.setEmail(email);
        copy.setPhone(phone);
        copy.setPassword(password);
        copy.setCountry(country);
        copy.setCustomerType(customerType);
        copy.setIdentityType(identityType);
        copy.setIdentityValue(identityValue);
        return copy;
    }
}
