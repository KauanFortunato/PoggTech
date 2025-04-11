package com.mordekai.poggtech.domain;

import android.util.Log;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.remote.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FCMManager {
    private final ApiService apiService;

    public FCMManager(ApiService apiService) {
        this.apiService = apiService;
    }

    public void saveToken(int user_id, String token, RepositoryCallback<ApiResponse> callback) {
        apiService.saveToken(user_id, token).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    callback.onSuccess(apiResponse);
                } else {
                    callback.onFailure(new Throwable("Erro ao enviar token"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onFailure(new Throwable("Erro: " + t.getMessage()));
            }
        });
    }

    public void removeToken(int user_id, String token, RepositoryCallback<ApiResponse> callback) {
        apiService.removeToken(user_id, token).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    callback.onSuccess(apiResponse);
                } else {
                    callback.onFailure(new Throwable("Erro ao enviar token"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onFailure(new Throwable("Erro: " + t.getMessage()));
            }
        });
    }
}
