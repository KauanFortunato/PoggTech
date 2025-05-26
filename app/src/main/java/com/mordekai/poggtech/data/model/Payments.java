package com.mordekai.poggtech.data.model;

public class Payments{
    private int id;
    private int user_id;
    private int order_id;
    private double amount;
    private String status;
    private String created_at;
    private String created_at_formatted;

    public int getId() { return id; }
    public int getUserId() { return user_id; }
    public int getOrderId() { return order_id; }
    public double getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return created_at; }
    public String getCreatedAtFormatted() { return created_at_formatted; }
}
