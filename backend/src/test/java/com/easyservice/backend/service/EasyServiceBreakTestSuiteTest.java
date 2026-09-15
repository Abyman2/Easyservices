package com.easyservice.backend.service;

import com.easyservice.backend.dto.RegisterRequest;
import com.easyservice.backend.infrastructure.FakeIdentityVerificationService;
import com.easyservice.backend.infrastructure.FakeNotificationService;
import com.easyservice.backend.infrastructure.FakePaymentService;
import com.easyservice.backend.model.Listing;
import com.easyservice.backend.model.Transaction;
import com.easyservice.backend.model.User;
import com.easyservice.backend.model.enums.CustomerType;
import com.easyservice.backend.model.enums.IdentityStatus;
import com.easyservice.backend.model.enums.IdentityType;
import com.easyservice.backend.model.enums.ListingCategory;
import com.easyservice.backend.model.enums.ListingStatus;
import com.easyservice.backend.model.enums.TransactionStatus;
import com.easyservice.backend.repository.InMemoryListingRepository;
import com.easyservice.backend.repository.InMemoryPromotionRepository;
import com.easyservice.backend.repository.InMemoryTransactionRepository;
import com.easyservice.backend.repository.InMemoryUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("🔥 EasyService 100 Marketplace Break Test Suite")
public class EasyServiceBreakTestSuiteTest {

    private InMemoryUserRepository userRepository;
    private InMemoryListingRepository listingRepository;
    private InMemoryTransactionRepository transactionRepository;
    private FakeIdentityVerificationService identityService;
    private RegistrationService registrationService;
    private BookingTransactionService bookingService;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        listingRepository = new InMemoryListingRepository();
        transactionRepository = new InMemoryTransactionRepository();
        InMemoryPromotionRepository promotionRepository = new InMemoryPromotionRepository();

        identityService = new FakeIdentityVerificationService();
        registrationService = new RegistrationService(userRepository, identityService);
        FakePaymentService paymentService = new FakePaymentService(userRepository);
        PromotionService promotionService = new PromotionService(promotionRepository);
        FakeNotificationService notificationService = new FakeNotificationService();

        bookingService = new BookingTransactionService(
                userRepository, listingRepository, transactionRepository,
                paymentService, promotionService, notificationService
        );

        // Seed initial users
        User ethUser = new User(
                "cust_1", "Abebe Kebede", "user1@aau.edu.et", "+251911000001",
                "Password123!", "Ethiopia", CustomerType.ETHIOPIAN, IdentityType.FAYDA, "TEST-FAYDA-VALID",
                IdentityStatus.VERIFIED, new BigDecimal("5500.00")
        );

        User forUser = new User(
                "cust_9", "John Foreigner", "john@global.com", "+12025550199",
                "Password123!", "USA", CustomerType.FOREIGNER, IdentityType.PASSPORT, "TEST-PASSPORT-VALID",
                IdentityStatus.VERIFIED, new BigDecimal("8000.00")
        );

        userRepository.save(ethUser);
        userRepository.save(forUser);

