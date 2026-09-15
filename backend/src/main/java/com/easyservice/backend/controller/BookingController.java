package com.easyservice.backend.controller;

import com.easyservice.backend.model.Transaction;
import com.easyservice.backend.service.BookingTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingTransactionService bookingTransactionService;

    public BookingController(BookingTransactionService bookingTransactionService) {
        this.bookingTransactionService = bookingTransactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> createBooking(@RequestParam String customerId,
                                                      @RequestParam String listingId,
                                                      @RequestParam int quantity,
                                                      @RequestParam(required = false) String promoCode,
                                                      @RequestParam(required = false) LocalDate startDate,
                                                      @RequestParam(required = false) LocalDate endDate,
                                                      @RequestParam(required = false) String customerName,
                                                      @RequestParam(required = false) String customerEmail,
                                                      @RequestParam(required = false) String customerPhone,
                                                      @RequestParam(required = false) String pickupTime,
                                                      @RequestParam(required = false) String returnTime,
                                                      @RequestParam(required = false) String driverOption) {
        Transaction tx = bookingTransactionService.createBooking(
                customerId, listingId, quantity, promoCode,
                startDate, endDate, customerName, customerEmail, customerPhone,
                pickupTime, returnTime, driverOption
        );
        return ResponseEntity.ok(tx);
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Transaction>> getCustomerBookings(@PathVariable String customerId) {
        return ResponseEntity.ok(bookingTransactionService.getCustomerBookings(customerId));
    }

    @GetMapping("/provider/{providerId}")
    public ResponseEntity<List<Transaction>> getProviderBookings(@PathVariable String providerId) {
        return ResponseEntity.ok(bookingTransactionService.getProviderBookings(providerId));
    }

    @GetMapping("/listing/{listingId}")
    public ResponseEntity<List<Transaction>> getListingBookings(@PathVariable String listingId) {
        return ResponseEntity.ok(bookingTransactionService.getListingBookings(listingId));
    }

    @PutMapping("/{id}/provider-status")
    public ResponseEntity<Transaction> updateProviderStatus(@PathVariable String id,
                                                              @RequestParam String providerId,
                                                              @RequestParam String providerStatus) {
        return ResponseEntity.ok(bookingTransactionService.updateProviderStatus(id, providerId, providerStatus));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<Transaction> completeBooking(@PathVariable String id) {
        Transaction tx = bookingTransactionService.completeTransaction(id);
        return ResponseEntity.ok(tx);
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Transaction> cancelBooking(@PathVariable String id, @RequestParam String customerId) {
        Transaction tx = bookingTransactionService.cancelTransaction(id, customerId);
        return ResponseEntity.ok(tx);
    }
}
