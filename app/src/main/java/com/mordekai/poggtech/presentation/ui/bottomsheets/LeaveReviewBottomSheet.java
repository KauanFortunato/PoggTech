package com.mordekai.poggtech.presentation.ui.bottomsheets;

import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatRatingBar;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.mordekai.poggtech.R;

public class LeaveReviewBottomSheet extends BottomSheetDialogFragment {

    private int userRating;
    private OnReviewSubmittedListener listener;

    public interface OnReviewSubmittedListener {
        void onReviewSubmitted(int rating, String reviewText);
    }

    public void setOnReviewSubmittedListener(OnReviewSubmittedListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_leave_review, container, false);

        AppCompatButton btnCancel = view.findViewById(R.id.btnCancel);
        AppCompatButton btnConfirm = view.findViewById(R.id.btnConfirm);
        AppCompatRatingBar ratingBar = view.findViewById(R.id.rating_bar);
        AppCompatEditText editTextReview = view.findViewById(R.id.editTextReview);

        btnCancel.setOnClickListener(v -> {
            if(btnCancel.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }
            dismiss();
        });

        btnConfirm.setOnClickListener(v -> {
            if(btnConfirm.isHapticFeedbackEnabled()) {
                v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            String reviewText = editTextReview.getText() != null ? editTextReview.getText().toString().trim() : "";

            if (listener != null) {
                listener.onReviewSubmitted(userRating, reviewText);
            }

            dismiss();
        });

        ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
            if(ratingBar.isHapticFeedbackEnabled()) {
                ratingBar.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE);
            }

            if (fromUser) {
                userRating = (int) rating;
                Log.d("Rating", "Rating: " + userRating);
            }
        });

        return view;
    }
}
