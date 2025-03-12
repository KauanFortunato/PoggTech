package com.mordekai.poggtech.domain;

import android.util.Log;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.remote.ApiInteraction;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InteractionManager {

    private final ApiInteraction apiInteraction;

    public InteractionManager(ApiInteraction apiInteraction) {
        this.apiInteraction = apiInteraction;
    }

    public void userInteraction(int product_id, int user_id, String action,RepositoryCallback<String> callback) {
        apiInteraction.userInteraction(product_id, user_id, action).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    Log.d("API_RESPONSE", "Interaction response: " + response.body().getMessage());
                    ApiResponse<Void> apiResponse = response.body();
                    if(apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getMessage());
                    } else {
                        callback.onFailure(new Exception(apiResponse.getMessage()));
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao enviar Interaction: " + t.getMessage()));
            }
        });
    }

    public void addUserHistory(int product_id, int user_id, String action,RepositoryCallback<String> callback) {
        apiInteraction.addUserHistory(product_id, user_id, action).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if(apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getMessage());
                    } else {
                        callback.onFailure(new Exception(apiResponse.getMessage()));
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao enviar Interaction: " + t.getMessage()));
            }
        });
    }
}
