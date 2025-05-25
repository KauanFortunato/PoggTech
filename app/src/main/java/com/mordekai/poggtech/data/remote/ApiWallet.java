package com.mordekai.poggtech.data.remote;

import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Payments;
import com.mordekai.poggtech.data.model.Wallet;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiWallet {

    @GET("Wallet/GetWallet.php")
    Call<ApiResponse<Wallet>> getWallet(
            @Query("user_id") int user_id
    );

    @GET("Wallet/GetPayments.php")
    Call<ApiResponse<List<Payments>>> getPayments(
            @Query("user_id") int user_id
    );
}
