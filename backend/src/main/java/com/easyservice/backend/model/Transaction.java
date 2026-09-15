package com.easyservice.backend.model;

import com.easyservice.backend.model.enums.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Transaction {
    private String id;
    private String customerId;
    private String listingId;
    private String providerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private int quantity;
    private BigDecimal totalAmount;
    private TransactionStatus status;
    private String providerStatus;
    private LocalDate startDate;
    private LocalDate endDate;
    private String pickupTime;
    private String returnTime;
    private String driverOption;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Transaction() {
    }

    public Transaction(String id, String customerId, String listingId, int quantity,
                       BigDecimal totalAmount, TransactionStatus status, LocalDateTime createdAt) {
        this(id, customerId, listingId, null, null, null, null, quantity, totalAmount,
                status, "PENDING", null, null, null, null, null, createdAt);
    }

    public Transaction(String id, String customerId, String listingId, String providerId,
                       String customerName, String customerEmail, String customerPhone,
                       int quantity, BigDecimal totalAmount, TransactionStatus status,
                       String providerStatus, LocalDate startDate, LocalDate endDate,
                       String pickupTime, String returnTime, String driverOption,
                       LocalDateTime createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.listingId = listingId;
        this.providerId = providerId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.customerPhone = customerPhone;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.status = status;
        this.providerStatus = providerStatus;
        this.startDate = startDate;
        this.endDate = endDate;
        this.pickupTime = pickupTime;
        this.returnTime = returnTime;
        this.driverOption = driverOption;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }
    public String getListingId() { return listingId; }
    public void setListingId(String listingId) { this.listingId = listingId; }
    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }
    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }
    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public TransactionStatus getStatus() { return status; }
    public void setStatus(TransactionStatus status) { this.status = status; }
    public String getProviderStatus() { return providerStatus; }
    public void setProviderStatus(String providerStatus) { this.providerStatus = providerStatus; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getPickupTime() { return pickupTime; }
    public void setPickupTime(String pickupTime) { this.pickupTime = pickupTime; }
    public String getReturnTime() { return returnTime; }
    public void setReturnTime(String returnTime) { this.returnTime = returnTime; }
    public String getDriverOption() { return driverOption; }
    public void setDriverOption(String driverOption) { this.driverOption = driverOption; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
