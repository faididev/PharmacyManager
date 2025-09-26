package com.example.pharmacymanager.data.entities;

import org.json.JSONException;
import org.json.JSONObject;

public class Category {
    private int id;
    private String name;
    private String description;
    private String createdAt;
    private String updatedAt;

    public Category() {}

    public Category(int id, String name, String description, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Parse from API response
    public static Category fromJson(JSONObject json) throws JSONException {
        JSONObject data = json.getJSONObject("data");
        JSONObject attributes = data.getJSONObject("attributes");
        
        return new Category(
            data.getInt("id"),
            attributes.getString("name"),
            attributes.getString("description"),
            attributes.getString("createdAt"),
            attributes.getString("updatedAt")
        );
    }

    // Convert to JSON for API request
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("description", description);
        return json;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return name;
    }
}