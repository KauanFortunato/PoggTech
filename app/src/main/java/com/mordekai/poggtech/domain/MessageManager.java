package com.mordekai.poggtech.domain;

import android.util.Log;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Chat;
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

    public void sendMessage(int sender_id, int receiver_id, int chat_id, String message, RepositoryCallback<String> callback) {
        messageApi.sendMessage(sender_id, receiver_id, chat_id, message).enqueue(new Callback<ApiResponse<Void>>(){
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
                callback.onFailure(new Exception("Erro ao enviar mensagem: " + t.getMessage()));
            }
        });
    }

    public void fetchMessages(int sender_id, int receiver_id, int product_id, RepositoryCallback<List<Message>> callback) {
        messageApi.getMessages(sender_id, receiver_id, product_id).enqueue(new Callback<ApiResponse<List<Message>>>() {
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

    public void fetchUserChatsBuy(int user_id, RepositoryCallback<List<Chat>> callback) {
        messageApi.getUserChatsBuy(user_id).enqueue(new Callback<List<Chat>>(){

            @Override
            public void onResponse(Call<List<Chat>> call, Response<List<Chat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Chat> chats = response.body();
                    callback.onSuccess(chats);
                } else {
                    callback.onFailure(new Exception("Erro ao buscar chats: Código " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Chat>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao buscar chats: " + t.getMessage()));
            }
        });

    }

    public void fetchUserChatsSell(int user_id, RepositoryCallback<List<Chat>> callback) {
        messageApi.getUserChatsSell(user_id).enqueue(new Callback<List<Chat>>(){

            @Override
            public void onResponse(Call<List<Chat>> call, Response<List<Chat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Chat> chats = response.body();
                    callback.onSuccess(chats);
                } else {
                    callback.onFailure(new Exception("Erro ao buscar chats: Código " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Chat>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao buscar chats: " + t.getMessage()));
            }
        });

    }
}
