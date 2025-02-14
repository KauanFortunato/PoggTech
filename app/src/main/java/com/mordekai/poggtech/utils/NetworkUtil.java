package com.mordekai.poggtech.utils;

import android.content.Context;
import android.media.browse.MediaBrowser;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mordekai.poggtech.data.callback.ConnectionCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.remote.ApiService;
import com.mordekai.poggtech.data.remote.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkUtil {
    public static boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        }
        return false;
    }

    public static boolean isConnectedXampp(ConnectionCallback callback) {
        ApiService apiService = RetrofitClient.getRetrofitInstance().create(ApiService.class);
        apiService.testConnection().enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    callback.onResult(response.body().isSuccess());
                } else {
                    callback.onResult(false);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onResult(false);
            }
        });
        return true;
    }
}
