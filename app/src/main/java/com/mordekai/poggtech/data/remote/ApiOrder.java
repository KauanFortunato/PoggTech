package com.mordekai.poggtech.data.remote;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.OrderRequest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiOrder {

    @POST("Order/RegisterOrder.php")
    Call<ApiResponse<Integer>> registerOrder(@Body OrderRequest orderRequest);
}
