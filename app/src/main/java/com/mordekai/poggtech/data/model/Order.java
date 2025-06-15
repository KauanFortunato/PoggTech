package com.mordekai.poggtech.data.model;

import java.io.Serializable;
import java.util.List;

public class Order implements Serializable {
    private int id;
    private double total_amount;
    private String status;
    private String created_at;
    private String created_at_format;
    private List<String> images;
    private int total_products;

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

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }
    public double getTotal_amount() { return total_amount; }

    public String getStatus() { return status; }

    public String getCreated_at() { return created_at; }

    public List<String> getImages() { return images; }

    public int getTotal_products() { return total_products; }

    public String getCreated_at_format() { return created_at_format; }

}
