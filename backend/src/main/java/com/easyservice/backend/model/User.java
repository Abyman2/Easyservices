package com.easyservice.backend.model;

import com.easyservice.backend.model.enums.CustomerType;
import com.easyservice.backend.model.enums.IdentityStatus;
import com.easyservice.backend.model.enums.IdentityType;
import java.math.BigDecimal;

public class User {
    private String id;
    private String fullName;
    private String email;
    private String phone;
    private String password;
    private String country;
    private CustomerType customerType;
    private IdentityType identityType;
    private String identityValue;
    private IdentityStatus identityStatus;
    private BigDecimal balance;

    // Empty Constructor
    public User() {
    }

    // Full Constructor
    public User(String id, String fullName, String email, String phone, String password, 
                String country, CustomerType customerType, IdentityType identityType, 
                String identityValue, IdentityStatus identityStatus, BigDecimal balance) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.country = country;
        this.customerType = customerType;
        this.identityType = identityType;
        this.identityValue = identityValue;
        this.identityStatus = identityStatus;
        this.balance = balance;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public CustomerType getCustomerType() { return customerType; }
    public void setCustomerType(CustomerType customerType) { this.customerType = customerType; }

    public IdentityType getIdentityType() { return identityType; }
    public void setIdentityType(IdentityType identityType) { this.identityType = identityType; }

    public String getIdentityValue() { return identityValue; }
    public void setIdentityValue(String identityValue) { this.identityValue = identityValue; }

    public IdentityStatus getIdentityStatus() { return identityStatus; }
    public void setIdentityStatus(IdentityStatus identityStatus) { this.identityStatus = identityStatus; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
}
