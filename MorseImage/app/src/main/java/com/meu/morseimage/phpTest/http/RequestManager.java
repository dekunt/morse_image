package com.meu.morseimage.phpTest.http;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * 负责管理http请求
 */
public class RequestManager
{
    private static RequestManager mInstance;

    private Context mAppContext;

    private RequestQueue mRequestQueue;

    private RequestManager(Context ctx) {
        this.mAppContext = ctx;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized RequestManager getInstance(Context ctx) {
        if (mInstance == null) {
            mInstance = new RequestManager(ctx);
        }
        return mInstance;
    }

    public synchronized RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mAppContext);
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(String.valueOf(System.currentTimeMillis()));
        getRequestQueue().add(req);
    }
}
