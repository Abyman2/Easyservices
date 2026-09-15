package com.easyservice.backend.repository;

import com.easyservice.backend.model.Listing;
import com.easyservice.backend.model.enums.ListingCategory;
import java.util.List;
import java.util.Optional;

public interface ListingRepository {
    Listing save(Listing listing);
    Optional<Listing> findById(String id);
    List<Listing> findAll();
    List<Listing> findByProviderId(String providerId);
    List<Listing> findByCategory(ListingCategory category);
    void deleteById(String id);
    void clear();
}
