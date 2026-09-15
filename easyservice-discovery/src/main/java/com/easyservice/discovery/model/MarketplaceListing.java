package com.easyservice.discovery.model;

import java.time.Instant;
import java.util.List;

public record MarketplaceListing(
        String id,
        String name,
        ListingCategory category,
        String country,
        String city,
        String area,
        String address,
        String description,
        String website,
        String phone,
        String imageUrl,
        String price,
        String currency,
        String priceUnit,
        String priceSource,
        SourceType sourceType,
        String sourceName,
        String sourceUrl,
        Instant lastChecked,
        List<String> tags,
        List<String> imageUrls
) {

    /**
     * Backwards-compatible constructor for existing discovery records.
     * If only one image is supplied, it is also used as the first gallery image.
     */
    public MarketplaceListing(
            String id,
            String name,
            ListingCategory category,
            String country,
            String city,
            String area,
            String address,
            String description,
            String website,
            String phone,
            String imageUrl,
            String price,
            String currency,
            String priceUnit,
            String priceSource,
            SourceType sourceType,
            String sourceName,
            String sourceUrl,
            Instant lastChecked,
            List<String> tags
    ) {
        this(
                id,
                name,
                category,
                country,
                city,
                area,
                address,
                description,
                website,
                phone,
                imageUrl,
                price,
                currency,
                priceUnit,
                priceSource,
                sourceType,
                sourceName,
                sourceUrl,
                lastChecked,
                tags,
                imageUrl == null || imageUrl.isBlank()
                        ? List.of()
                        : List.of(imageUrl)
        );
    }
}