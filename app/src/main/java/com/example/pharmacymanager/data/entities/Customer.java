package com.example.pharmacymanager.data.entities;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;

public class Customer implements Serializable {
    private int id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private int userId;
    private int loyaltyPoints;
    private String createdAt;
    private String updatedAt;

    public Customer() {}

    public Customer(int id, String name, String email, String phone, String address, 
                   int userId, int loyaltyPoints, String createdAt, String updatedAt) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.userId = userId;
        this.loyaltyPoints = loyaltyPoints;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Parse from API response (handles both list and single customer responses)
    public static Customer fromJson(JSONObject json) throws JSONException {
        android.util.Log.d("Customer", "Parsing JSON: " + json.toString());
        
        try {
            JSONObject customerData;
            
            // Check if this is a single customer response (has "data" wrapper)
            if (json.has("data")) {
                customerData = json.getJSONObject("data");
                android.util.Log.d("Customer", "Single customer response detected");
            } else {
                // This is a direct customer object (from list response)
                customerData = json;
                android.util.Log.d("Customer", "Direct customer object detected");
            }
            
            // Try different response formats
            int id;
            String name;
            String email;
            String phone;
            String address;
            int userId;
            int loyaltyPoints;
            String createdAt;
            String updatedAt;
            
            // Format 1: With attributes wrapper (like Category)
            if (customerData.has("attributes")) {
                android.util.Log.d("Customer", "Using attributes format");
                JSONObject attributes = customerData.getJSONObject("attributes");
                
                id = customerData.getInt("id");
                name = attributes.getString("name");
                email = attributes.getString("email");
                phone = attributes.getString("phone");
                address = attributes.optString("address", "");
                userId = attributes.getInt("user_id");
                loyaltyPoints = attributes.optInt("loyalty_points", 0);
                createdAt = attributes.optString("createdAt", "");
                updatedAt = attributes.optString("updatedAt", "");
            }
            // Format 2: Direct fields (no attributes wrapper)
            else {
                android.util.Log.d("Customer", "Using direct fields format");
                
                id = customerData.getInt("id");
                name = customerData.getString("name");
                email = customerData.getString("email");
                phone = customerData.getString("phone");
                address = customerData.optString("address", "");
                userId = customerData.getInt("user_id");
                loyaltyPoints = customerData.optInt("loyalty_points", 0);
                createdAt = customerData.optString("createdAt", "");
                updatedAt = customerData.optString("updatedAt", "");
            }
            
            android.util.Log.d("Customer", "Parsed - ID: " + id + ", Name: " + name + ", Email: " + email);
            
            return new Customer(
                id,
                name,
                email,
                phone,
                address,
                userId,
                loyaltyPoints,
                createdAt,
                updatedAt
            );
        } catch (JSONException e) {
            android.util.Log.e("Customer", "Failed to parse customer JSON: " + json.toString(), e);
            throw e;
        }
    }

    // Convert to JSON for API request
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("name", name);
        json.put("email", email);
        json.put("phone", phone);
        json.put("address", address != null ? address : "");
        json.put("user_id", userId);
        json.put("loyalty_points", loyaltyPoints);
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
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
    public static Customer safeFromJson(JSONObject json) {
        try {
            return fromJson(json);
        } catch (JSONException e) {
            android.util.Log.e("Customer", "Safe parsing failed, trying alternative approach", e);
            
            try {
                JSONObject customerData;
                
                // Check if this is a single customer response (has "data" wrapper)
                if (json.has("data")) {
                    customerData = json.optJSONObject("data");
                } else {
                    customerData = json;
                }
                
                if (customerData == null) {
                    throw new Exception("No valid customer data found");
                }
                
                // Try to extract basic info even if structure is different
                int id = customerData.optInt("id", 0);
                String name = customerData.optString("name", "Unknown Customer");
                String email = customerData.optString("email", "");
                String phone = customerData.optString("phone", "");
                String address = customerData.optString("address", "");
                int userId = customerData.optInt("user_id", 1);
                int loyaltyPoints = customerData.optInt("loyalty_points", 0);
                
                // Try to get attributes if they exist
                if (customerData.has("attributes")) {
                    JSONObject attributes = customerData.optJSONObject("attributes");
                    if (attributes != null) {
                        name = attributes.optString("name", name);
                        email = attributes.optString("email", email);
                        phone = attributes.optString("phone", phone);
                        address = attributes.optString("address", address);
                        userId = attributes.optInt("user_id", userId);
                        loyaltyPoints = attributes.optInt("loyalty_points", loyaltyPoints);
                    }
                }
                
                return new Customer(id, name, email, phone, address, userId, loyaltyPoints, "", "");
            } catch (Exception ex) {
                android.util.Log.e("Customer", "All parsing methods failed", ex);
                return new Customer(0, "Parse Error", "error@example.com", "", "", 1, 0, "", "");
            }
        }
    }
    
