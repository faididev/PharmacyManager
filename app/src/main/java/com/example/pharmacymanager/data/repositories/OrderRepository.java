package com.example.pharmacymanager.data.repositories;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.pharmacymanager.data.entities.Order;
import com.example.pharmacymanager.data.entities.OrderItem;
import com.example.pharmacymanager.data.remote.ApiClient;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONArray;

import java.util.List;

public class OrderRepository {
    private final Context appContext;

    public interface OrderCallback {
        void onSuccess(Order order);
        void onError(String message);
    }

    public interface OrderListCallback {
        void onSuccess(JSONObject response);
        void onError(String message);
    }

    public OrderRepository(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public void createOrder(int customerId, String orderDate, String status, List<OrderItem> items, OrderCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("customer_id", customerId);
            body.put("order_date", orderDate);
            body.put("status", status);
            
            JSONArray itemsArray = new JSONArray();
            for (OrderItem item : items) {
                itemsArray.put(item.toJson());
            }
            body.put("items", itemsArray);

            Log.d("OrderRepository", "Create order request body=" + body.toString());

            ApiClient.enqueue(appContext, ApiClient.jsonRequest(
                    appContext,
                    Request.Method.POST,
                    "orders",
                    body,
                    response -> {
                        Log.d("OrderRepository", "Create order response=" + response.toString());
                        try {
                            Order order = Order.fromJson(response);
                            callback.onSuccess(order);
                        } catch (JSONException e) {
                            android.util.Log.w("OrderRepository", "Standard parsing failed, trying safe parsing: " + e.getMessage());
                            try {
                                Order order = Order.safeFromJson(response);
                                callback.onSuccess(order);
                            } catch (Exception ex) {
                                callback.onError("Failed to parse order response: " + ex.getMessage());
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

    public void getOrders(OrderListCallback callback) {
        JsonObjectRequest req = ApiClient.jsonRequest(
                appContext,
                Request.Method.GET,
                "orders",
                null,
                response -> {
                    Log.d("OrderRepository", "Get orders response=" + response.toString());
                    callback.onSuccess(response);
                },
                error -> {
                    String message = parseError(error);
                    Log.e("OrderRepository", "Get orders failed: " + message, error);
                    callback.onError(message);
                }
        );
        ApiClient.enqueue(appContext, req);
    }

    public void getOrderById(int orderId, OrderCallback callback) {
        JsonObjectRequest req = ApiClient.jsonRequest(
                appContext,
                Request.Method.GET,
                "orders/" + orderId,
                null,
                response -> {
                    Log.d("OrderRepository", "Get order by ID response=" + response.toString());
                    try {
                        Order order = Order.fromJson(response);
                        callback.onSuccess(order);
                    } catch (JSONException e) {
                        android.util.Log.w("OrderRepository", "Standard parsing failed, trying safe parsing: " + e.getMessage());
                        try {
                            Order order = Order.safeFromJson(response);
                            callback.onSuccess(order);
                        } catch (Exception ex) {
                            callback.onError("Failed to parse order response: " + ex.getMessage());
                        }
                    }
                },
                error -> {
                    String message = parseError(error);
                    Log.e("OrderRepository", "Get order by ID failed: " + message, error);
                    callback.onError(message);
                }
        );
        ApiClient.enqueue(appContext, req);
    }

    public void updateOrder(int orderId, int customerId, String orderDate, String status, List<OrderItem> items, OrderCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("customer_id", customerId);
            body.put("order_date", orderDate);
            body.put("status", status);
            
            JSONArray itemsArray = new JSONArray();
            for (OrderItem item : items) {
                itemsArray.put(item.toJson());
            }
            body.put("items", itemsArray);

            Log.d("OrderRepository", "Update order request body=" + body.toString());

            ApiClient.enqueue(appContext, ApiClient.jsonRequest(
                    appContext,
                    Request.Method.PUT,
                    "orders/" + orderId,
                    body,
                    response -> {
                        Log.d("OrderRepository", "Update order response=" + response.toString());
                        try {
                            Order order = Order.fromJson(response);
                            callback.onSuccess(order);
                        } catch (JSONException e) {
                            android.util.Log.w("OrderRepository", "Standard parsing failed, trying safe parsing: " + e.getMessage());
                            try {
                                Order order = Order.safeFromJson(response);
                                callback.onSuccess(order);
                            } catch (Exception ex) {
                                callback.onError("Failed to parse order response: " + ex.getMessage());
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

    public void updateOrderStatus(int orderId, String status, OrderCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("status", status);

            Log.d("OrderRepository", "Update order status request body=" + body.toString());

            ApiClient.enqueue(appContext, ApiClient.jsonRequest(
                    appContext,
                    Request.Method.PATCH,
                    "orders/" + orderId + "/status",
                    body,
                    response -> {
                        Log.d("OrderRepository", "Update order status response=" + response.toString());
                        try {
                            Order order = Order.fromJson(response);
                            callback.onSuccess(order);
                        } catch (JSONException e) {
                            android.util.Log.w("OrderRepository", "Standard parsing failed, trying safe parsing: " + e.getMessage());
                            try {
                                Order order = Order.safeFromJson(response);
                                callback.onSuccess(order);
                            } catch (Exception ex) {
                                callback.onError("Failed to parse order response: " + ex.getMessage());
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

    public void deleteOrder(int orderId, OrderCallback callback) {
        JsonObjectRequest req = ApiClient.jsonRequest(
                appContext,
                Request.Method.DELETE,
                "orders/" + orderId,
                null,
                response -> {
                    Log.d("OrderRepository", "Delete order response=" + response.toString());
                    Order deletedOrder = new Order();
                    deletedOrder.setId(orderId);
                    callback.onSuccess(deletedOrder);
                },
                error -> {
                    String message = parseError(error);
                    Log.e("OrderRepository", "Delete order failed: " + message, error);
                    callback.onError(message);
                }
        );
        ApiClient.enqueue(appContext, req);
    }

    private void handleError(VolleyError error, OrderCallback callback) {
        String message = parseError(error);
        Log.e("OrderRepository", "API Error: " + message, error);
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
                Log.e("OrderRepository", "Error parsing error response", e);
            }
        }
        return error.getMessage() != null ? error.getMessage() : "Unknown error occurred";
    }
}
