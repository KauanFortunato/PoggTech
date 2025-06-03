package com.mordekai.poggtech.data.remote;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Order;
import com.mordekai.poggtech.data.model.OrderRequest;
import com.mordekai.poggtech.data.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiOrder {

    @POST("Order/RegisterOrder.php")
    Call<ApiResponse<Integer>> registerOrder(@Body OrderRequest orderRequest);

    @GET("Order/GetOrders.php")
    Call<ApiResponse<List<Order>>> getOrders(
            @Query("user_id") int user_id
    );
}
