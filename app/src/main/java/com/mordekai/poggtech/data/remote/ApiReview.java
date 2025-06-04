package com.mordekai.poggtech.data.remote;

import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Order;
import com.mordekai.poggtech.data.model.Review;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiReview {

    @GET("Review/GetReviews.php")
    Call<ApiResponse<List<Review>>> getReviews(
            @Query("product_id") int product_id
    );
}
