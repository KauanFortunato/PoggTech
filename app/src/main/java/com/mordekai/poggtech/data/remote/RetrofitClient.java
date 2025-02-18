package com.mordekai.poggtech.data.remote;

import android.util.Log;

import com.mordekai.poggtech.utils.AppConfig;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        String username = "admin";
        String password = "D7b@IRia";

        Log.d("RetrofitClient", "getRetrofitInstance: " + AppConfig.getBaseUrl());

        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
            httpClient.addInterceptor(new BasicAuthInterceptor(username, password));

            String baseUrl = AppConfig.getBaseUrl();
            if (baseUrl == null) {
                Log.e("RetrofitClient", "O base URL não foi definido.");
                return null;
            }

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }

    public static void resetRetrofit() {
        retrofit = null;
    }
}