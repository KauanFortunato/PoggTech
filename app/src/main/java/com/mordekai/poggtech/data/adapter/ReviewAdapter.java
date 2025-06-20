package com.mordekai.poggtech.data.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mordekai.poggtech.R;
import com.mordekai.poggtech.data.model.Product;
import com.mordekai.poggtech.data.model.Review;
import com.mordekai.poggtech.utils.Utils;

import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ViewHolder> {

    private final List<Review> reviews;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_review, parent, false);

        return new ReviewAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewAdapter.ViewHolder holder, int position) {
        Review review = reviews.get(position);
        holder.bind(review, holder.itemView);
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public void updateReviews(List<Review> newReviews) {
        reviews.clear();
        reviews.addAll(newReviews);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView comment;
        private final TextView userName;
        private final TextView commentDate;
        private final LinearLayout starsContainer;
        private final AppCompatImageView userAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            comment = itemView.findViewById(R.id.comment);
            starsContainer = itemView.findViewById(R.id.starsContainer);
            userName = itemView.findViewById(R.id.userName);
            commentDate = itemView.findViewById(R.id.commentDate);
            userAvatar = itemView.findViewById(R.id.userAvatar);
        }

        public void bind(Review review, View view) {
            comment.setText(review.getComment());
            setRatingStars(starsContainer, review.getRating(), view);
            userName.setText(String.format("%s", review.getUser_name()));
            commentDate.setText(review.getTimeAgo());

            Utils.loadImageBasicAuth(userAvatar, review.getUser_avatar());
        }

        private void setRatingStars(LinearLayout container, float rating, View view) {
            container.removeAllViews();

            int filledStars = (int) rating;
            boolean hasHalfStar = (rating - filledStars) >= 0.25f && (rating - filledStars) < 0.75f;
            int totalStars = 5;

            for (int i = 0; i < totalStars; i++) {
                AppCompatImageView star = new AppCompatImageView(container.getContext());

                if (i < filledStars) {
                    star.setImageResource(R.drawable.ic_star_filled);
                } else if (i == filledStars && hasHalfStar) {
                    star.setImageResource(R.drawable.ic_star_half);
                } else {
                    star.setImageResource(R.drawable.ic_star);
                }

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        (int) view.getContext().getResources().getDisplayMetrics().density * 15,
                        (int) view.getContext().getResources().getDisplayMetrics().density * 15
                );
                params.setMargins(0, 0, 0, 0);
                star.setLayoutParams(params);

                container.addView(star);
            }
        }
    }
}
