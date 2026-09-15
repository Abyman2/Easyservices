package com.easyservice.backend.repository;

import com.easyservice.backend.model.Listing;
import com.easyservice.backend.model.enums.ListingCategory;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryListingRepository implements ListingRepository {
    private final Map<String, Listing> store = new ConcurrentHashMap<>();

    @Override
    public Listing save(Listing listing) {
        if (listing.getId() == null || listing.getId().isEmpty()) {
            listing.setId(java.util.UUID.randomUUID().toString());
        }
        store.put(listing.getId(), listing);
        return listing;
    }

    @Override
    public Optional<Listing> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Listing> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public List<Listing> findByProviderId(String providerId) {
        return store.values().stream()
                .filter(l -> l.getProviderId() != null && l.getProviderId().equals(providerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Listing> findByCategory(ListingCategory category) {
        return store.values().stream()
                .filter(l -> l.getCategory() == category)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        store.remove(id);
    }

    @Override
    public void clear() {
        store.clear();
    }
}
