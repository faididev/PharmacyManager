package com.example.pharmacymanager.data.remote;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.pharmacymanager.data.local.SessionManager;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
                Log.d("ApiClient", "JSON request - Token check: " + (token != null ? "Token present (length: " + token.length() + ")" : "Token null"));
                
                if (token != null && !token.isEmpty()) {
                    headers.put("Authorization", "Bearer " + token);
                    Log.d("ApiClient", "Token added to headers for JSON request: " + url);
                    Log.d("ApiClient", "Authorization header: Bearer " + token.substring(0, Math.min(20, token.length())) + "...");
                } else {
                    Log.w("ApiClient", "No token available for JSON request: " + url);
                    Log.w("ApiClient", "Token is null or empty - this will cause authentication failure");
                }
                return headers;
            }
        };
    }

    public static StringRequest stringRequest(
            Context context,
            int method,
            String path,
            String body,
            com.android.volley.Response.Listener<String> listener,
            com.android.volley.Response.ErrorListener errorListener
    ) {
        final String url = ApiConfig.buildUrl(path);
        Log.d("ApiClient", "String request url=" + url + ", body=" + body);
        return new StringRequest(method, url, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                headers.put("Content-Type", "application/json");
                String token = new SessionManager(context).getToken();
                if (token != null && !token.isEmpty()) {
                    headers.put("Authorization", "Bearer " + token);
                    Log.d("ApiClient", "Token added to headers for String request: " + url);
                } else {
                    Log.w("ApiClient", "No token available for String request: " + url);
                }
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                if (body != null) {
                    return body.getBytes();
                }
                return super.getBody();
            }
        };
    }

    public static StringRequest multipartRequest(
            Context context,
            int method,
            String path,
            Map<String, String> params,
            Map<String, File> files,
            com.android.volley.Response.Listener<String> listener,
            com.android.volley.Response.ErrorListener errorListener
    ) {
        final String url = ApiConfig.buildUrl(path);
        Log.d("ApiClient", "Multipart request url=" + url);
        
        return new StringRequest(method, url, listener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Accept", "application/json");
                String token = new SessionManager(context).getToken();
                Log.d("ApiClient", "Multipart request - Token check: " + (token != null ? "Token present (length: " + token.length() + ")" : "Token null"));
                
                if (token != null && !token.isEmpty()) {
                    headers.put("Authorization", "Bearer " + token);
                    Log.d("ApiClient", "Token added to headers for multipart request: " + url);
                    Log.d("ApiClient", "Authorization header: Bearer " + token.substring(0, Math.min(20, token.length())) + "...");
                } else {
                    Log.w("ApiClient", "No token available for multipart request: " + url);
                    Log.w("ApiClient", "Token is null or empty - this will cause authentication failure");
                }
                return headers;
            }

            @Override
            public String getBodyContentType() {
                String boundary = getBoundary();
                return "multipart/form-data; boundary=" + boundary;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                String boundary = getBoundary();
                
                try {
                    Log.d("ApiClient", "Creating multipart body with boundary: " + boundary);
                    
                    // Add text parameters
                    if (params != null) {
                        Log.d("ApiClient", "Adding " + params.size() + " text parameters");
                        for (Map.Entry<String, String> entry : params.entrySet()) {
                            bos.write(("--" + boundary + "\r\n").getBytes());
                            bos.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"\r\n").getBytes());
                            bos.write(("Content-Type: text/plain\r\n\r\n").getBytes());
                            bos.write(entry.getValue().getBytes("UTF-8"));
                            bos.write("\r\n".getBytes());
                            Log.d("ApiClient", "Added parameter: " + entry.getKey() + " = " + entry.getValue());
                        }
                    }
                    
                    // Add file parameters
                    if (files != null) {
                        Log.d("ApiClient", "Adding " + files.size() + " file parameters");
                        for (Map.Entry<String, File> entry : files.entrySet()) {
                            File file = entry.getValue();
                            if (file != null && file.exists()) {
                                Log.d("ApiClient", "Adding file: " + entry.getKey() + " = " + file.getName() + " (" + file.length() + " bytes)");
                                bos.write(("--" + boundary + "\r\n").getBytes());
                                bos.write(("Content-Disposition: form-data; name=\"" + entry.getKey() + "\"; filename=\"" + file.getName() + "\"\r\n").getBytes());
                                bos.write(("Content-Type: image/jpeg\r\n\r\n").getBytes());
                                
                                FileInputStream fis = new FileInputStream(file);
                                byte[] buffer = new byte[1024];
                                int bytesRead;
                                while ((bytesRead = fis.read(buffer)) != -1) {
                                    bos.write(buffer, 0, bytesRead);
                                }
                                fis.close();
                                bos.write("\r\n".getBytes());
                            } else {
                                Log.w("ApiClient", "File does not exist or is null: " + (file != null ? file.getAbsolutePath() : "null"));
                            }
                        }
                    }
                    
                    bos.write(("--" + boundary + "--\r\n").getBytes());
                    
                    byte[] bodyBytes = bos.toByteArray();
                    Log.d("ApiClient", "Multipart body created successfully, size: " + bodyBytes.length + " bytes");
                    
                    // Debug: Log the first 500 characters of the body to see the format
                    String bodyPreview = new String(bodyBytes, 0, Math.min(500, bodyBytes.length), "UTF-8");
                    Log.d("ApiClient", "Multipart body preview: " + bodyPreview);
                    
                    return bodyBytes;
                } catch (IOException e) {
                    Log.e("ApiClient", "Error creating multipart body", e);
                    return new byte[0];
                }
            }
            
            private String getBoundary() {
                return "----FormBoundary" + System.currentTimeMillis();
            }
        };
    }

    public static <T> void enqueue(Context context, Request<T> request) {
        RequestQueueSingleton.getInstance(context).addToRequestQueue(request);
    }
}




