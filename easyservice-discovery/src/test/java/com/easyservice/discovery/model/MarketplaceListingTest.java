package com.easyservice.discovery.model;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class MarketplaceListingTest {

    @Test
    void preservesCoordinatesAndGalleryImages() {
        MarketplaceListing listing = new MarketplaceListing(
                "osm-1", "Cafe", ListingCategory.STORE, "Ethiopia", "Addis Ababa", "Bole",
                "Bole Road", "Coffee nearby", "https://example.test", "+251900000000",
                "https://example.test/image.jpg", null, "ETB", null, "NOT_PUBLISHED",
                SourceType.IMPORTED, "OpenStreetMap", "https://www.openstreetmap.org/node/1",
                Instant.now(), List.of("cafe"), List.of("https://example.test/image.jpg"),
                9.0123, 38.7612);

        assertEquals(9.0123, listing.latitude());
        assertEquals(38.7612, listing.longitude());
        assertEquals(1, listing.imageUrls().size());
    }

    @Test
    void backwardsConstructorLeavesCoordinatesUnset() {
        MarketplaceListing listing = new MarketplaceListing(
                "legacy", "Legacy place", ListingCategory.STORE, "Ethiopia", "Addis Ababa", "Bole",
                null, "A place", null, null, null, null, "ETB", null, "NOT_PUBLISHED",
                SourceType.IMPORTED, "Legacy source", null, Instant.now(), List.of());

        assertNull(listing.latitude());
        assertNull(listing.longitude());
        assertEquals(0, listing.imageUrls().size());
    }
}