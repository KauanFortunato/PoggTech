package com.mordekai.poggtech.data.model;

import java.util.List;

public class OrderRequest {
    private int user_id;
    private String location;
    private String user_name;
    private String user_phone;
    private List<OrderItem> items;

    public OrderRequest(int user_id, String location, String user_name, String user_phone, List<OrderItem> items) {
        this.user_id = user_id;
        this.location = location;
        this.user_name = user_name;
        this.user_phone = user_phone;
        this.items = items;
    }

    public static class OrderItem {
        private int product_id;
        private int quantity;
        private double unitPrice;

        public OrderItem(int product_id, int quantity, double unitPrice) {
            this.product_id = product_id;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
    }
}
