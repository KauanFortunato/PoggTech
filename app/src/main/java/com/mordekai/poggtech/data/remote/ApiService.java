package com.mordekai.poggtech.data.remote;

import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.User;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    // Test Connection
    @GET("health")
    Call<ApiResponse<Void>> testConnection();

    // User
    @POST("user")
    Call<ApiResponse<Void>> registerUser(@Body User user);

    @GET("user/{firebase_uid}")
    Call<ApiResponse<User>> getUser(@Path("firebase_uid") String firebaseUid);

    @PUT("user")
    Call<ApiResponse<Void>> updateUser(@Body User user);

    @FormUrlEncoded
    @POST("FCM/save")
    Call<ApiResponse<Void>> saveToken(
            @Field("user_id") int user_id,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("FCM/remove")
    Call<ApiResponse<Void>> removeToken(
            @Field("user_id") int user_id,
            @Field("token") String token
    );

}