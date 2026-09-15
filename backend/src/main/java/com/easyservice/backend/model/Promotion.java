package com.easyservice.backend.model;

import com.easyservice.backend.model.enums.PromotionStatus;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Promotion {
    private String id;
    private String listingId;
    private String code;
    private double discountPercentage;
    private BigDecimal minimumAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private PromotionStatus status;

    public Promotion() {
    }

    public Promotion(String id, String listingId, String code, double discountPercentage, 
                     BigDecimal minimumAmount, LocalDate startDate, LocalDate endDate, 
                     PromotionStatus status) {
        this.id = id;
        this.listingId = listingId;
        this.code = code;
        this.discountPercentage = discountPercentage;
        this.minimumAmount = minimumAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getListingId() { return listingId; }
    public void setListingId(String listingId) { this.listingId = listingId; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public double getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(double discountPercentage) { this.discountPercentage = discountPercentage; }

    public BigDecimal getMinimumAmount() { return minimumAmount; }
    public void setMinimumAmount(BigDecimal minimumAmount) { this.minimumAmount = minimumAmount; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public PromotionStatus getStatus() { return status; }
    public void setStatus(PromotionStatus status) { this.status = status; }
}
