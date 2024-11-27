package com.mordekai.poggtech.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST("RegisterUsers.php")
    Call<ResponseBody> insertUser(
            @Field("firebase_uid ") String firebase_uid,
            @Field("name") String name,
            @Field("last_name") String last_name,
            @Field("email") String email
    );
}