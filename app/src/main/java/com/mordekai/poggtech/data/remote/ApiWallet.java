package com.mordekai.poggtech.data.remote;

import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Payments;
import com.mordekai.poggtech.data.model.Wallet;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiWallet {

    @GET("wallet/{user_id}")
    Call<ApiResponse<Wallet>> getWallet(
            @Path("user_id") int user_id
    );

    @GET("wallet/{user_id}/payments")
    Call<ApiResponse<List<Payments>>> getPayments(
            @Path("user_id") int user_id
    );

    @FormUrlEncoded
    @POST("wallet/{user_id}/deposit")
    Call<ApiResponse<Void>> deposit(
            @Path("user_id") int user_id,
            @Field("amount") float amount
    );
}
