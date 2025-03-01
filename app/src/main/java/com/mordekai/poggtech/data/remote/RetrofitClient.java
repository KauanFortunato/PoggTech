package com.mordekai.poggtech.data.remote;

import android.util.Log;

import com.mordekai.poggtech.utils.AppConfig;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    private static Retrofit retrofit;

    public static Retrofit getRetrofitInstance() {
        String username = "admin";
        String password = "D7b@IRia";

        Log.d("RetrofitClient", "getRetrofitInstance: " + AppConfig.getBaseUrl());

        if (retrofit == null) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .addInterceptor(new BasicAuthInterceptor(username, password))
                    .build();

            String baseUrl = AppConfig.getBaseUrl();
            if (baseUrl == null) {
                Log.e("RetrofitClient", "O base URL não foi definido.");
                return null;
            }

            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

    public static void resetRetrofit() {
        retrofit = null;
    }
}