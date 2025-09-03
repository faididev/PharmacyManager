package com.example.pharmacymanager.data.entities;

public class Product {
    private long id;
    private String uuid;
    private String sku;
    private String name;
    private String description;
    private double price;
    private int quantity;
    private double total;
    private String manufactureDate;
    private String expiryDate;
    private long categoryId;
    private String deletedAt;
    private String createdAt;
    private String updatedAt;

    // Constructors

    public Product() {}

    public Product(long id, String uuid, String sku, String name, String description,
                   double price, int quantity, double total, String manufactureDate,
                   String expiryDate, long categoryId, String deletedAt,
                   String createdAt, String updatedAt) {
        this.id = id;
        this.uuid = uuid;
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.total = total;
        this.manufactureDate = manufactureDate;
        this.expiryDate = expiryDate;
        this.categoryId = categoryId;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Product(String name, String description, double price, int quantity, long categoryId) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.categoryId = categoryId;
        this.total = price * quantity;
    }

    // Getters and Setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPrice() { return price; }
    public void setPrice(double price) {
        this.price = price;
        this.total = this.price * this.quantity; // auto-update total
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.total = this.price * this.quantity; // auto-update total
    }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public String getManufactureDate() { return manufactureDate; }
    public void setManufactureDate(String manufactureDate) { this.manufactureDate = manufactureDate; }

    public String getExpiryDate() { return expiryDate; }
    public void setExpiryDate(String expiryDate) { this.expiryDate = expiryDate; }

    public long getCategoryId() { return categoryId; }
    public void setCategoryId(long categoryId) { this.categoryId = categoryId; }

    public String getDeletedAt() { return deletedAt; }
    public void setDeletedAt(String deletedAt) { this.deletedAt = deletedAt; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
