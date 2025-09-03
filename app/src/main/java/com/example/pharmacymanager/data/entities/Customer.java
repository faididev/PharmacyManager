package com.example.pharmacymanager.data.entities;

public class Customer {
    private long id;
    private long userId;
    private String loyaltyPoints;
    private String createdAt;
    private String updatedAt;

    // Constructors
    public Customer() {}
    public Customer(long id, long userId, String loyaltyPoints, String createdAt, String updatedAt) {
        this.id = id;
        this.userId = userId;
        this.loyaltyPoints = loyaltyPoints;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Customer(long userId, String loyaltyPoints) {
        this.userId = userId;
        this.loyaltyPoints = loyaltyPoints;
    }

    // Getters and Setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getUserId() { return userId; }
    public void setUserId(long userId) { this.userId = userId; }

    public String getLoyaltyPoints() { return loyaltyPoints; }
    public void setLoyaltyPoints(String loyaltyPoints) { this.loyaltyPoints = loyaltyPoints; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
