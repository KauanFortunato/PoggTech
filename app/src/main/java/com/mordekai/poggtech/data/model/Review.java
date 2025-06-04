package com.mordekai.poggtech.data.model;


public class Review {

    private int review_id;
    private int user_id;
    private int product_id;
    private int rating;
    private String comment;
    private String created_at;

    public Review(int review_id, int user_id, int product_id, int rating, String comment, String created_at) {
        this.review_id = review_id;
        this.user_id = user_id;
        this.product_id = product_id;
        this.rating = rating;
        this.comment = comment;
    }

    public int getReview_id() {
        return review_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public int getProduct_id() {
        return product_id;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getCreated_at() {
        return created_at;
    }
}







