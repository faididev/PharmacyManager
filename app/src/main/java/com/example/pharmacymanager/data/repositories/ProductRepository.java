package com.example.pharmacymanager.data.repositories;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.pharmacymanager.data.entities.Product;
import com.example.pharmacymanager.data.remote.ApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ProductRepository {
    private final Context appContext;

    public interface ProductCallback {
        void onSuccess(Product product);
        void onError(String message);
    }

    public interface ProductListCallback {
        void onSuccess(JSONObject response);
        void onError(String message);
    }

    public ProductRepository(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public void createProduct(String name, String sku, String description, int quantity, 
                             int total, String manufactureDate, String expiryDate, 
                             int categoryId, double price, ProductCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("name", name);
            body.put("sku", sku);
            body.put("description", description);
            body.put("quantity", quantity);
            body.put("total", total);
            body.put("manufacture_date", manufactureDate);
            body.put("expiry_date", expiryDate);
            body.put("category_id", categoryId);
            body.put("price", price);

            Log.d("ProductRepository", "Create product request body=" + body.toString());

            ApiClient.enqueue(appContext, ApiClient.jsonRequest(
                    appContext,
                    Request.Method.POST,
                    "products",
                    body,
                    response -> {
                        Log.d("ProductRepository", "Create product response=" + response.toString());
                        try {
                            Product product = Product.fromJson(response);
                            callback.onSuccess(product);
                        } catch (JSONException e) {
                            callback.onError("Failed to parse product response: " + e.getMessage());
                        }
                    },
                    error -> {
                        handleError(error, callback);
                    }
            ));
        } catch (JSONException e) {
            callback.onError(e.getMessage());
        }
    }


    public void getProducts(ProductListCallback callback) {
        JsonObjectRequest req = ApiClient.jsonRequest(
                appContext,
                Request.Method.GET,
                "products",
                null,
                response -> {
                    Log.d("ProductRepository", "Get products response=" + response.toString());
                    callback.onSuccess(response);
                },
                error -> {
                    String message = parseError(error);
                    Log.e("ProductRepository", "Get products failed: " + message, error);
                    callback.onError(message);
                }
        );
        ApiClient.enqueue(appContext, req);
    }

    public void getProductById(String uuid, ProductCallback callback) {
        JsonObjectRequest req = ApiClient.jsonRequest(
                appContext,
                Request.Method.GET,
                "products/" + uuid,
                null,
                response -> {
                    Log.d("ProductRepository", "Get product by ID response=" + response.toString());
                    try {
                        Product product = Product.fromJson(response);
                        callback.onSuccess(product);
                    } catch (JSONException e) {
                        callback.onError("Failed to parse product response: " + e.getMessage());
                    }
                },
                error -> {
                    String message = parseError(error);
                    Log.e("ProductRepository", "Get product by ID failed: " + message, error);
                    callback.onError(message);
                }
        );
        ApiClient.enqueue(appContext, req);
    }

    public void updateProduct(String uuid, String name, String sku, String description, 
                             int quantity, int total, String manufactureDate, String expiryDate, 
                             int categoryId, double price, ProductCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("name", name);
            body.put("sku", sku);
            body.put("description", description);
            body.put("quantity", quantity);
            body.put("total", total);
            body.put("manufacture_date", manufactureDate);
            body.put("expiry_date", expiryDate);
            body.put("category_id", categoryId);
            body.put("price", price);

            Log.d("ProductRepository", "Update product request body=" + body.toString());

            ApiClient.enqueue(appContext, ApiClient.jsonRequest(
                    appContext,
                    Request.Method.PUT,
                    "products/" + uuid,
                    body,
                    response -> {
                        Log.d("ProductRepository", "Update product response=" + response.toString());
                        try {
                            Product product = Product.fromJson(response);
                            callback.onSuccess(product);
                        } catch (JSONException e) {
                            callback.onError("Failed to parse product response: " + e.getMessage());
                        }
                    },
                    error -> {
                        handleError(error, callback);
                    }
            ));
        } catch (JSONException e) {
            callback.onError(e.getMessage());
        }
    }


    public void deleteProduct(String uuid, ProductCallback callback) {
        Log.d("ProductRepository", "Deleting product with UUID: " + uuid);
        ApiClient.enqueue(appContext, ApiClient.stringRequest(
                appContext,
                Request.Method.DELETE,
                "products/" + uuid,
                null,
                response -> {
                    Log.d("ProductRepository", "Delete product successful, response=" + response);
                    // For delete, we don't need to parse the response (204 No Content)
                    callback.onSuccess(null);
                },
                error -> {
                    Log.e("ProductRepository", "Delete product failed", error);
                    handleError(error, callback);
                }
        ));
    }


    private void handleError(VolleyError error, ProductCallback callback) {
        String message = error.getMessage();

        Log.e("ProductRepository", "=== VOLLEY ERROR DETAILS ===");
        Log.e("ProductRepository", "Error message: " + message);
        Log.e("ProductRepository", "Error class: " + error.getClass().getSimpleName());
        Log.e("ProductRepository", "Error toString: " + error.toString());
        
        if (error.networkResponse != null) {
            Log.e("ProductRepository", "Network response status code: " + error.networkResponse.statusCode);
            Log.e("ProductRepository", "Network response headers: " + error.networkResponse.headers.toString());
            
            String body = null;
            try {
                body = new String(error.networkResponse.data);
                Log.e("ProductRepository", "Network response body: " + body);
            } catch (Exception e) {
                Log.e("ProductRepository", "Error reading response body: " + e.getMessage());
            }
            message = "HTTP " + error.networkResponse.statusCode + (body != null ? (": " + body) : "");
        } else {
            Log.e("ProductRepository", "No network response - connection error");
            Log.e("ProductRepository", "This usually indicates: Network connectivity issues, Server down, or DNS resolution problems");
            message = "Connection error: " + (message != null ? message : "Unable to connect to server");
        }
        
        if (message == null) message = "Unknown error";
        Log.e("ProductRepository", "Final error message: " + message);
        Log.e("ProductRepository", "=== END ERROR DETAILS ===");
        
        callback.onError(message);
    }

    private String parseError(VolleyError error) {
        String message = error.getMessage();
        if (message == null && error.networkResponse != null) {
            message = "HTTP " + error.networkResponse.statusCode;
        }
        if (message == null) message = "Unknown error";
        return message;
    }
}