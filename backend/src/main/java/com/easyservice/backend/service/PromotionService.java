package com.easyservice.backend.service;

import com.easyservice.backend.model.Promotion;
import com.easyservice.backend.model.enums.PromotionStatus;
import com.easyservice.backend.repository.PromotionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class PromotionService {
    private final PromotionRepository promotionRepository;

    public PromotionService(PromotionRepository promotionRepository) {
        this.promotionRepository = promotionRepository;
    }

    public Promotion createPromotion(String listingId, String code, double discountPercentage, 
                                     BigDecimal minimumAmount, LocalDate startDate, LocalDate endDate) {
        if (discountPercentage <= 0 || discountPercentage > 100) {
            throw new IllegalArgumentException("Discount percentage must be between 0 and 100");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date");
        }

        Promotion promotion = new Promotion(
                null,
                listingId,
                code,
                discountPercentage,
                minimumAmount,
                startDate,
                endDate,
                PromotionStatus.ACTIVE
        );

        return promotionRepository.save(promotion);
    }

    public BigDecimal calculateDiscountedTotal(String promoCode, String listingId, BigDecimal baseAmount, LocalDate currentDate) {
        if (promoCode == null || promoCode.trim().isEmpty()) {
            return baseAmount;
        }

        Optional<Promotion> promoOpt = promotionRepository.findByCode(promoCode);
        if (promoOpt.isEmpty()) {
            throw new IllegalArgumentException("Invalid promotion code");
        }

        Promotion promo = promoOpt.get();

        // BR-12: Expired or inactive promotion validation
        if (promo.getStatus() != PromotionStatus.ACTIVE || currentDate.isBefore(promo.getStartDate()) || currentDate.isAfter(promo.getEndDate())) {
            throw new IllegalStateException("Promotion code is expired or inactive (BR-12)");
        }

        if (promo.getListingId() != null && !promo.getListingId().equals(listingId)) {
            throw new IllegalArgumentException("Promotion is not applicable to this listing");
        }

        // BR-11: Minimum amount threshold check
        if (promo.getMinimumAmount() != null && baseAmount.compareTo(promo.getMinimumAmount()) < 0) {
            throw new IllegalArgumentException("Booking amount does not meet promotion minimum threshold (BR-11)");
        }

        BigDecimal discountMultiplier = BigDecimal.valueOf(1.0 - (promo.getDiscountPercentage() / 100.0));
        return baseAmount.multiply(discountMultiplier);
    }
}
