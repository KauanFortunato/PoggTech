package com.mordekai.poggtech.data.remote.request;

import java.util.List;

public class OrderRequest {
    private final int user_id;
    private final String location;
    private final String user_name;
    private final String user_phone;
    private final List<OrderItem> items;

    public OrderRequest(int user_id, String location, String user_name, String user_phone, List<OrderItem> items) {
        this.user_id = user_id;
        this.location = location;
        this.user_name = user_name;
        this.user_phone = user_phone;
        this.items = items;
    }

    public static class OrderItem {
        private final int product_id;
        private final int quantity;
        private final double unitPrice;

        public OrderItem(int product_id, int quantity, double unitPrice) {
            this.product_id = product_id;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }
    }
}
