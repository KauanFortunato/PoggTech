package com.mordekai.poggtech.data.model;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private int id;
    private double total_amount;
    private String status;
    private String shipping_status;
    private String created_at;
    private String created_at_format;
    private List<String> images;
    private int total_products;
    private String location;
    private String user_name;
    private String user_phone;
    private Boolean is_refund_pending;

    public Order() {
    }

    public Order(int id, double total_amount, String status, String created_at, List<String> images) {
        this.id = id;
        this.total_amount = total_amount;
        this.status = status;
        this.created_at = created_at;
        this.images = images;
        this.total_products = images.size();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getTotal_amount() {
        return total_amount;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String userName) {
        this.user_name = userName;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public void setUser_phone(String userPhone) {
        this.user_phone = userPhone;
    }

    public String getStatus() {
        return status;
    }

    public String getShipping_status() {
        return shipping_status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public List<String> getImages() {
        return images;
    }

    public int getTotal_products() {
        return total_products;
    }

    public String getCreated_at_format() {
        return created_at_format;
    }

    public Boolean getIsRefundPending() { return is_refund_pending; }
}
