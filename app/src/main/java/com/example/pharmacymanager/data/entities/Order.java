package com.example.pharmacymanager.data.entities;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Order implements Serializable {
    private int id;
    private int customerId;
    private String orderDate;
    private String status;
    private List<OrderItem> items;
    private double totalAmount;
    private String createdAt;
    private String updatedAt;
    private String customerName; // For display purposes

    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(int id, int customerId, String orderDate, String status, List<OrderItem> items, 
                 double totalAmount, String createdAt, String updatedAt) {
        this.id = id;
        this.customerId = customerId;
        this.orderDate = orderDate;
        this.status = status;
        this.items = items != null ? items : new ArrayList<>();
        this.totalAmount = totalAmount;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Parse from API response (handles both list and single order responses)
    public static Order fromJson(JSONObject json) throws JSONException {
        android.util.Log.d("Order", "=== PARSING ORDER JSON ===");
        android.util.Log.d("Order", "Full JSON: " + json.toString());
        
        try {
            JSONObject orderData;

            if (json.has("data")) {
                orderData = json.getJSONObject("data");
                android.util.Log.d("Order", "Single order response detected");
            } else {
                orderData = json;
                android.util.Log.d("Order", "Direct order object detected");
            }
            
            android.util.Log.d("Order", "Order data keys: " + orderData.keys().next());
            android.util.Log.d("Order", "Order data: " + orderData.toString());
            
            // Try different response formats
            int id;
            int customerId;
            String orderDate;
            String status;
            List<OrderItem> items;
            double totalAmount;
            String createdAt;
            String updatedAt;
            String customerName;
            
            // Format 1: With attributes wrapper (like Category)
            if (orderData.has("attributes")) {
                android.util.Log.d("Order", "=== USING ATTRIBUTES FORMAT ===");
                android.util.Log.d("Order", "Attributes: " + orderData.getJSONObject("attributes").toString());
                JSONObject attributes = orderData.getJSONObject("attributes");
                
                id = orderData.getInt("id");
                customerId = attributes.getInt("customer_id");
                orderDate = attributes.getString("order_date");
                status = attributes.getString("status");
                totalAmount = attributes.optDouble("total_amount", 0.0);
                createdAt = attributes.optString("createdAt", "");
                updatedAt = attributes.optString("updatedAt", "");
                customerName = attributes.optString("customer_name", "");
                android.util.Log.d("Order", "Customer name from attributes: '" + customerName + "'");
                
                // Parse items
                items = new ArrayList<>();
                if (attributes.has("items")) {
                    android.util.Log.d("Order", "Found items in attributes, parsing...");
                    JSONArray itemsArray = attributes.getJSONArray("items");
                    android.util.Log.d("Order", "Items array length: " + itemsArray.length());
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject itemJson = itemsArray.getJSONObject(i);
                        android.util.Log.d("Order", "Parsing item " + i + ": " + itemJson.toString());
                        OrderItem item = OrderItem.fromJson(itemJson);
                        items.add(item);
                        android.util.Log.d("Order", "Item " + i + " added successfully");
                    }
                } else {
                    android.util.Log.w("Order", "No items found in attributes");
                }
            }
            // Format 2: Direct fields (no attributes wrapper)
            else {
                android.util.Log.d("Order", "=== USING DIRECT FIELDS FORMAT ===");
                android.util.Log.d("Order", "Direct fields: " + orderData.toString());
                
                id = orderData.getInt("id");
                customerId = orderData.getInt("customer_id");
                orderDate = orderData.getString("order_date");
                status = orderData.getString("status");
                totalAmount = orderData.optDouble("total_amount", 0.0);
                createdAt = orderData.optString("createdAt", "");
                updatedAt = orderData.optString("updatedAt", "");
                customerName = orderData.optString("customer_name", "");
                android.util.Log.d("Order", "Customer name from direct fields: '" + customerName + "'");
                
                // Parse items
                items = new ArrayList<>();
                if (orderData.has("items")) {
                    android.util.Log.d("Order", "Found items in direct fields, parsing...");
                    JSONArray itemsArray = orderData.getJSONArray("items");
                    android.util.Log.d("Order", "Items array length: " + itemsArray.length());
                    for (int i = 0; i < itemsArray.length(); i++) {
                        JSONObject itemJson = itemsArray.getJSONObject(i);
                        android.util.Log.d("Order", "Parsing item " + i + ": " + itemJson.toString());
                        OrderItem item = OrderItem.fromJson(itemJson);
                        items.add(item);
                        android.util.Log.d("Order", "Item " + i + " added successfully");
                    }
                } else {
                    android.util.Log.w("Order", "No items found in direct fields");
                }
            }
            
            android.util.Log.d("Order", "=== PARSING COMPLETE ===");
            android.util.Log.d("Order", "ID: " + id);
            android.util.Log.d("Order", "Customer ID: " + customerId);
            android.util.Log.d("Order", "Customer Name: '" + customerName + "'");
            android.util.Log.d("Order", "Status: " + status);
            android.util.Log.d("Order", "Items count: " + items.size());
            android.util.Log.d("Order", "Total amount: " + totalAmount);
            
            Order order = new Order(id, customerId, orderDate, status, items, totalAmount, createdAt, updatedAt);
            order.setCustomerName(customerName);
            android.util.Log.d("Order", "Order created successfully with customer name: '" + order.getCustomerName() + "'");
            return order;
        } catch (JSONException e) {
            android.util.Log.e("Order", "Failed to parse order JSON: " + json.toString(), e);
            throw e;
        }
    }

    // Convert to JSON for API request
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("customer_id", customerId);
        json.put("order_date", orderDate);
        json.put("status", status);
        
        JSONArray itemsArray = new JSONArray();
        for (OrderItem item : items) {
            itemsArray.put(item.toJson());
        }
        json.put("items", itemsArray);
        
        return json;
    }

    // Calculate total amount from items
    public void calculateTotalAmount() {
        totalAmount = 0.0;
        for (OrderItem item : items) {
            totalAmount += item.getTotalPrice();
        }
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        calculateTotalAmount();
    }

    public void addItem(OrderItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        calculateTotalAmount();
    }

    public void removeItem(OrderItem item) {
        if (this.items != null) {
            this.items.remove(item);
            calculateTotalAmount();
        }
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    @Override
    public String toString() {
        return "Order #" + id + " - " + status;
    }

    // Alternative parsing method with more defensive approach
    public static Order safeFromJson(JSONObject json) {
        try {
            return fromJson(json);
        } catch (JSONException e) {
            android.util.Log.e("Order", "Safe parsing failed, trying alternative approach", e);
            
            try {
                JSONObject orderData;
                
                // Check if this is a single order response (has "data" wrapper)
                if (json.has("data")) {
                    orderData = json.optJSONObject("data");
                } else {
                    orderData = json;
                }
                
                if (orderData == null) {
                    throw new Exception("No valid order data found");
                }
                
                // Try to extract basic info even if structure is different
                int id = orderData.optInt("id", 0);
                int customerId = orderData.optInt("customer_id", 1);
                String orderDate = orderData.optString("order_date", "");
                String status = orderData.optString("status", "pending");
                String customerName = orderData.optString("customer_name", "");
                
                // Try to get attributes if they exist
                if (orderData.has("attributes")) {
                    JSONObject attributes = orderData.optJSONObject("attributes");
                    if (attributes != null) {
                        customerId = attributes.optInt("customer_id", customerId);
                        orderDate = attributes.optString("order_date", orderDate);
                        status = attributes.optString("status", status);
                        customerName = attributes.optString("customer_name", customerName);
                    }
                }
                
                return new Order(id, customerId, orderDate, status, new ArrayList<>(), 0.0, "", "");
            } catch (Exception ex) {
                android.util.Log.e("Order", "All parsing methods failed", ex);
                return new Order(0, 1, "", "error", new ArrayList<>(), 0.0, "", "");
            }
        }
    }
    
    // Test method to verify parsing with sample data
    public static void testParsing() {
        try {
            // Test list response format (direct order object)
            String listResponseJson = "{\n" +
                "  \"type\": \"order\",\n" +
                "  \"id\": 1,\n" +
                "  \"attributes\": {\n" +
                "    \"customer_id\": 2,\n" +
                "    \"order_date\": \"2024-01-15\",\n" +
                "    \"status\": \"pending\",\n" +
                "    \"total_amount\": 18.98,\n" +
                "    \"customer_name\": \"John Doe\",\n" +
                "    \"items\": [\n" +
                "      {\n" +
                "        \"product_id\": 1,\n" +
                "        \"quantity\": 2,\n" +
                "        \"price\": 5.99,\n" +
                "        \"product_name\": \"Product 1\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"product_id\": 2,\n" +
                "        \"quantity\": 1,\n" +
                "        \"price\": 12.99,\n" +
                "        \"product_name\": \"Product 2\"\n" +
                "      }\n" +
                "    ],\n" +
                "    \"createdAt\": \"2025-01-01T10:00:00.000000Z\",\n" +
                "    \"updatedAt\": \"2025-01-01T10:00:00.000000Z\"\n" +
                "  }\n" +
                "}";
            
            JSONObject listJson = new JSONObject(listResponseJson);
            Order listOrder = fromJson(listJson);
            android.util.Log.d("Order", "List response parsing successful: " + listOrder.toString());
            
            // Test single order response format (with data wrapper)
            String singleResponseJson = "{\n" +
                "  \"data\": {\n" +
                "    \"type\": \"order\",\n" +
                "    \"id\": 1,\n" +
                "    \"attributes\": {\n" +
                "      \"customer_id\": 2,\n" +
                "      \"order_date\": \"2024-01-15\",\n" +
                "      \"status\": \"completed\",\n" +
                "      \"total_amount\": 18.98,\n" +
                "      \"customer_name\": \"Jane Smith\",\n" +
                "      \"items\": [\n" +
                "        {\n" +
                "          \"product_id\": 1,\n" +
                "          \"quantity\": 2,\n" +
                "          \"price\": 5.99,\n" +
                "          \"product_name\": \"Product 1\"\n" +
                "        }\n" +
                "      ],\n" +
                "      \"createdAt\": \"2025-01-01T10:00:00.000000Z\",\n" +
                "      \"updatedAt\": \"2025-01-01T10:00:00.000000Z\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
            
            JSONObject singleJson = new JSONObject(singleResponseJson);
            Order singleOrder = fromJson(singleJson);
            android.util.Log.d("Order", "Single response parsing successful: " + singleOrder.toString());
            
        } catch (Exception e) {
            android.util.Log.e("Order", "Test parsing failed: " + e.getMessage(), e);
        }
    }
}