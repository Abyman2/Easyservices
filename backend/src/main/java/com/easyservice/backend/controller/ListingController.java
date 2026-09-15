package com.easyservice.backend.controller;

import com.easyservice.backend.model.Listing;
import com.easyservice.backend.model.enums.ListingCategory;
import com.easyservice.backend.service.ListingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/listings")
public class ListingController {

    private final ListingService listingService;

    public ListingController(ListingService listingService) {
        this.listingService = listingService;
    }

    @GetMapping
    public ResponseEntity<List<Listing>> getPublicListings() {
        return ResponseEntity.ok(listingService.getCustomerVisibleListings());
    }

    @PostMapping
    public ResponseEntity<Listing> createListing(@RequestParam String providerId,
                                                @RequestParam String title,
                                                @RequestParam ListingCategory category,
                                                @RequestParam String description,
                                                @RequestParam BigDecimal price,
                                                @RequestParam int capacity) {
        Listing listing = listingService.createListing(providerId, title, category, description, price, capacity);
        return ResponseEntity.ok(listing);
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<Listing> publishListing(@PathVariable String id, @RequestParam String providerId) {
        Listing published = listingService.publishListing(id, providerId);
        return ResponseEntity.ok(published);
    }

    @PutMapping("/{id}/unpublish")
    public ResponseEntity<Listing> unpublishListing(@PathVariable String id, @RequestParam String providerId) {
        Listing unpublished = listingService.unpublishListing(id, providerId);
        return ResponseEntity.ok(unpublished);
    }
}
