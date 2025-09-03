package com.example.pharmacymanager.data.entities;

public class User {
    private long id;
    private String uuid;
    private String name;
    private String email;
    private String phone;
    private String address;
    private String emailVerifiedAt;
    private String password;
    private String lastLoginAt;
    private String rememberToken;
    private String deletedAt;
    private String createdAt;
    private String updatedAt;

    // Constructors

    public User() {}

    public User(long id, String uuid, String name, String email, String phone, String address,
                String emailVerifiedAt, String password, String lastLoginAt, String rememberToken,
                String deletedAt, String createdAt, String updatedAt) {
        this.id = id;
        this.uuid = uuid;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.emailVerifiedAt = emailVerifiedAt;
        this.password = password;
        this.lastLoginAt = lastLoginAt;
        this.rememberToken = rememberToken;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public User(String name, String email, String password, String phone, String address) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.address = address;
    }

    // Getters and Setters

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getEmailVerifiedAt() { return emailVerifiedAt; }
    public void setEmailVerifiedAt(String emailVerifiedAt) { this.emailVerifiedAt = emailVerifiedAt; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getLastLoginAt() { return lastLoginAt; }
    public void setLastLoginAt(String lastLoginAt) { this.lastLoginAt = lastLoginAt; }

    public String getRememberToken() { return rememberToken; }
    public void setRememberToken(String rememberToken) { this.rememberToken = rememberToken; }

    public String getDeletedAt() { return deletedAt; }
    public void setDeletedAt(String deletedAt) { this.deletedAt = deletedAt; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}