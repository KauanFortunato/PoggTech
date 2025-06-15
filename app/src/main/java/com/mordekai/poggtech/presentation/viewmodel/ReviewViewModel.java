package com.mordekai.poggtech.presentation.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mordekai.poggtech.data.model.Review;
import com.mordekai.poggtech.domain.ReviewManager;
import com.mordekai.poggtech.data.callback.RepositoryCallback;

import java.util.List;

public class ReviewViewModel extends ViewModel {

    private final ReviewManager reviewManager;

    private final MutableLiveData<List<Review>> _reviews = new MutableLiveData<>();
    public LiveData<List<Review>> reviews = _reviews;

    private final MutableLiveData<Boolean> _isLoading = new MutableLiveData<>(false);
    public LiveData<Boolean> isLoading = _isLoading;

    private final MutableLiveData<String> _error = new MutableLiveData<>();
    public LiveData<String> error = _error;

    public ReviewViewModel(ReviewManager reviewManager) {
        this.reviewManager = reviewManager;
    }

    public void loadReviews(int productId, int quantity) {
        _isLoading.setValue(true);
        reviewManager.getReviews(productId, quantity, new RepositoryCallback<List<Review>>() {
            @Override
            public void onSuccess(List<Review> data) {
                _isLoading.postValue(false);
                _reviews.postValue(data);
            }

            @Override
            public void onFailure(Throwable t) {
                _isLoading.postValue(false);
                _error.postValue(t.getMessage());
            }
        });
    }

    public void createReview(Review review) {
        _isLoading.setValue(true);
        reviewManager.createReview(review, new RepositoryCallback<String>() {
            @Override
            public void onSuccess(String data) {
                Log.d("API_RESPONSE", "Review created: " + data);
            }

            @Override
            public void onFailure(Throwable t) {
                _isLoading.postValue(false);
                _error.postValue(t.getMessage());
            }
        });
    }
}
