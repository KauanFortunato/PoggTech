package com.mordekai.poggtech.data.remote;

import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Order;
import com.mordekai.poggtech.data.model.OrderItem;
import com.mordekai.poggtech.data.model.RefundRequest;
import com.mordekai.poggtech.data.remote.request.OrderRequest;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ApiOrder {

    @POST("order/register")
    Call<ApiResponse<Integer>>  registerOrder(@Body OrderRequest orderRequest);

    @GET("order/{user_id}")
    Call<ApiResponse<List<Order>>> getOrders(
            @Path("user_id") int user_id
    );

    @GET("order/items/{order_id}")
    Call<ApiResponse<List<OrderItem>>> getOrderItems(
            @Path("order_id") int order_id
    );

    @FormUrlEncoded
    @POST("order/refund")
    Call<ApiResponse<Void>>  setRefundRequest(
            @Field("order_id") int order_id,
            @Field("user_id") int user_id,
            @Field("reason") String reason,
            @Field("status") int status
    );

    @GET("/order/refund/{order_id}")
    Call<ApiResponse<RefundRequest>> getRefundRequest(
            @Path("order_id") int order_id
    );
}
