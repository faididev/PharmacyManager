package com.example.pharmacymanager.data.remote;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.pharmacymanager.data.local.SessionManager;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ApiClient {

    public static JsonObjectRequest jsonRequest(
            Context context,
            int method,
            String path,
            JSONObject body,
            com.android.volley.Response.Listener<JSONObject> listener,
            com.android.volley.Response.ErrorListener errorListener
    ) {
        final String url = ApiConfig.buildUrl(path);
        Log.d("ApiClient", "Request url=" + url + ", body=" + (body != null ? body.toString() : "null"));
        return new JsonObjectRequest(method, url, body, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                String token = new SessionManager(context).getToken();
                if (token != null && !token.isEmpty()) {
                    headers.put("Authorization", "Bearer " + token);
                }
                return headers;
            }
        };
    }

    public static <T> void enqueue(Context context, Request<T> request) {
        RequestQueueSingleton.getInstance(context).addToRequestQueue(request);
    }
}




