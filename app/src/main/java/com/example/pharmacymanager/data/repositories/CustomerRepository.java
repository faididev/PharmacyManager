package com.example.pharmacymanager.data.repositories;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.pharmacymanager.data.entities.Customer;
import com.example.pharmacymanager.data.remote.ApiClient;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomerRepository {
    private final Context appContext;

    public interface CustomerCallback {
        void onSuccess(Customer customer);
        void onError(String message);
    }

    public interface CustomerListCallback {
        void onSuccess(JSONObject response);
        void onError(String message);
    }

    public CustomerRepository(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public void createCustomer(String name, String email, String phone, String address, 
                              int userId, int loyaltyPoints, CustomerCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("name", name);
            body.put("email", email);
            body.put("phone", phone);
            body.put("address", address != null ? address : "");
            body.put("user_id", userId);
            body.put("loyalty_points", loyaltyPoints);

            Log.d("CustomerRepository", "Create customer request body=" + body.toString());

            ApiClient.enqueue(appContext, ApiClient.jsonRequest(
                    appContext,
                    Request.Method.POST,
                    "customers",
                    body,
                    response -> {
                        Log.d("CustomerRepository", "Create customer response=" + response.toString());
                        try {
                            Customer customer = Customer.fromJson(response);
                            callback.onSuccess(customer);
                        } catch (JSONException e) {
                            android.util.Log.w("CustomerRepository", "Standard parsing failed, trying safe parsing: " + e.getMessage());
                            try {
                                Customer customer = Customer.safeFromJson(response);
                                callback.onSuccess(customer);
                            } catch (Exception ex) {
                                callback.onError("Failed to parse customer response: " + ex.getMessage());
                            }
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

    public void getCustomers(CustomerListCallback callback) {
        // Try to include user data in the request
        String url = "customers?include=user";
        Log.d("CustomerRepository", "Fetching customers from: " + url);
        
        JsonObjectRequest req = ApiClient.jsonRequest(
                appContext,
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d("CustomerRepository", "Get customers response=" + response.toString());
                    callback.onSuccess(response);
                },
                error -> {
                    Log.w("CustomerRepository", "Get customers with include failed, trying without include: " + error.getMessage());
                    // Fallback to original endpoint without include
                    getCustomersWithoutInclude(callback);
                }
        );
        ApiClient.enqueue(appContext, req);
    }
    
    private void getCustomersWithoutInclude(CustomerListCallback callback) {
        String url = "customers";
        Log.d("CustomerRepository", "Fetching customers from fallback URL: " + url);
        
        JsonObjectRequest req = ApiClient.jsonRequest(
                appContext,
                Request.Method.GET,
                url,
                null,
                response -> {
                    Log.d("CustomerRepository", "Get customers fallback response=" + response.toString());
                    callback.onSuccess(response);
                },
                error -> {
                    String message = parseError(error);
                    Log.e("CustomerRepository", "Get customers fallback failed: " + message, error);
                    callback.onError(message);
                }
        );
        ApiClient.enqueue(appContext, req);
    }

    public void getCustomerById(int customerId, CustomerCallback callback) {
        JsonObjectRequest req = ApiClient.jsonRequest(
                appContext,
                Request.Method.GET,
                "customers/" + customerId,
                null,
                response -> {
                    Log.d("CustomerRepository", "Get customer by ID response=" + response.toString());
                    try {
                        Customer customer = Customer.fromJson(response);
                        callback.onSuccess(customer);
                    } catch (JSONException e) {
                        android.util.Log.w("CustomerRepository", "Standard parsing failed, trying safe parsing: " + e.getMessage());
                        try {
                            Customer customer = Customer.safeFromJson(response);
                            callback.onSuccess(customer);
                        } catch (Exception ex) {
                            callback.onError("Failed to parse customer response: " + ex.getMessage());
                        }
                    }
                },
                error -> {
                    String message = parseError(error);
                    Log.e("CustomerRepository", "Get customer by ID failed: " + message, error);
                    callback.onError(message);
                }
        );
        ApiClient.enqueue(appContext, req);
    }

    public void updateCustomer(int customerId, String name, String email, String phone, 
                              String address, int userId, int loyaltyPoints, CustomerCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("name", name);
            body.put("email", email);
            body.put("phone", phone);
            body.put("address", address != null ? address : "");
            body.put("user_id", userId);
            body.put("loyalty_points", loyaltyPoints);

            Log.d("CustomerRepository", "Update customer request body=" + body.toString());

            ApiClient.enqueue(appContext, ApiClient.jsonRequest(
                    appContext,
                    Request.Method.PUT,
                    "customers/" + customerId,
                    body,
                    response -> {
                        Log.d("CustomerRepository", "Update customer response=" + response.toString());
                        try {
                            Customer customer = Customer.fromJson(response);
                            callback.onSuccess(customer);
                        } catch (JSONException e) {
                            android.util.Log.w("CustomerRepository", "Standard parsing failed, trying safe parsing: " + e.getMessage());
                            try {
                                Customer customer = Customer.safeFromJson(response);
                                callback.onSuccess(customer);
                            } catch (Exception ex) {
                                callback.onError("Failed to parse customer response: " + ex.getMessage());
                            }
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

    public void deleteCustomer(int customerId, CustomerCallback callback) {
        JsonObjectRequest req = ApiClient.jsonRequest(
                appContext,
                Request.Method.DELETE,
                "customers/" + customerId,
                null,
                response -> {
                    Log.d("CustomerRepository", "Delete customer response=" + response.toString());
                    // For delete operations, we might not get a customer object back
                    // Create a dummy customer with the ID to indicate successful deletion
                    Customer deletedCustomer = new Customer();
                    deletedCustomer.setId(customerId);
                    callback.onSuccess(deletedCustomer);
                },
                error -> {
                    String message = parseError(error);
                    Log.e("CustomerRepository", "Delete customer failed: " + message, error);
                    callback.onError(message);
                }
        );
        ApiClient.enqueue(appContext, req);
    }

    private void handleError(VolleyError error, CustomerCallback callback) {
        String message = parseError(error);
        Log.e("CustomerRepository", "API Error: " + message, error);
        callback.onError(message);
    }

    private String parseError(VolleyError error) {
        if (error.networkResponse != null) {
            try {
                String responseBody = new String(error.networkResponse.data, "utf-8");
                JSONObject jsonResponse = new JSONObject(responseBody);
                if (jsonResponse.has("message")) {
                    return jsonResponse.getString("message");
                }
                if (jsonResponse.has("error")) {
                    return jsonResponse.getString("error");
                }
            } catch (Exception e) {
                Log.e("CustomerRepository", "Error parsing error response", e);
            }
        }
        return error.getMessage() != null ? error.getMessage() : "Unknown error occurred";
    }
}
