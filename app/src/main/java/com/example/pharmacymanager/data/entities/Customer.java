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

            if (json.has("data")) {
                customerData = json.getJSONObject("data");
                android.util.Log.d("Customer", "Single customer response detected");
            } else {

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
                
                // Check if user data is in includes (new format)
                if (customerData.has("includes") && customerData.getJSONObject("includes").has("user")) {
                    android.util.Log.d("Customer", "Using includes.user format");
                    JSONObject userData = customerData.getJSONObject("includes").getJSONObject("user");
                    JSONObject userAttributes = userData.getJSONObject("attributes");
                    
                    name = userAttributes.getString("name");
                    email = userAttributes.getString("email");
                    phone = userAttributes.optString("phone", "");
                    address = userAttributes.optString("address", "");
                    
                    // Get user_id from relationships
                    if (customerData.has("relationships") && 
                        customerData.getJSONObject("relationships").has("user") &&
                        customerData.getJSONObject("relationships").getJSONObject("user").has("data")) {
                        userId = customerData.getJSONObject("relationships")
                                           .getJSONObject("user")
                                           .getJSONObject("data")
                                           .getInt("id");
                    } else {
                        userId = userData.optInt("id", 1);
                    }
                } else {
                    // Fallback to old format
                    name = attributes.optString("name", "Unknown Customer");
                    email = attributes.optString("email", "");
                    phone = attributes.optString("phone", "");
                    address = attributes.optString("address", "");
                    userId = attributes.optInt("user_id", 1);
                }
                
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
                        loyaltyPoints = attributes.optInt("loyalty_points", loyaltyPoints);
                    }
                }
                
                // Try to get user data from includes
                if (customerData.has("includes") && customerData.optJSONObject("includes").has("user")) {
                    JSONObject userData = customerData.optJSONObject("includes").optJSONObject("user");
                    if (userData != null && userData.has("attributes")) {
                        JSONObject userAttributes = userData.optJSONObject("attributes");
                        if (userAttributes != null) {
                            name = userAttributes.optString("name", name);
                            email = userAttributes.optString("email", email);
                            phone = userAttributes.optString("phone", phone);
                            address = userAttributes.optString("address", address);
                        }
                    }
                }
                
                // Try to get user_id from relationships
                if (customerData.has("relationships") && 
                    customerData.optJSONObject("relationships").has("user") &&
                    customerData.optJSONObject("relationships").optJSONObject("user").has("data")) {
                    JSONObject userData = customerData.optJSONObject("relationships")
                                                   .optJSONObject("user")
                                                   .optJSONObject("data");
                    if (userData != null) {
                        userId = userData.optInt("id", userId);
                    }
                }
                
                // If we still don't have a proper name, try to use email as name
                if (name.equals("Unknown Customer") && !email.isEmpty()) {
                    name = email.split("@")[0]; // Use part before @ as name
                }
                
                android.util.Log.d("Customer", "Safe parsing result - ID: " + id + ", Name: " + name + ", Email: " + email);
                
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
            // Test with the actual API response format
            String testJson = "{\n" +
                "  \"type\": \"customer\",\n" +
                "  \"id\": 2,\n" +
                "  \"attributes\": {\n" +
                "    \"loyalty_points\": \"100\",\n" +
                "    \"createdAt\": \"2025-08-31T20:49:06.000000Z\",\n" +
                "    \"updatedAt\": \"2025-08-31T20:49:06.000000Z\"\n" +
                "  },\n" +
                "  \"relationships\": {\n" +
                "    \"user\": {\n" +
                "      \"data\": {\n" +
                "        \"type\": \"user\",\n" +
                "        \"id\": 13\n" +
                "      }\n" +
                "    }\n" +
                "  },\n" +
                "  \"includes\": {\n" +
                "    \"user\": {\n" +
                "      \"type\": \"user\",\n" +
                "      \"id\": \"addf1f2c-6789-41db-8b6e-649085ee5807\",\n" +
                "      \"attributes\": {\n" +
                "        \"name\": \"yassine\",\n" +
                "        \"email\": \"yacin.wo@gmail.com\"\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";
            
            JSONObject testCustomerJson = new JSONObject(testJson);
            Customer testCustomer = Customer.fromJson(testCustomerJson);
            
            android.util.Log.d("Customer", "Test parsing successful:");
            android.util.Log.d("Customer", "ID: " + testCustomer.getId());
            android.util.Log.d("Customer", "Name: " + testCustomer.getName());
            android.util.Log.d("Customer", "Email: " + testCustomer.getEmail());
            android.util.Log.d("Customer", "User ID: " + testCustomer.getUserId());
            android.util.Log.d("Customer", "Loyalty Points: " + testCustomer.getLoyaltyPoints());
            
        } catch (Exception e) {
            android.util.Log.e("Customer", "Test parsing failed: " + e.getMessage(), e);
        }
    }
}