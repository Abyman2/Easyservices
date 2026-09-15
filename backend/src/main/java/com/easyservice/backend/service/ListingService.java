package com.easyservice.backend.service;

import com.easyservice.backend.model.Listing;
import com.easyservice.backend.model.enums.ListingCategory;
import com.easyservice.backend.model.enums.ListingStatus;
import com.easyservice.backend.repository.ListingRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class ListingService {
    private final ListingRepository listingRepository;

    public ListingService(ListingRepository listingRepository) {
        this.listingRepository = listingRepository;
    }

    public Listing createListing(String providerId, String title, ListingCategory category, 
                                 String description, BigDecimal price, int capacity) {
        // BR-05: Price must be greater than zero
        if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Price must be greater than zero (BR-05)");
        }

        // BR-06: Capacity must be greater than zero
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than zero (BR-06)");
        }

        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Listing title is required");
        }

        Listing listing = new Listing(
                null,
                providerId,
                title,
                category,
                description,
                price,
                capacity,
                capacity, // Initial available quantity equals capacity
                ListingStatus.DRAFT // Created as draft initially
        );

        return listingRepository.save(listing);
    }

    public Listing publishListing(String listingId, String providerId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        // BR-18: Authorization - provider ownership check
        if (!listing.getProviderId().equals(providerId)) {
            throw new SecurityException("Unauthorized: Cannot modify another provider's listing (BR-18)");
        }

        // BR-17: Validation check for incomplete listings
        if (listing.getPrice() == null || listing.getPrice().compareTo(BigDecimal.ZERO) <= 0 
                || listing.getCapacity() <= 0
                || listing.getAvailableQuantity() < 0
                || listing.getAvailableQuantity() > listing.getCapacity()) {
            throw new IllegalStateException("Cannot publish an incomplete or invalid listing (BR-17)");
        }

        listing.setStatus(ListingStatus.PUBLISHED);
        return listingRepository.save(listing);
    }

    public Listing unpublishListing(String listingId, String providerId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        if (!listing.getProviderId().equals(providerId)) {
            throw new SecurityException("Unauthorized: Cannot modify another provider's listing (BR-18)");
        }

        listing.setStatus(ListingStatus.UNPUBLISHED);
        return listingRepository.save(listing);
    }

    public List<Listing> getCustomerVisibleListings() {
        // BR-04: Only published listings are available to customers
        return listingRepository.findAll().stream()
                .filter(l -> l.getStatus() == ListingStatus.PUBLISHED)
                .toList();
    }

    public List<Listing> getProviderListings(String providerId) {
        return listingRepository.findByProviderId(providerId);
    }
}
