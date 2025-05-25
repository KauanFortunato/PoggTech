package com.mordekai.poggtech.data.model;

public class Payments{
    private int id;
    private int user_id;
    private int product_id;
    private double amount;
    private String status;
    private String created_at;

    public int getId() { return id; }
    public int getUserId() { return user_id; }
    public int getProductId() { return product_id; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return created_at; }
}
