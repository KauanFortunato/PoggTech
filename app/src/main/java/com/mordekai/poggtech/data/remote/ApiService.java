package com.mordekai.poggtech.data.remote;

import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Category;
import com.mordekai.poggtech.data.model.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiService {
    // Test Connection
    @GET("TestConnection.php")
    Call<ApiResponse<Void>> testConnection();

    // User
    @FormUrlEncoded
    @POST("RegisterUser.php")
    Call<ResponseBody> insertUser(
            @Field("firebase_uid") String firebase_uid,
            @Field("name") String name,
            @Field("last_name") String last_name,
            @Field("email") String email
    );

    @FormUrlEncoded
    @POST("GetUser.php")
    Call<ApiResponse<User>> getUser(
            @Field("firebase_uid") String firebase_uid
    );

    @PUT("UpdateUser.php")
    Call<ApiResponse<Void>> updateUser(@Body User user);

    @FormUrlEncoded
    @POST("NotificationsFCM/SaveToken.php")
    Call<ApiResponse<Void>> saveToken(
            @Field("user_id") int user_id,
            @Field("token") String token
    );

    @FormUrlEncoded
    @POST("NotificationsFCM/RemoveToken.php")
    Call<ApiResponse<Void>> removeToken(
            @Field("user_id") int user_id,
            @Field("token") String token
    );

    // Category
    @GET("GetAllCategories.php")
    Call<List<Category>> getAllCategories();
}