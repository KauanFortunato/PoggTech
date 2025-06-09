package com.mordekai.poggtech.data.remote;

import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Review;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ApiReview {

    @GET("review/{product_id}/{quantity}")
    Call<ApiResponse<List<Review>>> getReviews(
            @Path("product_id") int product_id,
            @Path("quantity") int quantity
    );
}
