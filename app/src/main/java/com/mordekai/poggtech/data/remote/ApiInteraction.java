package com.mordekai.poggtech.data.remote;

import com.mordekai.poggtech.data.model.ApiResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiInteraction {

    @FormUrlEncoded
    @POST("UserInteraction.php")
    Call<ApiResponse<Void>> userInteraction(
            @Field("product_id") int product_id,
            @Field("user_id") int user_id,
            @Field("action") String action
    );

    @FormUrlEncoded
    @POST("AddUserHistory.php")
    Call<ApiResponse<Void>> addUserHistory(
            @Field("product_id") int product_id,
            @Field("user_id") int user_id,
            @Field("action") String action
    );
}
