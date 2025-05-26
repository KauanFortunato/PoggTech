package com.mordekai.poggtech.data.model;

import java.util.List;

public class OrderRequest {
    private int user_id;
    private List<OrderItem> items;

    public OrderRequest(int user_id, List<OrderItem> items) {
        this.user_id = user_id;
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
