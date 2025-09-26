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

    // Parse from API response (handles both list and single category responses)
    public static Category fromJson(JSONObject json) throws JSONException {
        android.util.Log.d("Category", "Parsing JSON: " + json.toString());
        
        try {
            JSONObject categoryData;
            JSONObject attributes;
            
            // Check if this is a single category response (has "data" wrapper)
            if (json.has("data")) {
                categoryData = json.getJSONObject("data");
                android.util.Log.d("Category", "Single category response detected");
            } else {
                // This is a direct category object (from list response)
                categoryData = json;
                android.util.Log.d("Category", "Direct category object detected");
            }
            
            attributes = categoryData.getJSONObject("attributes");
            
            int id = categoryData.getInt("id");
            String name = attributes.getString("name");
            String description = attributes.optString("description", ""); // Handle null descriptions
            String createdAt = attributes.optString("createdAt", "");
            String updatedAt = attributes.optString("updatedAt", "");
            
            android.util.Log.d("Category", "Parsed - ID: " + id + ", Name: " + name + ", Description: " + description);
            
            return new Category(
                id,
                name,
                description,
                createdAt,
                updatedAt
            );
        } catch (JSONException e) {
            android.util.Log.e("Category", "Failed to parse category JSON: " + json.toString(), e);
            throw e;
        }
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
    
    // Alternative parsing method with more defensive approach
    public static Category fromJsonSafe(JSONObject json) {
        try {
            return fromJson(json);
        } catch (JSONException e) {
            android.util.Log.e("Category", "Safe parsing failed, trying alternative approach", e);
            
            try {
                JSONObject categoryData;
                
                // Check if this is a single category response (has "data" wrapper)
                if (json.has("data")) {
                    categoryData = json.optJSONObject("data");
                } else {
                    categoryData = json;
                }
                
                if (categoryData == null) {
                    throw new Exception("No valid category data found");
                }
                
                // Try to extract basic info even if structure is different
                int id = categoryData.optInt("id", 0);
                String name = categoryData.optString("name", "Unknown Category");
                
                // Try to get attributes if they exist
                if (categoryData.has("attributes")) {
                    JSONObject attributes = categoryData.optJSONObject("attributes");
                    if (attributes != null) {
                        name = attributes.optString("name", name);
                    }
                }
                
                return new Category(id, name, "", "", "");
            } catch (Exception ex) {
                android.util.Log.e("Category", "All parsing methods failed", ex);
                return new Category(0, "Parse Error", "Failed to parse category", "", "");
            }
        }
    }
    
    // Test method to verify parsing with sample data
    public static void testParsing() {
        try {
            // Test list response format (direct category object)
            String listResponseJson = "{\n" +
                "  \"type\": \"category\",\n" +
                "  \"id\": 1,\n" +
                "  \"attributes\": {\n" +
                "    \"name\": \"Antibiotics\",\n" +
                "    \"description\": null,\n" +
                "    \"createdAt\": \"2025-08-31T10:10:07.000000Z\",\n" +
                "    \"updatedAt\": \"2025-08-31T10:10:07.000000Z\"\n" +
                "  }\n" +
                "}";
            
            JSONObject listJson = new JSONObject(listResponseJson);
            Category listCategory = fromJson(listJson);
            android.util.Log.d("Category", "List response parsing successful: " + listCategory.getName());
            
            // Test single category response format (with data wrapper)
            String singleResponseJson = "{\n" +
                "  \"data\": {\n" +
                "    \"type\": \"category\",\n" +
                "    \"id\": 1,\n" +
                "    \"attributes\": {\n" +
                "      \"name\": \"Pain Relief\",\n" +
                "      \"description\": \"Updated medications for pain\",\n" +
                "      \"createdAt\": \"2025-08-31T10:10:07.000000Z\",\n" +
                "      \"updatedAt\": \"2025-09-26T09:53:55.000000Z\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
            
            JSONObject singleJson = new JSONObject(singleResponseJson);
            Category singleCategory = fromJson(singleJson);
            android.util.Log.d("Category", "Single response parsing successful: " + singleCategory.getName());
            
        } catch (Exception e) {
            android.util.Log.e("Category", "Test parsing failed: " + e.getMessage(), e);
        }
    }
}