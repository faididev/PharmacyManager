package com.example.pharmacymanager.data.repositories;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.example.pharmacymanager.data.local.SessionManager;
import com.example.pharmacymanager.data.remote.ApiClient;
import com.example.pharmacymanager.data.entities.User;
import com.example.pharmacymanager.data.entities.Customer;

import org.json.JSONException;
import org.json.JSONObject;

public class AuthRepository {

    private final Context appContext;
    private final SessionManager sessionManager;

    public interface AuthCallback {
        void onSuccess();
        void onError(String message);
    }

    public AuthRepository(Context context) {
        this.appContext = context.getApplicationContext();
        this.sessionManager = new SessionManager(appContext);
    }

    public void register(String username, String email, String password, AuthCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("name", username);
            body.put("email", email);
            body.put("password", password);
            body.put("password_confirmation", password);

            Log.d("AuthRepository", "Register request body=" + body.toString());

            ApiClient.enqueue(appContext, ApiClient.jsonRequest(
                    appContext,
                    Request.Method.POST,
                    "auth/register",
                    body,
                    response -> {
                        Log.d("AuthRepository", "Register response=" + response.toString());
                        handleAuthResponse(response, callback);
                    },
                    error -> {
                        handleError(error, callback);
                    }
            ));
        } catch (JSONException e) {
            callback.onError(e.getMessage());
        }
    }

    public void registerWithCustomer(String username, String email, String password, String phone, String address, int loyaltyPoints, AuthCallback callback) {
        Log.d("AuthRepository", "Starting two-step registration: User -> Customer");
        
        // Step 1: Create User
        UserRepository userRepository = new UserRepository(appContext);
        userRepository.createUser(username, email, password, phone, address, new UserRepository.UserCallback() {
            @Override
            public void onSuccess(User user) {
                Log.d("AuthRepository", "User created successfully with ID: " + user.getId());
                
                // Step 2: Create Customer with the user ID
                createCustomerFromUser(user, password, loyaltyPoints, callback);
            }

            @Override
            public void onError(String message) {
                Log.e("AuthRepository", "Failed to create user: " + message);
                callback.onError("Failed to create user: " + message);
            }
        });
    }

    private void createCustomerFromUser(User user, String password, int loyaltyPoints, AuthCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("name", user.getName());
            body.put("email", user.getEmail());
            body.put("phone", user.getPhone() != null ? user.getPhone() : "");
            body.put("address", user.getAddress() != null ? user.getAddress() : "");
            body.put("user_id", user.getId());
            body.put("loyalty_points", loyaltyPoints);

            Log.d("AuthRepository", "Create customer request body=" + body.toString());

            ApiClient.enqueue(appContext, ApiClient.jsonRequest(
                    appContext,
                    Request.Method.POST,
                    "customers", // Customer endpoint
                    body,
                    response -> {
                        Log.d("AuthRepository", "Create customer response=" + response.toString());
                        try {
                            Customer customer = Customer.fromJson(response);
                            Log.d("AuthRepository", "Customer created successfully with ID: " + customer.getId());
                            
                            // Now login the user to get the token
                            Log.d("AuthRepository", "Customer created successfully, now logging in user");
                            login(user.getEmail(), password, callback);
                        } catch (JSONException e) {
                            Log.e("AuthRepository", "Failed to parse customer response: " + e.getMessage());
                            callback.onError("Customer created but failed to parse response: " + e.getMessage());
                        }
                    },
                    error -> {
                        Log.e("AuthRepository", "Failed to create customer: " + error.getMessage());
                        callback.onError("Failed to create customer: " + error.getMessage());
                    }
            ));
        } catch (JSONException e) {
            Log.e("AuthRepository", "Failed to create customer request: " + e.getMessage());
            callback.onError("Failed to create customer request: " + e.getMessage());
        }
    }

    public void login(String email, String password, AuthCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("email", email);
            body.put("password", password);

            Log.d("AuthRepository", "Login request body=" + body.toString());

            ApiClient.enqueue(appContext, ApiClient.jsonRequest(
                    appContext,
                    Request.Method.POST,
                    "auth/login",
                    body,
                    response -> {
                        Log.d("AuthRepository", "Login response=" + response.toString());
                        handleAuthResponse(response, callback);
                    },
                    error -> {
                        handleError(error, callback);
                    }
            ));
        } catch (JSONException e) {
            callback.onError(e.getMessage());
        }
    }

    public void logout(AuthCallback callback) {
        Log.d("AuthRepository", "Starting logout process");
        
        // Get the current token
        String token = sessionManager.getToken();
        if (token == null || token.isEmpty()) {
            Log.w("AuthRepository", "No token found, clearing local session only");
            sessionManager.clear();
            callback.onSuccess();
            return;
        }

        // Make API call to logout
        ApiClient.enqueue(appContext, ApiClient.jsonRequest(
                appContext,
                Request.Method.POST,
                "auth/logout",
                null,
                response -> {
                    Log.d("AuthRepository", "Logout API response=" + response.toString());
                    // Clear local session regardless of API response
                    sessionManager.clear();
                    callback.onSuccess();
                },
                error -> {
                    Log.w("AuthRepository", "Logout API failed, but clearing local session: " + error.getMessage());
                    // Clear local session even if API call fails
                    sessionManager.clear();
                    callback.onSuccess();
                }
        ));
    }

    // Registration flow removed to simplify project

    private void handleAuthResponse(JSONObject response, AuthCallback callback) {
        try {
            String token = null;
            int userId = -1;
            
            // Extract token
            if (response.has("token")) {
                token = response.getString("token");
            } else if (response.has("access_token")) {
                token = response.getString("access_token");
            } else if (response.has("data")) {
                JSONObject data = response.getJSONObject("data");
                if (data.has("token")) token = data.getString("token");
                else if (data.has("access_token")) token = data.getString("access_token");
            }

            // Extract user ID
            if (response.has("user")) {
                JSONObject user = response.getJSONObject("user");
                if (user.has("id")) {
                    userId = user.getInt("id");
                }
            } else if (response.has("data")) {
                JSONObject data = response.getJSONObject("data");
                if (data.has("user")) {
                    JSONObject user = data.getJSONObject("user");
                    if (user.has("id")) {
                        userId = user.getInt("id");
                    }
                } else if (data.has("id")) {
                    userId = data.getInt("id");
                }
            } else if (response.has("id")) {
                userId = response.getInt("id");
            }

            if (token != null && !token.isEmpty()) {
                sessionManager.saveToken(token);
                
                // Save user ID if found
                if (userId != -1) {
                    sessionManager.saveUserId(userId);
                    Log.d("AuthRepository", "Saved user ID: " + userId);
                } else {
                    Log.w("AuthRepository", "User ID not found in response");
                }
                
                callback.onSuccess();
            } else {
                callback.onError("Token not found in response");
            }
        } catch (JSONException e) {
            callback.onError(e.getMessage());
        }
    }

    private void handleError(VolleyError error, AuthCallback callback) {
        String message = error.getMessage();
        if (error.networkResponse != null) {
            String body = null;
            try {
                body = new String(error.networkResponse.data);
            } catch (Exception ignored) {}
            message = "HTTP " + error.networkResponse.statusCode + (body != null ? (": " + body) : "");
        }
        if (message == null) message = "Unknown error";
        Log.e("AuthRepository", "Auth request failed: " + message, error);
        callback.onError(message);
    }
}


