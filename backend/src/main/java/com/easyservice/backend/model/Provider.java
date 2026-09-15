package com.easyservice.backend.model;

public class Provider {
    private String id;
    private String businessName;
    private String name;
    private String email;
    private String phone;
    private boolean verified;

    public Provider() {
    }

    public Provider(String id, String businessName, String name, String email, String phone, boolean verified) {
        this.id = id;
        this.businessName = businessName;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.verified = verified;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getBusinessName() { return businessName; }
    public void setBusinessName(String businessName) { this.businessName = businessName; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public boolean isVerified() { return verified; }
    public void setVerified(boolean verified) { this.verified = verified; }
}
