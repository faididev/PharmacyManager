package com.example.pharmacymanager.data.repositories;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.example.pharmacymanager.data.local.SessionManager;
import com.example.pharmacymanager.data.remote.ApiClient;

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

    // Registration flow removed to simplify project

    private void handleAuthResponse(JSONObject response, AuthCallback callback) {
        try {
            String token = null;
            if (response.has("token")) {
                token = response.getString("token");
            } else if (response.has("access_token")) {
                token = response.getString("access_token");
            } else if (response.has("data")) {
                JSONObject data = response.getJSONObject("data");
                if (data.has("token")) token = data.getString("token");
                else if (data.has("access_token")) token = data.getString("access_token");
            }

            if (token != null && !token.isEmpty()) {
                sessionManager.saveToken(token);
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