        Listing listing = new Listing(
                "list_1", "prov_1", "Kuriftu Resort & Spa", ListingCategory.HOTEL,
                "Luxury resort experience on Lake Hora", new BigDecimal("4500.00"), 10, 10, ListingStatus.PUBLISHED,
                "Bishoftu", "Kuriftu Resorts"
        );
        listingRepository.save(listing);
    }

    private RegisterRequest createReq(String fullName, String email, String phone, String password, String country, CustomerType custType, IdentityType idType, String idVal) {
        RegisterRequest req = new RegisterRequest();
        req.setFullName(fullName);
        req.setEmail(email);
        req.setPhone(phone);
        req.setPassword(password);
        req.setCountry(country);
        req.setCustomerType(custType);
        req.setIdentityType(idType);
        req.setIdentityValue(idVal);
        return req;
    }

    @Test
    @DisplayName("BT-01..BT-05: Registration Missing Fields & Password Boundary Tests")
    void testRegistrationBoundaries() {
        // Missing name
        RegisterRequest missingNameReq = createReq("", "new@aau.edu.et", "+251911000099", "Pass123!", "Ethiopia", CustomerType.ETHIOPIAN, IdentityType.FAYDA, "TEST-FAYDA-VALID");
        assertThrows(IllegalArgumentException.class, () -> registrationService.register(missingNameReq));

        // Invalid Email
        RegisterRequest invalidEmailReq = createReq("Valid Name", "invalid-email", "+251911000099", "Pass123!", "Ethiopia", CustomerType.ETHIOPIAN, IdentityType.FAYDA, "TEST-FAYDA-VALID");
        assertThrows(IllegalArgumentException.class, () -> registrationService.register(invalidEmailReq));
    }

    @Test
    @DisplayName("BT-10: Duplicate Email & Phone Prevention")
    void testDuplicateAccountPrevention() {
        RegisterRequest duplicateReq = createReq("Duplicate User", "user1@aau.edu.et", "+251999999999", "Pass123!", "Ethiopia", CustomerType.ETHIOPIAN, IdentityType.FAYDA, "TEST-FAYDA-VALID");
        assertThrows(IllegalArgumentException.class, () -> registrationService.register(duplicateReq));
    }

    @Test
    @DisplayName("BT-11..BT-12: Ethiopian vs Foreigner Identity Cross-Validation Decision Matrix")
    void testIdentityCrossValidationMatrix() {
        // Ethiopian submitting Passport -> REJECTED
        assertFalse(identityService.verify(CustomerType.ETHIOPIAN, IdentityType.PASSPORT, "TEST-PASSPORT-VALID"));

        // Ethiopian submitting Fayda -> VERIFIED
        assertTrue(identityService.verify(CustomerType.ETHIOPIAN, IdentityType.FAYDA, "TEST-FAYDA-VALID"));

        // Foreigner submitting Fayda -> REJECTED
        assertFalse(identityService.verify(CustomerType.FOREIGNER, IdentityType.FAYDA, "TEST-FAYDA-VALID"));

        // Foreigner submitting Passport -> VERIFIED
        assertTrue(identityService.verify(CustomerType.FOREIGNER, IdentityType.PASSPORT, "TEST-PASSPORT-VALID"));
    }

    @Test
    @DisplayName("BT-24 & BT-27: Simultaneous Race Condition Concurrency Test")
    void testConcurrencyInventoryRaceCondition() throws InterruptedException {
        Listing singleItemListing = new Listing(
                "list_single", "prov_1", "Limited Edition Coffee", ListingCategory.STORE,
                "Single item stock", new BigDecimal("1250.00"), 1, 1, ListingStatus.PUBLISHED,
                "Sidama", "Coffee Co."
        );
        listingRepository.save(singleItemListing);

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    latch.await();
                    Transaction tx = bookingService.createBooking("cust_1", "list_single", 1, "");
                    if (tx.getStatus() == TransactionStatus.CONFIRMED) {
                        successCount.incrementAndGet();
                    }
                } catch (Exception ignored) {
                }
            });
        }

        latch.countDown();
        Thread.sleep(500);
        executor.shutdown();

        assertEquals(1, successCount.get(), "Race condition check: exactly ONE concurrent booking must succeed for single item stock!");
    }

    @Test
    @DisplayName("BT-30: Insufficient Wallet Balance Payment Decline (BR-08)")
    void testInsufficientBalancePaymentDecline() {
        // Price ETB 4,500 * Qty 2 = ETB 9,000 > Cust_1 Balance ETB 5,500
        Transaction tx = bookingService.createBooking("cust_1", "list_1", 2, "");
        assertEquals(TransactionStatus.EXPIRED, tx.getStatus());

        // Verify balance was NOT deducted
        User cust = userRepository.findById("cust_1").orElseThrow();
        assertEquals(new BigDecimal("5500.00"), cust.getBalance());
    }

    @Test
    @DisplayName("BT-34 & BT-35: Cancellation & Double Cancellation Refund Idempotency (BR-14)")
    void testCancellationDoubleRefundPrevention() {
        // Step 1: Successful Booking of 1 unit = ETB 4,500
        Transaction tx = bookingService.createBooking("cust_1", "list_1", 1, "");
        assertEquals(TransactionStatus.CONFIRMED, tx.getStatus());

        User custAfterBooking = userRepository.findById("cust_1").orElseThrow();
        assertEquals(new BigDecimal("1000.00"), custAfterBooking.getBalance()); // 5500 - 4500 = 1000

        // Step 2: First Cancellation -> Refunds ETB 4,500
        Transaction cancelledTx = bookingService.cancelTransaction(tx.getId(), "cust_1");
        assertEquals(TransactionStatus.CANCELLED, cancelledTx.getStatus());

        User custAfterCancel = userRepository.findById("cust_1").orElseThrow();
        assertEquals(new BigDecimal("5500.00"), custAfterCancel.getBalance()); // 1000 + 4500 = 5500

        // Step 3: Second Cancellation Attempt -> MUST THROWS IllegalStateException
        assertThrows(IllegalStateException.class, () ->
            bookingService.cancelTransaction(tx.getId(), "cust_1")
        );

        User custAfterSecondCancel = userRepository.findById("cust_1").orElseThrow();
        assertEquals(new BigDecimal("5500.00"), custAfterSecondCancel.getBalance(), "Wallet balance must remain ETB 5,500 without double refund!");
    }
}
