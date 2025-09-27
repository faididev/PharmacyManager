package com.example.pharmacymanager.data.repositories;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.example.pharmacymanager.data.entities.User;
import com.example.pharmacymanager.data.remote.ApiClient;

import org.json.JSONException;
import org.json.JSONObject;

public class UserRepository {
    private final Context appContext;

    public interface UserCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public UserRepository(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public void createUser(String name, String email, String password, String phone, String address, UserCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("name", name);
            body.put("email", email);
            body.put("password", password);
            body.put("password_confirmation", password);
            body.put("phone", phone != null ? phone : "");
            body.put("address", address != null ? address : "");

            Log.d("UserRepository", "Create user request body=" + body.toString());

            ApiClient.enqueue(appContext, ApiClient.jsonRequest(
                    appContext,
                    Request.Method.POST,
                    "users", // User endpoint
                    body,
                    response -> {
                        Log.d("UserRepository", "Create user response=" + response.toString());
                        try {
                            User user = parseUserFromResponse(response);
                            callback.onSuccess(user);
                        } catch (JSONException e) {
                            callback.onError("Failed to parse user response: " + e.getMessage());
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

    private User parseUserFromResponse(JSONObject response) throws JSONException {
        Log.d("UserRepository", "Parsing user response: " + response.toString());
        
        JSONObject userData;
        
        // Check if this is a single user response (has "data" wrapper)
        if (response.has("data")) {
            userData = response.getJSONObject("data");
            Log.d("UserRepository", "Single user response detected");
        } else {
            // This is a direct user object
            userData = response;
            Log.d("UserRepository", "Direct user object detected");
        }
        
        // Try different response formats
        long id;
        String uuid;
        String name;
        String email;
        String phone;
        String address;
        String createdAt;
        String updatedAt;
        
        // Format 1: With attributes wrapper
        if (userData.has("attributes")) {
            Log.d("UserRepository", "Using attributes format");
            JSONObject attributes = userData.getJSONObject("attributes");
            
            id = userData.getLong("id");
            uuid = userData.optString("uuid", "");
            name = attributes.getString("name");
            email = attributes.getString("email");
            phone = attributes.optString("phone", "");
            address = attributes.optString("address", "");
            createdAt = attributes.optString("createdAt", "");
            updatedAt = attributes.optString("updatedAt", "");
        }
        // Format 2: Direct fields (no attributes wrapper)
        else {
            Log.d("UserRepository", "Using direct fields format");
            
            id = userData.getLong("id");
            uuid = userData.optString("uuid", "");
            name = userData.getString("name");
            email = userData.getString("email");
            phone = userData.optString("phone", "");
            address = userData.optString("address", "");
            createdAt = userData.optString("createdAt", "");
            updatedAt = userData.optString("updatedAt", "");
        }
        
        Log.d("UserRepository", "Parsed - ID: " + id + ", Name: " + name + ", Email: " + email);
        
        return new User(id, uuid, name, email, phone, address, 
                       null, null, null, null, null, createdAt, updatedAt);
    }

    private void handleError(VolleyError error, UserCallback callback) {
        String message = error.getMessage();
        
        // Enhanced error logging
        Log.e("UserRepository", "=== VOLLEY ERROR DETAILS ===");
        Log.e("UserRepository", "Error message: " + message);
        Log.e("UserRepository", "Error class: " + error.getClass().getSimpleName());
        Log.e("UserRepository", "Error toString: " + error.toString());
        
        if (error.networkResponse != null) {
            Log.e("UserRepository", "Network response status code: " + error.networkResponse.statusCode);
            Log.e("UserRepository", "Network response headers: " + error.networkResponse.headers.toString());
            
            String body = null;
            try {
                body = new String(error.networkResponse.data);
                Log.e("UserRepository", "Network response body: " + body);
            } catch (Exception e) {
                Log.e("UserRepository", "Error reading response body: " + e.getMessage());
            }
            message = "HTTP " + error.networkResponse.statusCode + (body != null ? (": " + body) : "");
        } else {
            Log.e("UserRepository", "No network response - connection error");
            Log.e("UserRepository", "This usually indicates: Network connectivity issues, Server down, or DNS resolution problems");
            message = "Connection error: " + (message != null ? message : "Unable to connect to server");
        }
        
        if (message == null) message = "Unknown error";
        Log.e("UserRepository", "Final error message: " + message);
        Log.e("UserRepository", "=== END ERROR DETAILS ===");
        
        callback.onError(message);
    }
}
