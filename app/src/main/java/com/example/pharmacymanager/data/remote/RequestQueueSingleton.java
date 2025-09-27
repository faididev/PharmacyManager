package com.example.pharmacymanager.data.remote;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class RequestQueueSingleton {
    private static volatile RequestQueueSingleton instance;
    private final RequestQueue requestQueue;

    private RequestQueueSingleton(Context context) {
        requestQueue = Volley.newRequestQueue(context.getApplicationContext());
    }

    public static RequestQueueSingleton getInstance(Context context) {
        if (instance == null) {
            synchronized (RequestQueueSingleton.class) {
                if (instance == null) {
                    instance = new RequestQueueSingleton(context);
                }
            }
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        requestQueue.add(req);
    }
}




