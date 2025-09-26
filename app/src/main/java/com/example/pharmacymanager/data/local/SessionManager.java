package com.example.pharmacymanager.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class SessionManager {
    private static final String PREF_NAME = "pharmacy_prefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";

    private final SharedPreferences preferences;

    public SessionManager(Context context) {
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        Log.d("SessionManager", "Saving token: " + (token != null ? "Token present" : "Token null"));
        preferences.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        String token = preferences.getString(KEY_TOKEN, null);
        Log.d("SessionManager", "Retrieved token: " + (token != null ? "Token present" : "Token null"));
        return token;
    }

    public void saveUserId(int userId) {
        Log.d("SessionManager", "Saving user ID: " + userId);
        preferences.edit().putInt(KEY_USER_ID, userId).apply();
    }

    public int getUserId() {
        int userId = preferences.getInt(KEY_USER_ID, -1);
        Log.d("SessionManager", "Retrieved user ID: " + userId);
        return userId;
    }

    public void clear() {
        Log.d("SessionManager", "Clearing session");
        preferences.edit().clear().apply();
    }
}







