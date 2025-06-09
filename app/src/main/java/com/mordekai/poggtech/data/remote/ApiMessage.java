package com.mordekai.poggtech.data.remote;

import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Chat;
import com.mordekai.poggtech.data.model.Message;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiMessage {

    @FormUrlEncoded
    @POST("messages/send")
    Call<ApiResponse<Void>> sendMessage(
            @Field("sender_id") int sender_id,
            @Field("receiver_id") int receiver_id,
            @Field("chat_id") int chat_id,
            @Field("message") String message
    );

    @GET("messages/{product_id}/{chat_id}")
    Call<ApiResponse<List<Message>>> getMessages(
            @Path("product_id") int product_id,
            @Path("chat_id") int chat_id
    );

    @GET("chat/{user_id}/{product_id}")
    Call<ApiResponse<Chat>> getChat(
            @Path("user_id") int user_id,
            @Path("product_id") int product_id
    );

    @FormUrlEncoded
    @POST("chat/create")
    Call<ApiResponse<Integer>> createChat(
            @Field("product_id") int product_id
    );

    @GET("chat/buying/{user_id}")
    Call<ApiResponse<List<Chat>>> getUserChatsBuy(
            @Path("user_id") int user_id
    );

    @GET("chat/selling/{user_id}")
    Call<ApiResponse<List<Chat>>> getUserChatsSell(
            @Path("user_id") int user_id
    );

    @GET("chat/unread/{chat_id}/{receiver_id}")
    Call<ApiResponse<Integer>> getUnread(
            @Path("chat_id") int chat_id,
            @Path("user_id") int receiver_id
    );

    @FormUrlEncoded
    @POST("chat/read")
    Call<ApiResponse<Void>> markIsRead(
            @Field("chat_id") int chat_id,
            @Field("receiver_id") int receiver_id
    );
}
