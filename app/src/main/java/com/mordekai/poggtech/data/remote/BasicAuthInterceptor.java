package com.mordekai.poggtech.data.remote;

import java.io.IOException;
import java.util.Base64;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import android.annotation.SuppressLint;
import android.util.Log;

public class BasicAuthInterceptor implements Interceptor {

    private String credentials;

    @SuppressLint("NewApi")
    public BasicAuthInterceptor(String user, String password) {
        this.credentials = Base64.getEncoder().encodeToString((user + ":" + password).getBytes());
        Log.d("BasicAuthInterceptor", "Authorization: Basic " + credentials);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        Request.Builder requestBuilder = original.newBuilder()
                .header("Authorization", "Basic " + credentials);
        Request request = requestBuilder.build();
        return chain.proceed(request);
    }
}
