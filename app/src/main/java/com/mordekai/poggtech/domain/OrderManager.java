package com.mordekai.poggtech.domain;

import android.util.Log;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.OrderRequest;
import com.mordekai.poggtech.data.model.Wallet;
import com.mordekai.poggtech.data.remote.ApiOrder;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderManager {
    private final ApiOrder apiOrder;

    public OrderManager(ApiOrder apiOrder) {
        this.apiOrder = apiOrder;
    }

    public void RegisterOrder(OrderRequest orderRequest, RepositoryCallback<ApiResponse<Integer>> callback) {
        apiOrder.registerOrder(orderRequest).enqueue(new Callback<ApiResponse<Integer>>() {
            @Override
            public void onResponse(Call<ApiResponse<Integer>> call, Response<ApiResponse<Integer>> response) {

                if (response.isSuccessful() && response.body() != null) {
                    // Tudo certo, resposta correta da API
                    Log.d("API_RESPONSE", "Resposta da API: " + response.body().getMessage());
                    callback.onSuccess(response.body());
                } else {
                    // A resposta não foi bem-sucedida (ex: erro 500, JSON inválido etc.)
                    try {
                        if (response.errorBody() != null) {
                            String error = response.errorBody().string();
                            Log.e("API_ERROR", "Erro bruto da API: " + error);
                            callback.onFailure(new Throwable("Erro: " + error));
                        } else {
                            callback.onFailure(new Throwable("Erro desconhecido na resposta"));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        callback.onFailure(new Throwable("Erro ao ler resposta de erro"));
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Integer>> call, Throwable t) {
                Log.e("API_FAILURE", "Falha na comunicação com a API", t);
                callback.onFailure(t);
            }
        });
    }


}
