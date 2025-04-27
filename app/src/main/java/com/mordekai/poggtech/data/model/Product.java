package com.mordekai.poggtech.data.model;

import java.util.List;
import java.util.Objects;

public class Product {
    private int product_id;
    private int user_id;
    private String title;
    private String description;
    private Float price;
    private Float price_before;
    private Float discount_percentage;
    private String category;
    private String cover;
    private List<String> images;
    private String location;
    private String created_at;
    private String updated_at;
    private String seller_type;
    private int views;
    private int favorite_count;


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

    public String getCover() {
        return cover;
    }

    public List<String> getImages() {
        return images;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public String getSeller_type() {
        return seller_type;
    }

    public boolean isPoggers() {
        return Objects.equals(seller_type, "admin");
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getLocation() { return location; }

    public int getViews() { return views; }

    public int getFavorite_count() { return favorite_count; }
}
