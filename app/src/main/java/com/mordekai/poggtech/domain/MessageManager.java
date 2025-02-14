package com.mordekai.poggtech.domain;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Message;
import com.mordekai.poggtech.data.remote.MessageApi;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageManager {
    private final MessageApi messageApi;

    public MessageManager(MessageApi messageApi) {
        this.messageApi = messageApi;
    }

    public void sendMessage(int sender_id, int receiver_id, String message, RepositoryCallback<Void> callback) {
        messageApi.sendMessage(sender_id, receiver_id, message).enqueue(new Callback<ApiResponse<Void>>(){
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                if(response.isSuccessful() && response.body() != null) {
                    ApiResponse<Void> apiResponse = response.body();
                    if(apiResponse.isSuccess()) {
                        callback.onSuccess(null);
                    } else {
                        callback.onFailure(new Exception(apiResponse.getMessage()));
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao enviar mensagem: " + t.getMessage()));
            }
        });
    }

    public void fetchMessages(int sender_id, int receiver_id, RepositoryCallback<List<Message>> callback) {
        messageApi.getMessages(sender_id, receiver_id).enqueue(new Callback<ApiResponse<List<Message>>>() {
            @Override
            public void onResponse(Call<ApiResponse<List<Message>>> call, Response<ApiResponse<List<Message>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse<List<Message>> apiResponse = response.body();

                    if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onFailure(new Exception(apiResponse.getMessage()));
                    }
                } else {
                    callback.onFailure(new Exception("Erro na resposta da API: Código " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Message>>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao buscar mensagens: " + t.getMessage()));
            }
        });
    }
}
