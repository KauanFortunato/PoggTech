package com.mordekai.poggtech.data.remote;

import android.util.Log;

import com.mordekai.poggtech.utils.AppConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;
    public static Retrofit getRetrofitInstance() {
        Log.d("RetrofitClient", "getRetrofitInstance: " + AppConfig.getBaseUrl());
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(AppConfig.getBaseUrl())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static void resetRetrofit() {
        retrofit = null;
    }
}