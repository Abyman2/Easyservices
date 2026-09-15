package com.easyservice.discovery.normalizer;

import com.easyservice.discovery.model.*;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;

@Component
public class ListingNormalizer {

    public DiscoveredListing normalize(
            ListingCategory category, String sourceUrl, String name,
            String description, String address, String city, String phone,
            String website, String imageUrl, String price, String currency) {

        String cleanName = clean(name);
        String id = "imported_" + sha1(category + "|" + cleanName + "|" + sourceUrl);
        String cleanPrice = clean(price);

        return new DiscoveredListing(
                id,
                cleanName == null ? "Unnamed listing" : cleanName,
                category,
                clean(address),
                clean(city),
                clean(address),
                clean(description),
                clean(website),
                clean(phone),
                clean(imageUrl),
                cleanPrice,
                clean(currency),
                cleanPrice == null ? "NOT_PUBLISHED" : "IMPORTED",
                SourceType.IMPORTED,
                sourceUrl,
                Instant.now()
        );
    }

    private String clean(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private String sha1(String value) {
        try {
            byte[] bytes = MessageDigest.getInstance("SHA-1")
                    .digest(value.toLowerCase().getBytes(StandardCharsets.UTF_8));
            StringBuilder out = new StringBuilder();
            for (byte b : bytes) out.append(String.format("%02x", b));
            return out.toString();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create discovery ID", e);
        }
    }
}
