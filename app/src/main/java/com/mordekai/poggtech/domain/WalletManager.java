package com.mordekai.poggtech.domain;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Payments;
import com.mordekai.poggtech.data.model.Wallet;
import com.mordekai.poggtech.data.remote.ApiWallet;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WalletManager {

    private final ApiWallet apiWallet;

    public WalletManager(ApiWallet apiWallet) {
        this.apiWallet = apiWallet;
    }

    public void getWallet(int userId, RepositoryCallback<Wallet> callback) {
        apiWallet.getWallet(userId).enqueue(new Callback<ApiResponse<Wallet>>() {

            @Override
            public void onResponse(Call<ApiResponse<Wallet>> call, Response<ApiResponse<Wallet>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    ApiResponse<Wallet> apiResponse = response.body();
                    if(apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onFailure(new Exception("Carteira não encontrada"));
                    }
                } else {
                    callback.onFailure(new Exception("Erro ao buscar carteira"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Wallet>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao buscar carteira"));
            }
        });
    }

    public void getPayments(int userId, RepositoryCallback<List<Payments>> callback) {
        apiWallet.getPayments(userId).enqueue(new Callback<ApiResponse<List<Payments>>>() {

            @Override
            public void onResponse(Call<ApiResponse<List<Payments>>> call, Response<ApiResponse<List<Payments>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Payments>> apiResponse = response.body();

                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onFailure(new Exception("Erro ao buscar pagamentos"));
                    }
                } else {
                    callback.onFailure(new Exception("Erro ao buscar pagamentos"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Payments>>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao buscar pagamentos"));
            }
        });
    }

    public void deposit(int userId, float amount, RepositoryCallback<Void> callback) {
        if (amount <= 0) {
            callback.onFailure(new Exception("Valor inválido"));
            return;
        }
        apiWallet.deposit(userId, amount).enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if (apiResponse.isSuccess()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onFailure(new Exception("Erro ao depositar"));
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao depositar"));
            }
        });
    }
}
