package com.example.pharmacymanager.data.entities;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.Serializable;

public class OrderItem implements Serializable {
    private int productId;
    private int quantity;
    private double price;
    private String productName; // For display purposes

    public OrderItem() {}

    public OrderItem(int productId, int quantity, double price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public OrderItem(int productId, int quantity, double price, String productName) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.productName = productName;
    }

    // Parse from API response
    public static OrderItem fromJson(JSONObject json) throws JSONException {
        android.util.Log.d("OrderItem", "Parsing JSON: " + json.toString());
        
        try {
            int productId = json.getInt("product_id");
            int quantity = json.getInt("quantity");
            double price = json.getDouble("price");
            String productName = json.optString("product_name", "");
            
            android.util.Log.d("OrderItem", "Parsed - Product ID: " + productId + ", Quantity: " + quantity + ", Price: " + price);
            
            return new OrderItem(productId, quantity, price, productName);
        } catch (JSONException e) {
            android.util.Log.e("OrderItem", "Failed to parse order item JSON: " + json.toString(), e);
            throw e;
        }
    }

    // Convert to JSON for API request
    public JSONObject toJson() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("product_id", productId);
        json.put("quantity", quantity);
        json.put("price", price);
        return json;
    }

    // Getters and Setters
    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getTotalPrice() {
        return quantity * price;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "productId=" + productId +
                ", quantity=" + quantity +
                ", price=" + price +
                ", productName='" + productName + '\'' +
                '}';
    }
}