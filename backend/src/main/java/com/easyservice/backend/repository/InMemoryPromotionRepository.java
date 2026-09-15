package com.easyservice.backend.repository;

import com.easyservice.backend.model.Promotion;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class InMemoryPromotionRepository implements PromotionRepository {
    private final Map<String, Promotion> store = new ConcurrentHashMap<>();

    @Override
    public Promotion save(Promotion promotion) {
        if (promotion.getId() == null || promotion.getId().isEmpty()) {
            promotion.setId(java.util.UUID.randomUUID().toString());
        }
        store.put(promotion.getId(), promotion);
        return promotion;
    }

    @Override
    public Optional<Promotion> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<Promotion> findByCode(String code) {
        if (code == null) return Optional.empty();
        return store.values().stream()
                .filter(p -> code.equalsIgnoreCase(p.getCode()))
                .findFirst();
    }

    @Override
    public List<Promotion> findByListingId(String listingId) {
        return store.values().stream()
                .filter(p -> p.getListingId() != null && p.getListingId().equals(listingId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Promotion> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void clear() {
        store.clear();
    }
}
