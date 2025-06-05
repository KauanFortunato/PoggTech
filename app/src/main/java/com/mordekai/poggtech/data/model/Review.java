package com.mordekai.poggtech.data.model;


public class Review {

    private int review_id;
    private int user_id;
    private String user_name;
    private String user_last_name;
    private String user_avatar;
    private int product_id;
    private int rating;
    private String comment;
    private String created_at;
    private String time_ago;

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

    public String getUser_name() {
        return user_name;
    }

    public String getUser_last_name() {
        return user_last_name;
    }

    public String getUser_avatar() {
        return user_avatar;
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

    public String getTimeAgo() {
        return time_ago;
    }
}







