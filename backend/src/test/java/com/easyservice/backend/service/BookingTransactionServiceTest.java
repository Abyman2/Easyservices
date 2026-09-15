package com.easyservice.backend.service;

import com.easyservice.backend.infrastructure.FakeNotificationService;
import com.easyservice.backend.infrastructure.FakePaymentService;
import com.easyservice.backend.model.Listing;
import com.easyservice.backend.model.Transaction;
import com.easyservice.backend.model.User;
import com.easyservice.backend.model.enums.*;
import com.easyservice.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BookingTransactionServiceTest {

    private UserRepository userRepository;
    private ListingRepository listingRepository;
    private TransactionRepository transactionRepository;
    private FakePaymentService paymentService;
    private PromotionRepository promotionRepository;
    private PromotionService promotionService;
    private FakeNotificationService notificationService;
    private BookingTransactionService bookingService;

    private User verifiedCustomer;
    private User unverifiedCustomer;
    private Listing publishedListing;

    @BeforeEach
    void setUp() {
        userRepository = new InMemoryUserRepository();
        listingRepository = new InMemoryListingRepository();
        transactionRepository = new InMemoryTransactionRepository();
        promotionRepository = new InMemoryPromotionRepository();
        
        paymentService = new FakePaymentService(userRepository);
        promotionService = new PromotionService(promotionRepository);
        notificationService = new FakeNotificationService();

        bookingService = new BookingTransactionService(
                userRepository, listingRepository, transactionRepository,
                paymentService, promotionService, notificationService
        );

        // Seed verified customer with 1000 balance
        verifiedCustomer = new User("cust1", "Abebe Kebede", "abebe@aau.edu.et", "+251911000000",
                "Password123!", "Ethiopia", CustomerType.ETHIOPIAN, IdentityType.FAYDA,
                "FY12345678", IdentityStatus.VERIFIED, BigDecimal.valueOf(1000.00));
        userRepository.save(verifiedCustomer);

        // Seed unverified customer
        unverifiedCustomer = new User("cust2", "John Smith", "john@global.com", "+1234567890",
                "Password123!", "USA", CustomerType.FOREIGNER, IdentityType.PASSPORT,
                "P987654", IdentityStatus.UNVERIFIED, BigDecimal.valueOf(1000.00));
        userRepository.save(unverifiedCustomer);

        // Seed published listing with capacity 5
        publishedListing = new Listing("list1", "prov1", "Hilton Suite", ListingCategory.HOTEL,
                "5-star room", BigDecimal.valueOf(300.00), 5, 5, ListingStatus.PUBLISHED);
        listingRepository.save(publishedListing);
    }

    @Test
    @DisplayName("BR-02 Decision Table: Unverified user booking attempt is REJECTED")
    void createBooking_UnverifiedUser_ThrowsException() {
        assertThrows(IllegalStateException.class, () ->
                bookingService.createBooking("cust2", "list1", 1, null));
    }

    @Test
    @DisplayName("BR-07 BVA: Overbooking quantity > available capacity is REJECTED")
    void createBooking_QuantityExceedsCapacity_ThrowsException() {
        assertThrows(IllegalStateException.class, () ->
                bookingService.createBooking("cust1", "list1", 6, null));
    }

    @Test
    @DisplayName("State Machine & BR-15: Booking success transitions to CONFIRMED, deducts balance & inventory, notifies customer")
    void createBooking_SuccessfulPayment_ConfirmedAndInventoryDeducted() {
        Transaction tx = bookingService.createBooking("cust1", "list1", 2, null); // Total = 600.00

        assertEquals(TransactionStatus.CONFIRMED, tx.getStatus());

        // Assert customer balance deducted (1000 - 600 = 400)
        User updatedCustomer = userRepository.findById("cust1").orElseThrow();
        assertEquals(0, BigDecimal.valueOf(400.00).compareTo(updatedCustomer.getBalance()));

        // Assert inventory reduced (5 - 2 = 3)
        Listing updatedListing = listingRepository.findById("list1").orElseThrow();
        assertEquals(3, updatedListing.getAvailableQuantity());

        // Assert FakeNotificationService spy received notification call
        assertEquals(1, notificationService.getNotificationCount());
        assertTrue(notificationService.getLastNotificationMessage().contains("Hilton Suite"));
    }

    @Test
    @DisplayName("BR-10 & State Machine: Insufficient funds transitions to EXPIRED without deducting inventory")
    void createBooking_InsufficientFunds_TransitionsToExpired() {
        // Customer balance is 1000, attempt booking for 4 items @ 300 = 1200
        Transaction tx = bookingService.createBooking("cust1", "list1", 4, null);

        assertEquals(TransactionStatus.EXPIRED, tx.getStatus());

        // Inventory remains unchanged
        Listing updatedListing = listingRepository.findById("list1").orElseThrow();
        assertEquals(5, updatedListing.getAvailableQuantity());
    }

    @Test
    @DisplayName("BR-13 & BR-14: State Transition CONFIRMED -> CANCELLED restores inventory & balance")
    void cancelTransaction_Confirmed_RestoresInventoryAndBalance() {
        Transaction tx = bookingService.createBooking("cust1", "list1", 2, null); // total 600, balance now 400, avail now 3

        Transaction cancelledTx = bookingService.cancelTransaction(tx.getId(), "cust1");
        assertEquals(TransactionStatus.CANCELLED, cancelledTx.getStatus());

        // Assert balance restored to 1000
        User customer = userRepository.findById("cust1").orElseThrow();
        assertEquals(0, BigDecimal.valueOf(1000.00).compareTo(customer.getBalance()));

        // Assert inventory restored to 5
        Listing listing = listingRepository.findById("list1").orElseThrow();
        assertEquals(5, listing.getAvailableQuantity());
    }

    @Test
    @DisplayName("State Machine Negative Test: COMPLETED -> CANCELLED is explicitly forbidden")
    void cancelTransaction_AlreadyCompleted_ThrowsException() {
        Transaction tx = bookingService.createBooking("cust1", "list1", 1, null);
        bookingService.completeTransaction(tx.getId()); // State is now COMPLETED

        assertThrows(IllegalStateException.class, () ->
                bookingService.cancelTransaction(tx.getId(), "cust1"));
    }

    @Test
    @DisplayName("State Machine Negative Test: COMPLETED -> CONFIRMED is explicitly forbidden")
    void completeTransaction_AlreadyCompleted_ThrowsException() {
        Transaction tx = bookingService.createBooking("cust1", "list1", 1, null);
        bookingService.completeTransaction(tx.getId()); // COMPLETED

        assertThrows(IllegalStateException.class, () ->
                bookingService.completeTransaction(tx.getId()));
    }
}
