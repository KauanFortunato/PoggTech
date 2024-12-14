package com.mordekai.poggtech.data.remote;

import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.User;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiService {
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
    Call<User> getUser(
            @Field("firebase_uid") String firebase_uid
    );

    @PUT("UpdateUser.php")
    Call<ApiResponse> updateUser(@Body User user);
}