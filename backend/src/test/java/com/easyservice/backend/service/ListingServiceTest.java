package com.easyservice.backend.service;

import com.easyservice.backend.model.Listing;
import com.easyservice.backend.model.enums.ListingCategory;
import com.easyservice.backend.model.enums.ListingStatus;
import com.easyservice.backend.repository.InMemoryListingRepository;
import com.easyservice.backend.repository.ListingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ListingServiceTest {

    private ListingRepository listingRepository;
    private ListingService listingService;

    @BeforeEach
    void setUp() {
        listingRepository = new InMemoryListingRepository();
        listingService = new ListingService(listingRepository);
    }

    @Test
    @DisplayName("EP & BVA: Valid listing creation with price > 0 and capacity > 0")
    void createListing_Valid_Success() {
        Listing listing = listingService.createListing("provider1", "Boutique Hotel Room",
                ListingCategory.HOTEL, "Luxury suite", BigDecimal.valueOf(1500.00), 10);

        assertNotNull(listing.getId());
        assertEquals(ListingStatus.DRAFT, listing.getStatus());
        assertEquals(10, listing.getAvailableQuantity());
    }

    @Test
    @DisplayName("BVA Negative: Price = 0 should throw IllegalArgumentException (BR-05)")
    void createListing_PriceZero_ThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                listingService.createListing("provider1", "Hotel Room", ListingCategory.HOTEL,
                        "Desc", BigDecimal.ZERO, 10));
        assertTrue(exception.getMessage().contains("Price must be greater than zero"));
    }

    @ParameterizedTest
    @ValueSource(doubles = {-10.0, -0.01})
    @DisplayName("EP Negative: Negative prices throw IllegalArgumentException (BR-05)")
    void createListing_NegativePrice_ThrowsException(double negativePrice) {
        assertThrows(IllegalArgumentException.class, () ->
                listingService.createListing("provider1", "Hotel Room", ListingCategory.HOTEL,
                        "Desc", BigDecimal.valueOf(negativePrice), 10));
    }

    @Test
    @DisplayName("BVA Negative: Capacity = 0 throws IllegalArgumentException (BR-06)")
    void createListing_CapacityZero_ThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                listingService.createListing("provider1", "Hotel Room", ListingCategory.HOTEL,
                        "Desc", BigDecimal.valueOf(100.0), 0));
    }

    @Test
    @DisplayName("BR-18 Authorization: Provider can publish own listing")
    void publishListing_OwnListing_Success() {
        Listing draft = listingService.createListing("provider1", "Event Ticket",
                ListingCategory.EVENT, "Concert", BigDecimal.valueOf(300.0), 50);

        Listing published = listingService.publishListing(draft.getId(), "provider1");
        assertEquals(ListingStatus.PUBLISHED, published.getStatus());
    }

    @Test
    @DisplayName("BR-18 Security: Provider cannot publish another provider's listing")
    void publishListing_WrongProvider_ThrowsSecurityException() {
        Listing draft = listingService.createListing("provider1", "Event Ticket",
                ListingCategory.EVENT, "Concert", BigDecimal.valueOf(300.0), 50);

        assertThrows(SecurityException.class, () ->
                listingService.publishListing(draft.getId(), "provider2"));
    }

    @Test
    @DisplayName("BR-04 Customer Visibility: Only published listings are returned")
    void getCustomerVisibleListings_FiltersDraftAndUnpublished() {
        Listing draft = listingService.createListing("p1", "Draft Car", ListingCategory.CAR_RENTAL, "Desc", BigDecimal.valueOf(500), 2);
        assertEquals(ListingStatus.DRAFT, draft.getStatus()); // Draft stays unpublished
        Listing pub = listingService.createListing("p1", "Published Car", ListingCategory.CAR_RENTAL, "Desc", BigDecimal.valueOf(500), 2);
        listingService.publishListing(pub.getId(), "p1");

        List<Listing> visible = listingService.getCustomerVisibleListings();
        assertEquals(1, visible.size());
        assertEquals(pub.getId(), visible.get(0).getId());
    }
}
