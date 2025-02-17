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

public interface MessageApi {

    @FormUrlEncoded
    @POST("SendMessage.php")
    Call<ApiResponse<Void>> sendMessage(
            @Field("sender_id") int sender_id,
            @Field("receiver_id") int receiver_id,
            @Field("chat_id") int chat_id,
            @Field("message") String message
    );

    @GET("GetMessages.php")
    Call<ApiResponse<List<Message>>> getMessages(
            @Query("sender_id") int sender_id,
            @Query("receiver_id") int receiver_id,
            @Query("product_id") int product_id
    );

    @GET("GetUserChatsBuy.php")
    Call<List<Chat>> getUserChatsBuy(
            @Query("user_id") int user_id
    );

    @GET("GetUserChatsSell.php")
    Call<List<Chat>> getUserChatsSell(
            @Query("user_id") int user_id
    );
}
