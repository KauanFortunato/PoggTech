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
import retrofit2.http.Query;

public interface ApiMessage {

    @FormUrlEncoded
    @POST("Message/SendMessage.php")
    Call<ApiResponse<Void>> sendMessage(
            @Field("sender_id") int sender_id,
            @Field("receiver_id") int receiver_id,
            @Field("chat_id") int chat_id,
            @Field("message") String message
    );

    @GET("Message/GetMessages.php")
    Call<ApiResponse<List<Message>>> getMessages(
            @Query("product_id") int product_id,
            @Query("chat_id") int chat_id
    );

    @GET("Chat/GetUserChatsBuy.php")
    Call<List<Chat>> getUserChatsBuy(
            @Query("user_id") int user_id
    );

    @GET("Chat/GetUserChatsSell.php")
    Call<List<Chat>> getUserChatsSell(
            @Query("user_id") int user_id
    );

    @GET("Chat/GetChat.php")
    Call<ApiResponse<Chat>> getChat(
            @Query("user_id") int user_id,
            @Query("product_id") int product_id
    );

    @FormUrlEncoded
    @POST("Chat/CreateChat.php")
    Call<ApiResponse<Integer>> createChat(
            @Field("product_id") int product_id
    );

    @GET("Chat/GetUnread.php")
    Call<ApiResponse<Integer>> getUnread(
            @Query("chat_id") int chat_id,
            @Query("user_id") int receiver_id
    );

    @FormUrlEncoded
    @POST
    Call<ApiResponse<Integer>> MarkIsRead(
            @Field("chat_id") int chat_id,
            @Field("user_id") int receiver_id
    );
}
