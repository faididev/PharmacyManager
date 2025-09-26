package com.example.pharmacymanager.data.repositories;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.pharmacymanager.data.entities.Category;
import com.example.pharmacymanager.data.remote.ApiClient;

import org.json.JSONException;
import org.json.JSONObject;

public class CategoryRepository {
    private final Context appContext;

    public interface CategoryCallback {
        void onSuccess(Category category);
        void onError(String message);
    }

    public interface CategoryListCallback {
        void onSuccess(JSONObject response);
        void onError(String message);
    }

    public CategoryRepository(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public void createCategory(String name, String description, CategoryCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("name", name);
            body.put("description", description);

            Log.d("CategoryRepository", "Create category request body=" + body.toString());

            ApiClient.enqueue(appContext, ApiClient.jsonRequest(
                    appContext,
                    Request.Method.POST,
                    "categories",
                    body,
                    response -> {
                        Log.d("CategoryRepository", "Create category response=" + response.toString());
                        try {
                            Category category = Category.fromJson(response);
                            callback.onSuccess(category);
                        } catch (JSONException e) {
                            callback.onError("Failed to parse category response: " + e.getMessage());
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

    public void getCategories(CategoryListCallback callback) {
        JsonObjectRequest req = ApiClient.jsonRequest(
                appContext,
                Request.Method.GET,
                "categories",
                null,
                response -> {
                    Log.d("CategoryRepository", "Get categories response=" + response.toString());
                    callback.onSuccess(response);
                },
                error -> {
                    String message = parseError(error);
                    Log.e("CategoryRepository", "Get categories failed: " + message, error);
                    callback.onError(message);
                }
        );
        ApiClient.enqueue(appContext, req);
    }

    public void updateCategory(int categoryId, String name, String description, CategoryCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("name", name);
            body.put("description", description);

            Log.d("CategoryRepository", "Update category request body=" + body.toString());

            ApiClient.enqueue(appContext, ApiClient.jsonRequest(
                    appContext,
                    Request.Method.PUT,
                    "categories/" + categoryId,
                    body,
                    response -> {
                        Log.d("CategoryRepository", "Update category response=" + response.toString());
                        try {
                            Category category = Category.fromJson(response);
                            callback.onSuccess(category);
                        } catch (JSONException e) {
                            callback.onError("Failed to parse category response: " + e.getMessage());
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

    public void deleteCategory(int categoryId, CategoryCallback callback) {
        Log.d("CategoryRepository", "Deleting category with ID: " + categoryId);
        ApiClient.enqueue(appContext, ApiClient.stringRequest(
                appContext,
                Request.Method.DELETE,
                "categories/" + categoryId,
                null,
                response -> {
                    Log.d("CategoryRepository", "Delete category successful, response=" + response);
                    // For delete, we don't need to parse the response (204 No Content)
                    callback.onSuccess(null);
                },
                error -> {
                    Log.e("CategoryRepository", "Delete category failed", error);
                    handleError(error, callback);
                }
        ));
    }

    private void handleError(VolleyError error, CategoryCallback callback) {
        String message = error.getMessage();
        if (error.networkResponse != null) {
            String body = null;
            try {
                body = new String(error.networkResponse.data);
            } catch (Exception ignored) {}
            message = "HTTP " + error.networkResponse.statusCode + (body != null ? (": " + body) : "");
        }
        if (message == null) message = "Unknown error";
        Log.e("CategoryRepository", "Category request failed: " + message, error);
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

