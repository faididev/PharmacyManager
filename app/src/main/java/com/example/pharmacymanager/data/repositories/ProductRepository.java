package com.example.pharmacymanager.data.repositories;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.pharmacymanager.data.entities.Product;
import com.example.pharmacymanager.data.remote.ApiClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ProductRepository {
    private final Context appContext;

    public interface ProductCallback {
        void onSuccess(Product product);
        void onError(String message);
    }

    public interface ProductListCallback {
        void onSuccess(JSONObject response);
        void onError(String message);
    }

    public ProductRepository(Context context) {
        this.appContext = context.getApplicationContext();
    }

    public void createProduct(String name, String sku, String description, int quantity, 
                             int total, String manufactureDate, String expiryDate, 
                             int categoryId, double price, ProductCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("name", name);
            body.put("sku", sku);
            body.put("description", description);
            body.put("quantity", quantity);
            body.put("total", total);
            body.put("manufacture_date", manufactureDate);
            body.put("expiry_date", expiryDate);
            body.put("category_id", categoryId);
            body.put("price", price);

            Log.d("ProductRepository", "Create product request body=" + body.toString());

            ApiClient.enqueue(appContext, ApiClient.jsonRequest(
                    appContext,
                    Request.Method.POST,
                    "products",
                    body,
                    response -> {
                        Log.d("ProductRepository", "Create product response=" + response.toString());
                        try {
                            Product product = Product.fromJson(response);
                            callback.onSuccess(product);
                        } catch (JSONException e) {
                            callback.onError("Failed to parse product response: " + e.getMessage());
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

    public void createProductWithImage(String name, String sku, String description, int quantity, 
                                     int total, String manufactureDate, String expiryDate, 
                                     int categoryId, double price, Uri imageUri, ProductCallback callback) {
        try {
            // Prepare form data
            Map<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("sku", sku);
            params.put("description", description);
            params.put("quantity", String.valueOf(quantity));
            params.put("total", String.valueOf(total));
            params.put("manufacture_date", manufactureDate);
            params.put("expiry_date", expiryDate);
            params.put("category_id", String.valueOf(categoryId));
            params.put("price", String.valueOf(price));

            // Prepare file data
            Map<String, File> files = new HashMap<>();
            if (imageUri != null) {
                try {
                    File imageFile = createTempFileFromUri(imageUri);
                    if (imageFile != null && imageFile.exists()) {
                        files.put("image", imageFile);
                        Log.d("ProductRepository", "Image file created: " + imageFile.getAbsolutePath());
                    }
                } catch (Exception e) {
                    Log.e("ProductRepository", "Error creating file from URI: " + e.getMessage());
                }
            }

            Log.d("ProductRepository", "Create product with image - params=" + params.toString());
            Log.d("ProductRepository", "Create product with image - files=" + files.toString());

            StringRequest request = ApiClient.multipartRequest(
                    appContext,
                    Request.Method.POST,
                    "products",
                    params,
                    files,
                    response -> {
                        Log.d("ProductRepository", "Create product with image response=" + response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Product product = Product.fromJson(jsonResponse);
                            callback.onSuccess(product);
                        } catch (JSONException e) {
                            Log.e("ProductRepository", "Failed to parse product response: " + e.getMessage());
                            Log.e("ProductRepository", "Raw response: " + response);
                            callback.onError("Failed to parse product response: " + e.getMessage());
                        }
                    },
                    error -> {
                        Log.e("ProductRepository", "Create product with image failed", error);
                        handleError(error, callback);
                    }
            );

            ApiClient.enqueue(appContext, request);
        } catch (Exception e) {
            callback.onError("Error creating product with image: " + e.getMessage());
        }
    }

    public void getProducts(ProductListCallback callback) {
        JsonObjectRequest req = ApiClient.jsonRequest(
                appContext,
                Request.Method.GET,
                "products",
                null,
                response -> {
                    Log.d("ProductRepository", "Get products response=" + response.toString());
                    callback.onSuccess(response);
                },
                error -> {
                    String message = parseError(error);
                    Log.e("ProductRepository", "Get products failed: " + message, error);
                    callback.onError(message);
                }
        );
        ApiClient.enqueue(appContext, req);
    }

    public void getProductById(String uuid, ProductCallback callback) {
        JsonObjectRequest req = ApiClient.jsonRequest(
                appContext,
                Request.Method.GET,
                "products/" + uuid,
                null,
                response -> {
                    Log.d("ProductRepository", "Get product by ID response=" + response.toString());
                    try {
                        Product product = Product.fromJson(response);
                        callback.onSuccess(product);
                    } catch (JSONException e) {
                        callback.onError("Failed to parse product response: " + e.getMessage());
                    }
                },
                error -> {
                    String message = parseError(error);
                    Log.e("ProductRepository", "Get product by ID failed: " + message, error);
                    callback.onError(message);
                }
        );
        ApiClient.enqueue(appContext, req);
    }

    public void updateProduct(String uuid, String name, String sku, String description, 
                             int quantity, int total, String manufactureDate, String expiryDate, 
                             int categoryId, double price, ProductCallback callback) {
        try {
            JSONObject body = new JSONObject();
            body.put("name", name);
            body.put("sku", sku);
            body.put("description", description);
            body.put("quantity", quantity);
            body.put("total", total);
            body.put("manufacture_date", manufactureDate);
            body.put("expiry_date", expiryDate);
            body.put("category_id", categoryId);
            body.put("price", price);

            Log.d("ProductRepository", "Update product request body=" + body.toString());

            ApiClient.enqueue(appContext, ApiClient.jsonRequest(
                    appContext,
                    Request.Method.PUT,
                    "products/" + uuid,
                    body,
                    response -> {
                        Log.d("ProductRepository", "Update product response=" + response.toString());
                        try {
                            Product product = Product.fromJson(response);
                            callback.onSuccess(product);
                        } catch (JSONException e) {
                            callback.onError("Failed to parse product response: " + e.getMessage());
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

    public void updateProductWithImage(String uuid, String name, String sku, String description, 
                                      int quantity, int total, String manufactureDate, String expiryDate, 
                                      int categoryId, double price, Uri imageUri, ProductCallback callback) {
        try {
            // Prepare form data
            Map<String, String> params = new HashMap<>();
            params.put("name", name);
            params.put("sku", sku);
            params.put("description", description);
            params.put("quantity", String.valueOf(quantity));
            params.put("total", String.valueOf(total));
            params.put("manufacture_date", manufactureDate);
            params.put("expiry_date", expiryDate);
            params.put("category_id", String.valueOf(categoryId));
            params.put("price", String.valueOf(price));
            params.put("_method", "PUT"); // For Laravel to recognize as PUT request

            // Prepare file data
            Map<String, File> files = new HashMap<>();
            if (imageUri != null) {
                try {
                    File imageFile = createTempFileFromUri(imageUri);
                    if (imageFile != null && imageFile.exists()) {
                        files.put("image", imageFile);
                        Log.d("ProductRepository", "Image file created for update: " + imageFile.getAbsolutePath());
                    }
                } catch (Exception e) {
                    Log.e("ProductRepository", "Error creating file from URI for update: " + e.getMessage());
                }
            }

            Log.d("ProductRepository", "Update product with image - params=" + params.toString());

            StringRequest request = ApiClient.multipartRequest(
                    appContext,
                    Request.Method.POST, // Use POST for multipart, Laravel will handle PUT via _method
                    "products/" + uuid,
                    params,
                    files,
                    response -> {
                        Log.d("ProductRepository", "Update product with image response=" + response);
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            Product product = Product.fromJson(jsonResponse);
                            callback.onSuccess(product);
                        } catch (JSONException e) {
                            callback.onError("Failed to parse product response: " + e.getMessage());
                        }
                    },
                    error -> {
                        handleError(error, callback);
                    }
            );

            ApiClient.enqueue(appContext, request);
        } catch (Exception e) {
            callback.onError("Error updating product with image: " + e.getMessage());
        }
    }

    public void deleteProduct(String uuid, ProductCallback callback) {
        Log.d("ProductRepository", "Deleting product with UUID: " + uuid);
        ApiClient.enqueue(appContext, ApiClient.stringRequest(
                appContext,
                Request.Method.DELETE,
                "products/" + uuid,
                null,
                response -> {
                    Log.d("ProductRepository", "Delete product successful, response=" + response);
                    // For delete, we don't need to parse the response (204 No Content)
                    callback.onSuccess(null);
                },
                error -> {
                    Log.e("ProductRepository", "Delete product failed", error);
                    handleError(error, callback);
                }
        ));
    }

    private File createTempFileFromUri(Uri uri) {
        try {
            Log.d("ProductRepository", "Creating temp file from URI: " + uri.toString());
            
            // Create a temporary file
            File tempFile = File.createTempFile("product_image", ".jpg", appContext.getCacheDir());
            Log.d("ProductRepository", "Temp file created: " + tempFile.getAbsolutePath());
            
            // Copy the content from URI to the temporary file
            InputStream inputStream = appContext.getContentResolver().openInputStream(uri);
            if (inputStream != null) {
                Log.d("ProductRepository", "Input stream opened successfully");
                FileOutputStream outputStream = new FileOutputStream(tempFile);
                byte[] buffer = new byte[1024];
                int bytesRead;
                int totalBytes = 0;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytes += bytesRead;
                }
                inputStream.close();
                outputStream.close();
                
                Log.d("ProductRepository", "File copied successfully, size: " + totalBytes + " bytes");
                Log.d("ProductRepository", "Final file size: " + tempFile.length() + " bytes");
                return tempFile;
            } else {
                Log.e("ProductRepository", "Failed to open input stream from URI");
            }
        } catch (Exception e) {
            Log.e("ProductRepository", "Error creating temp file from URI: " + e.getMessage(), e);
        }
        return null;
    }

    private void handleError(VolleyError error, ProductCallback callback) {
        String message = error.getMessage();
        Log.e("ProductRepository", "VolleyError details: " + error.toString());
        
        if (error.networkResponse != null) {
            String body = null;
            try {
                body = new String(error.networkResponse.data);
                Log.e("ProductRepository", "Network response body: " + body);
            } catch (Exception e) {
                Log.e("ProductRepository", "Error reading response body: " + e.getMessage());
            }
            message = "HTTP " + error.networkResponse.statusCode + (body != null ? (": " + body) : "");
        } else {
            Log.e("ProductRepository", "No network response - connection error");
            message = "Connection error: " + (message != null ? message : "Unable to connect to server");
        }
        
        if (message == null) message = "Unknown error";
        Log.e("ProductRepository", "Product request failed: " + message, error);
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