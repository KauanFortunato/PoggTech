package com.mordekai.poggtech.domain;

import android.util.Log;

import com.mordekai.poggtech.data.callback.RepositoryCallback;
import com.mordekai.poggtech.data.model.ApiResponse;
import com.mordekai.poggtech.data.model.Order;
import com.mordekai.poggtech.data.model.Review;
import com.mordekai.poggtech.data.remote.ApiOrder;
import com.mordekai.poggtech.data.remote.ApiReview;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewManager {

    private final ApiReview apiReview;

    public ReviewManager(ApiReview apiReview) {
        this.apiReview = apiReview;
    }

    public void getReviews(int product_id, int quantity, RepositoryCallback<List<Review>> callback) {
        apiReview.getReviews(product_id, quantity).enqueue(new Callback<ApiResponse<List<Review>>>() {

            @Override
            public void onResponse(Call<ApiResponse<List<Review>>> call, Response<ApiResponse<List<Review>>> response) {
                if(response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    Log.d("API_RESPONSE", "Reviews: " + response.body().getData());
                    List<Review> reviews = response.body().getData();
                    callback.onSuccess(reviews);
                } else {
                    callback.onFailure(new Exception("Erro ao buscar reviews"));
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<List<Review>>> call, Throwable t) {
                callback.onFailure(new Exception("Erro ao buscar reviews"));
            }
        });
    }
}
