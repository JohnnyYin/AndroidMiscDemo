package com.johnnyyin.retrofitdemo;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;

/**
 * Created by Johnny on 16/4/15.
 */
public class NetUtils {
    public static OkHttpClient okHttpClient;

    static {
        okHttpClient = new OkHttpClient().newBuilder().addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Log.e("SS", "request start, " + chain.request());
                Response response = chain.proceed(chain.request());
                Log.e("SS", "request end, " + response);
                return response;
            }
        }).build();
    }
}