    // Test method to verify parsing with sample data
    public static void testParsing() {
        try {
            // Test list response format (direct customer object)
            String listResponseJson = "{\n" +
                "  \"type\": \"customer\",\n" +
                "  \"id\": 1,\n" +
                "  \"attributes\": {\n" +
                "    \"name\": \"John Doe\",\n" +
                "    \"email\": \"john@example.com\",\n" +
                "    \"phone\": \"+1234567890\",\n" +
                "    \"address\": \"123 Main St\",\n" +
                "    \"user_id\": 1,\n" +
                "    \"loyalty_points\": 100,\n" +
                "    \"createdAt\": \"2025-01-01T10:00:00.000000Z\",\n" +
                "    \"updatedAt\": \"2025-01-01T10:00:00.000000Z\"\n" +
                "  }\n" +
                "}";
            
            JSONObject listJson = new JSONObject(listResponseJson);
            Customer listCustomer = fromJson(listJson);
            android.util.Log.d("Customer", "List response parsing successful: " + listCustomer.getName());
            
            // Test single customer response format (with data wrapper)
            String singleResponseJson = "{\n" +
                "  \"data\": {\n" +
                "    \"type\": \"customer\",\n" +
                "    \"id\": 1,\n" +
                "    \"attributes\": {\n" +
                "      \"name\": \"Jane Smith\",\n" +
                "      \"email\": \"jane@example.com\",\n" +
                "      \"phone\": \"+0987654321\",\n" +
                "      \"address\": \"456 Oak Ave\",\n" +
                "      \"user_id\": 1,\n" +
                "      \"loyalty_points\": 50,\n" +
                "      \"createdAt\": \"2025-01-01T10:00:00.000000Z\",\n" +
                "      \"updatedAt\": \"2025-01-01T10:00:00.000000Z\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
            
            JSONObject singleJson = new JSONObject(singleResponseJson);
            Customer singleCustomer = fromJson(singleJson);
            android.util.Log.d("Customer", "Single response parsing successful: " + singleCustomer.getName());
            
            // Test direct fields format (no attributes wrapper)
            String directResponseJson = "{\n" +
                "  \"id\": 2,\n" +
                "  \"name\": \"Bob Johnson\",\n" +
                "  \"email\": \"bob@example.com\",\n" +
                "  \"phone\": \"+1122334455\",\n" +
                "  \"address\": \"789 Pine St\",\n" +
                "  \"user_id\": 1,\n" +
                "  \"loyalty_points\": 25,\n" +
                "  \"createdAt\": \"2025-01-01T10:00:00.000000Z\",\n" +
                "  \"updatedAt\": \"2025-01-01T10:00:00.000000Z\"\n" +
                "}";
            
            JSONObject directJson = new JSONObject(directResponseJson);
            Customer directCustomer = fromJson(directJson);
            android.util.Log.d("Customer", "Direct fields parsing successful: " + directCustomer.getName());
            
        } catch (Exception e) {
            android.util.Log.e("Customer", "Test parsing failed: " + e.getMessage(), e);
        }
    }
}