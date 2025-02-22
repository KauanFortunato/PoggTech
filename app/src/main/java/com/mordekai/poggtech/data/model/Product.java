package com.mordekai.poggtech.data.model;

public class Product {
    private int product_id;
    private int user_id;
    private String title;
    private String description;
    private Float price;
    private Float price_before;
    private Float discount_percentage;
    private String category;
    private String image_url;
    private String created_at;
    private String updated_at;
    public String seller_type;


    // GET and SETTERS
    public int getProduct_id() {
        return product_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Float getPrice() {
        return price;
    }

    public Float getPriceBefore() {
        return price_before;
    }

    public Float getDiscountPercentage() {
        return discount_percentage;
    }


    public String getCategory() {
        return category;
    }

    public String getImage_url() {
        return image_url;
    }

    public String getSeller_type() {
        return seller_type;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }
}
