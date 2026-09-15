package com.easyservice.discovery.model;

import java.time.Instant;

public record DiscoveredListing(
        String id,
        String name,
        ListingCategory category,
        String area,
        String city,
        String address,
        String description,
        String website,
        String phone,
        String imageUrl,
        String price,
        String currency,
        String priceSource,
        SourceType sourceType,
        String sourceUrl,
        Instant lastChecked
) {}
