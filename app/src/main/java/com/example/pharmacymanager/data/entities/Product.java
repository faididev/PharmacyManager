package com.example.pharmacymanager.data.entities;

import org.json.JSONException;
import org.json.JSONObject;

public class Product {
    private String uuid;
    private String name;
    private String sku;
    private String description;
    private String image;
    private int quantity;
    private int total;
    private String manufactureDate;
    private String expiryDate;
    private int categoryId;
    private double price;
    private String createdAt;
    private String updatedAt;

    public Product() {}

    public Product(String uuid, String name, String sku, String description, String image, 
                   int quantity, int total, String manufactureDate, String expiryDate, 
                   int categoryId, double price, String createdAt, String updatedAt) {
        this.uuid = uuid;
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.image = image;
        this.quantity = quantity;
        this.total = total;
        this.manufactureDate = manufactureDate;
        this.expiryDate = expiryDate;
        this.categoryId = categoryId;
        this.price = price;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Parse from API response (handles both list and single product responses)
    public static Product fromJson(JSONObject json) throws JSONException {
        android.util.Log.d("Product", "Parsing JSON: " + json.toString());
        
        try {
            JSONObject productData;
            JSONObject attributes;
            
            // Check if this is a single product response (has "data" wrapper)
            if (json.has("data")) {
                productData = json.getJSONObject("data");
                android.util.Log.d("Product", "Single product response detected");
            } else {
                // This is a direct product object (from list response)
                productData = json;
                android.util.Log.d("Product", "Direct product object detected");
            }
            
            attributes = productData.getJSONObject("attributes");
            
            String uuid = productData.getString("uuid");
            String name = attributes.getString("name");
            String sku = attributes.optString("sku", "");
            String description = attributes.optString("description", "");
            String image = attributes.optString("image", null);
            int quantity = attributes.optInt("quantity", 0);
            int total = attributes.optInt("total", 0);
            String manufactureDate = attributes.optString("manufacture_date", "");
            String expiryDate = attributes.optString("expiry_date", "");
            int categoryId = attributes.optInt("category_id", 0);
            double price = attributes.optDouble("price", 0.0);
            String createdAt = attributes.optString("createdAt", "");
            String updatedAt = attributes.optString("updatedAt", "");
            
            android.util.Log.d("Product", "Parsed - UUID: " + uuid + ", Name: " + name + ", Price: " + price);
            
            return new Product(
                uuid,
                name,
                sku,
                description,
                image,
                quantity,
                total,
                manufactureDate,
                expiryDate,
                categoryId,
                price,
                createdAt,
                updatedAt
            );
        } catch (JSONException e) {
            android.util.Log.e("Product", "Failed to parse product JSON: " + json.toString(), e);
            throw e;
        }
    }

    // Convert to JSON for API request
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("sku", sku);
        json.put("description", description);
        json.put("quantity", quantity);
        json.put("total", total);
        json.put("manufacture_date", manufactureDate);
        json.put("expiry_date", expiryDate);
        json.put("category_id", categoryId);
        json.put("price", price);
        return json;
    }

    // Getters and Setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getManufactureDate() {
        return manufactureDate;
    }

    public void setManufactureDate(String manufactureDate) {
        this.manufactureDate = manufactureDate;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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
    public static Product fromJsonSafe(JSONObject json) {
        try {
            return fromJson(json);
        } catch (JSONException e) {
            android.util.Log.e("Product", "Safe parsing failed, trying alternative approach", e);
            
            try {
                JSONObject productData;
                
                // Check if this is a single product response (has "data" wrapper)
                if (json.has("data")) {
                    productData = json.optJSONObject("data");
                } else {
                    productData = json;
                }
                
                if (productData == null) {
                    throw new Exception("No valid product data found");
                }
                
                // Try to extract basic info even if structure is different
                String uuid = productData.optString("uuid", "");
                String name = productData.optString("name", "Unknown Product");
                
                // Try to get attributes if they exist
                if (productData.has("attributes")) {
                    JSONObject attributes = productData.optJSONObject("attributes");
                    if (attributes != null) {
                        name = attributes.optString("name", name);
                    }
                }
                
                return new Product(uuid, name, "", "", null, 0, 0, "", "", 0, 0.0, "", "");
            } catch (Exception ex) {
                android.util.Log.e("Product", "All parsing methods failed", ex);
                return new Product("", "Parse Error", "", "Failed to parse product", null, 0, 0, "", "", 0, 0.0, "", "");
            }
        }
    }
}