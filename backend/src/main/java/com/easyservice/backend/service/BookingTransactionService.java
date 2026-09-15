package com.easyservice.backend.service;

import com.easyservice.backend.model.Listing;
import com.easyservice.backend.model.Transaction;
import com.easyservice.backend.model.User;
import com.easyservice.backend.model.enums.IdentityStatus;
import com.easyservice.backend.model.enums.ListingStatus;
import com.easyservice.backend.model.enums.TransactionStatus;
import com.easyservice.backend.repository.ListingRepository;
import com.easyservice.backend.repository.TransactionRepository;
import com.easyservice.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class BookingTransactionService {

    private final UserRepository userRepository;
    private final ListingRepository listingRepository;
    private final TransactionRepository transactionRepository;
    private final PaymentService paymentService;
    private final PromotionService promotionService;
    private final NotificationService notificationService;

    public BookingTransactionService(UserRepository userRepository,
                                     ListingRepository listingRepository,
                                     TransactionRepository transactionRepository,
                                     PaymentService paymentService,
                                     PromotionService promotionService,
                                     NotificationService notificationService) {
        this.userRepository = userRepository;
        this.listingRepository = listingRepository;
        this.transactionRepository = transactionRepository;
        this.paymentService = paymentService;
        this.promotionService = promotionService;
        this.notificationService = notificationService;
    }

    // Backwards-compatible overload used by the existing business-rule tests.
    public synchronized Transaction createBooking(String customerId,
                                                  String listingId,
                                                  int quantity,
                                                  String promoCode) {
        return createBooking(
                customerId,
                listingId,
                quantity,
                promoCode,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

    public synchronized Transaction createBooking(String customerId,
                                                  String listingId,
                                                  int quantity,
                                                  String promoCode,
                                                  LocalDate startDate,
                                                  LocalDate endDate,
                                                  String customerName,
                                                  String customerEmail,
                                                  String customerPhone,
                                                  String pickupTime,
                                                  String returnTime,
                                                  String driverOption) {

        /*
         * Frontend demo users use IDs such as:
         *     user1
         *     user2
         *     user3
         *
         * Backend initialized users use IDs such as:
         *     cust_1
         *     cust_2
         *     cust_3
         *
         * Resolve both formats so the frontend and backend can work together
         * without changing the in-memory architecture.
         */
        String resolvedCustomerId = resolveCustomerId(customerId);

        User customer = userRepository.findById(resolvedCustomerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        if (customer.getIdentityStatus() != IdentityStatus.VERIFIED) {
            throw new IllegalStateException(
                    "Unverified customer cannot initiate a booking (BR-02)"
            );
        }

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        if (listing.getStatus() != ListingStatus.PUBLISHED) {
            throw new IllegalStateException(
                    "Cannot book an unpublished listing (BR-04)"
            );
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Booking quantity must be greater than zero (BR-06)"
            );
        }

        if (listing.getAvailableQuantity() < 0 || quantity > listing.getAvailableQuantity()) {
            throw new IllegalStateException(
                    "Requested quantity exceeds available capacity (BR-07)"
            );
        }

        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new IllegalArgumentException(
                    "End date must be on or after start date"
            );
        }

        /*
         * Date-level exclusivity:
         * once a customer has a confirmed reservation for a listing/day,
         * another booking cannot take the same day.
         */
        if (hasDateConflict(listingId, startDate, endDate)) {
            throw new IllegalStateException(
                    "Already booked on the selected day for this listing"
            );
        }

        BigDecimal baseTotal = listing.getPrice()
                .multiply(BigDecimal.valueOf(quantity));

        BigDecimal finalTotal = promotionService.calculateDiscountedTotal(
                promoCode,
                listingId,
                baseTotal,
                LocalDateTime.now().toLocalDate()
        );

        LocalDateTime now = LocalDateTime.now();

        Transaction transaction = new Transaction(
                null,
                resolvedCustomerId,
                listingId,
                listing.getProviderId(),
                customerName != null
                        ? customerName
                        : customer.getFullName(),
                customerEmail != null
                        ? customerEmail
                        : customer.getEmail(),
                customerPhone != null
                        ? customerPhone
                        : customer.getPhone(),
                quantity,
                finalTotal,
                TransactionStatus.PENDING,
                "PENDING",
                startDate,
                endDate,
                pickupTime,
                returnTime,
                driverOption,
                now
        );

        Transaction savedTx = transactionRepository.save(transaction);

        /*
         * Use the resolved backend customer ID for payment as well.
         */
        PaymentService.PaymentResult paymentResult =
                paymentService.processPayment(
                        resolvedCustomerId,
                        finalTotal
                );

        if (paymentResult.isSuccess()) {

            savedTx.setStatus(TransactionStatus.CONFIRMED);
            savedTx.setProviderStatus("PENDING");

            listing.setAvailableQuantity(
                    listing.getAvailableQuantity() - quantity
            );

            listingRepository.save(listing);

            notificationService.sendNotification(
                    customer.getEmail(),
                    "Booking confirmed for listing: "
                            + listing.getTitle()
                            + ". Total: "
                            + finalTotal
            );

        } else {
            savedTx.setStatus(TransactionStatus.EXPIRED);
        }

        savedTx.setUpdatedAt(LocalDateTime.now());

        return transactionRepository.save(savedTx);
    }

    /**
     * Resolves customer IDs coming from either the frontend demo users
     * or the backend's seeded users.
     *
     * Examples:
     *     user1  -> cust_1
     *     user2  -> cust_2
     *     cust_1 -> cust_1
     */
    private String resolveCustomerId(String customerId) {

        if (customerId == null || customerId.isBlank()) {
            return customerId;
        }

        // Already a valid backend ID.
        if (userRepository.findById(customerId).isPresent()) {
            return customerId;
        }

        // Convert frontend format: user1 -> cust_1
        if (customerId.matches("user\\d+")) {

            String number = customerId.substring(4);
            String backendId = "cust_" + number;

            if (userRepository.findById(backendId).isPresent()) {
                return backendId;
            }
        }

        // Leave the original ID unchanged so the normal
        // "Customer not found" error is returned if it is invalid.
        return customerId;
    }

    private boolean hasDateConflict(String listingId,
                                    LocalDate startDate,
                                    LocalDate endDate) {

        if (startDate == null) {
            return false;
        }

        LocalDate requestedEnd =
                (endDate != null && endDate.isAfter(startDate))
                        ? endDate
                        : startDate.plusDays(1);

        return transactionRepository.findByListingId(listingId).stream()

                .filter(tx ->
                        tx.getStatus() == TransactionStatus.CONFIRMED
                )

                .filter(tx ->
                        !"DECLINED".equalsIgnoreCase(
                                tx.getProviderStatus()
                        )
                )

                .filter(tx ->
                        tx.getStartDate() != null
                )

                .anyMatch(tx -> {

                    LocalDate existingEnd =
                            (tx.getEndDate() != null
                                    && tx.getEndDate()
                                    .isAfter(tx.getStartDate()))
                                    ? tx.getEndDate()
                                    : tx.getStartDate().plusDays(1);

                    return startDate.isBefore(existingEnd)
                            && tx.getStartDate().isBefore(requestedEnd);
                });
    }

    public java.util.List<Transaction> getCustomerBookings(
            String customerId) {

        String resolvedCustomerId = resolveCustomerId(customerId);

        return transactionRepository.findByCustomerId(
                resolvedCustomerId
        );
    }

    public java.util.List<Transaction> getProviderBookings(
            String providerId) {

        return transactionRepository.findAll().stream()
                .filter(tx ->
                        providerId != null
                                && providerId.equals(tx.getProviderId())
                )
                .toList();
    }

    public java.util.List<Transaction> getListingBookings(
            String listingId) {

        return transactionRepository.findByListingId(listingId);
    }

    public synchronized Transaction updateProviderStatus(
            String transactionId,
            String providerId,
            String providerStatus) {

        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Transaction not found"
                        )
                );

        if (providerId == null
                || !providerId.equals(tx.getProviderId())) {

            throw new SecurityException(
                    "Unauthorized: Cannot modify another provider's booking"
            );
        }

        if (!"ACCEPTED".equals(providerStatus)
                && !"DECLINED".equals(providerStatus)) {

            throw new IllegalArgumentException(
                    "Provider status must be ACCEPTED or DECLINED"
            );
        }

        tx.setProviderStatus(providerStatus);
        tx.setUpdatedAt(LocalDateTime.now());

        return transactionRepository.save(tx);
    }

    public Transaction completeTransaction(String transactionId) {

        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Transaction not found"
                        )
                );

        if (tx.getStatus() != TransactionStatus.CONFIRMED) {

            throw new IllegalStateException(
                    "Invalid State Transition: Cannot complete transaction from state "
                            + tx.getStatus()
                            + " (BR-13)"
            );
        }

        tx.setStatus(TransactionStatus.COMPLETED);
        tx.setUpdatedAt(LocalDateTime.now());

        return transactionRepository.save(tx);
    }

    public synchronized Transaction cancelTransaction(
            String transactionId,
            String customerId) {

        String resolvedCustomerId = resolveCustomerId(customerId);

        Transaction tx = transactionRepository.findById(transactionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Transaction not found"
                        )
                );

        if (!tx.getCustomerId().equals(resolvedCustomerId)) {

            throw new SecurityException(
                    "Unauthorized: Cannot cancel another customer's transaction"
            );
        }

        if (tx.getStatus() != TransactionStatus.PENDING
                && tx.getStatus() != TransactionStatus.CONFIRMED) {

            throw new IllegalStateException(
                    "Invalid State Transition: Cannot cancel transaction in state "
                            + tx.getStatus()
                            + " (BR-14)"
            );
        }

        boolean wasConfirmed =
                tx.getStatus() == TransactionStatus.CONFIRMED;

        tx.setStatus(TransactionStatus.CANCELLED);
        tx.setProviderStatus("CANCELLED");
        tx.setUpdatedAt(LocalDateTime.now());

        Transaction cancelledTx =
                transactionRepository.save(tx);

        if (wasConfirmed) {

            Listing listing =
                    listingRepository.findById(tx.getListingId())
                            .orElse(null);

            if (listing != null) {

                                if (listing.getAvailableQuantity() + tx.getQuantity() > listing.getCapacity()) {
                                        throw new IllegalStateException("Cancellation would exceed listing capacity");
                                }

                listing.setAvailableQuantity(
                        listing.getAvailableQuantity()
                                + tx.getQuantity()
                );

                listingRepository.save(listing);
            }

            User customer =
                    userRepository.findById(resolvedCustomerId)
                            .orElse(null);

            if (customer != null
                    && customer.getBalance() != null) {

                customer.setBalance(
                        customer.getBalance()
                                .add(tx.getTotalAmount())
                );

                userRepository.save(customer);
            }
        }

        return cancelledTx;
    }
}