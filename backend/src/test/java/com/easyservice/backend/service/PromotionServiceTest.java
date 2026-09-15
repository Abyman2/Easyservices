package com.easyservice.backend.service;

import com.easyservice.backend.model.Promotion;
import com.easyservice.backend.repository.InMemoryPromotionRepository;
import com.easyservice.backend.repository.PromotionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class PromotionServiceTest {

    private PromotionRepository promotionRepository;
    private PromotionService promotionService;

    @BeforeEach
    void setUp() {
        promotionRepository = new InMemoryPromotionRepository();
        promotionService = new PromotionService(promotionRepository);
    }

    @Test
    @DisplayName("BR-11 & BVA: Promotion minimum threshold boundary checks")
    void calculateDiscountedTotal_BVA_Threshold() {
        LocalDate today = LocalDate.now();
        Promotion promo = promotionService.createPromotion("listing1", "SUMMER20", 20.0,
                BigDecimal.valueOf(500.00), today.minusDays(1), today.plusDays(10));
        assertNotNull(promo.getId()); // Verify promotion was created successfully

        // 1. Below threshold (499.99) -> throws IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () ->
                promotionService.calculateDiscountedTotal("SUMMER20", "listing1", BigDecimal.valueOf(499.99), today));

        // 2. Exact threshold (500.00) -> 20% discount applied = 400.00
        BigDecimal exactResult = promotionService.calculateDiscountedTotal("SUMMER20", "listing1", BigDecimal.valueOf(500.00), today);
        assertEquals(0, BigDecimal.valueOf(400.00).compareTo(exactResult));

        // 3. Above threshold (500.01) -> discount applied
        BigDecimal aboveResult = promotionService.calculateDiscountedTotal("SUMMER20", "listing1", BigDecimal.valueOf(1000.00), today);
        assertEquals(0, BigDecimal.valueOf(800.00).compareTo(aboveResult));
    }

    @Test
    @DisplayName("BR-12: Expired promotion throws IllegalStateException")
    void calculateDiscountedTotal_ExpiredPromo_ThrowsException() {
        LocalDate today = LocalDate.now();
        promotionService.createPromotion("listing1", "EXPIRED10", 10.0,
                BigDecimal.valueOf(100.00), today.minusDays(10), today.minusDays(1));

        assertThrows(IllegalStateException.class, () ->
                promotionService.calculateDiscountedTotal("EXPIRED10", "listing1", BigDecimal.valueOf(200.00), today));
    }
}
