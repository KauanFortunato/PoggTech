package com.mordekai.poggtech.data.model;

public class OrderItem {
    private int order_id;
    private int user_id;
    private String status;
    private String created_at;
    private int product_id;
    private int quantity;
    private Float unit_price;
    private Float total_item_value;
    private String product_title;
    private String product_cover;
    private String product_category;

    public OrderItem() {
    }

    public OrderItem(int order_id, String product_title, String product_category, Float unit_price, String product_cover, int product_id) {
        this.order_id = order_id;
        this.product_title = product_title;
        this.product_category = product_category;
        this.unit_price = unit_price;
        this.product_cover = product_cover;
        this.product_id = product_id;
    }

    // Getters e Setters
    public int getOrder_id() {
        return order_id;
    }

    public void setOrder_id(int order_id) {
        this.order_id = order_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public int getProduct_id() {
        return product_id;
    }

    public void setProduct_id(int product_id) {
        this.product_id = product_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Float getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(Float unit_price) {
        this.unit_price = unit_price;
    }

    public Float getTotal_item_value() {
        return total_item_value;
    }

    public void setTotal_item_value(Float total_item_value) {
        this.total_item_value = total_item_value;
    }

    public String getProduct_title() {
        return product_title;
    }

    public void setProduct_title(String product_title) {
        this.product_title = product_title;
    }

    public String getProduct_cover() {
        return product_cover;
    }

    public void setProduct_cover(String product_cover) {
        this.product_cover = product_cover;
    }

    public String getProduct_category() {
        return product_category;
    }

    public void setProduct_category(String product_category) {
        this.product_category = product_category;
    }
}
