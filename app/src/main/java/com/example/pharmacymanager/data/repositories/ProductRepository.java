package com.example.pharmacymanager.data.repositories;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.pharmacymanager.data.remote.ApiClient;

import org.json.JSONObject;

public class ProductRepository {
    private final Context appContext;

    public interface ProductCallback {
        void onSuccess(JSONObject response);
        void onError(String message);
    }

    public ProductRepository(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public void getProducts(Integer perPage, String search, Integer categoryId, String include, ProductCallback callback) {
        StringBuilder path = new StringBuilder("products");
        String sep = "?";
        if (perPage != null) { path.append(sep).append("per_page=").append(perPage); sep = "&"; }
        if (search != null && !search.isEmpty()) { path.append(sep).append("search=").append(search); sep = "&"; }
        if (categoryId != null) { path.append(sep).append("category_id=").append(categoryId); sep = "&"; }
        if (include != null && !include.isEmpty()) { path.append(sep).append("include=").append(include); }

        JsonObjectRequest req = ApiClient.jsonRequest(
                appContext,
                Request.Method.GET,
                path.toString(),
                null,
                callback::onSuccess,
                error -> callback.onError(parseError(error))
        );
        ApiClient.enqueue(appContext, req);
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




