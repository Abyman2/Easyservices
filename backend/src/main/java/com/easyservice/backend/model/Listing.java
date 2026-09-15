package com.easyservice.backend.model;

import com.easyservice.backend.model.enums.ListingCategory;
import com.easyservice.backend.model.enums.ListingStatus;
import java.math.BigDecimal;

public class Listing {
    private String id;
    private String providerId;
    private String title;
    private ListingCategory category;
    private String description;
    private BigDecimal price;
    private int capacity;
    private int availableQuantity;
    private ListingStatus status;
    private String location;
    private String hostName;

    public Listing() {
    }

    public Listing(String id, String providerId, String title, ListingCategory category, 
                   String description, BigDecimal price, int capacity, int availableQuantity, 
                   ListingStatus status) {
        this(id, providerId, title, category, description, price, capacity, availableQuantity, status, "Addis Ababa, Ethiopia", "EasyService Host");
    }

    public Listing(String id, String providerId, String title, ListingCategory category, 
                   String description, BigDecimal price, int capacity, int availableQuantity, 
                   ListingStatus status, String location, String hostName) {
        this.id = id;
        this.providerId = providerId;
        this.title = title;
        this.category = category;
        this.description = description;
        this.price = price;
        this.capacity = capacity;
        this.availableQuantity = availableQuantity;
        this.status = status;
        this.location = location;
        this.hostName = hostName;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProviderId() { return providerId; }
    public void setProviderId(String providerId) { this.providerId = providerId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public ListingCategory getCategory() { return category; }
    public void setCategory(ListingCategory category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public int getAvailableQuantity() { return availableQuantity; }
    public void setAvailableQuantity(int availableQuantity) { this.availableQuantity = availableQuantity; }

    public ListingStatus getStatus() { return status; }
    public void setStatus(ListingStatus status) { this.status = status; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getHostName() { return hostName; }
    public void setHostName(String hostName) { this.hostName = hostName; }
}
