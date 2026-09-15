package com.easyservice.backend.repository;

import com.easyservice.backend.model.Promotion;
import java.util.List;
import java.util.Optional;

public interface PromotionRepository {
    Promotion save(Promotion promotion);
    Optional<Promotion> findById(String id);
    Optional<Promotion> findByCode(String code);
    List<Promotion> findByListingId(String listingId);
    List<Promotion> findAll();
    void clear();
}
