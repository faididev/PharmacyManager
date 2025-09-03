package com.example.pharmacymanager.data.entities;

public class OrderItem {
        private long id;
        private long orderId;
        private long productId;
        private int quantity;
        private double price;
        private String createdAt;
        private String updatedAt;

        // Constructors

        public OrderItem() {}

        public OrderItem(long id, long orderId, long productId, int quantity, double price,
                         String createdAt, String updatedAt) {
            this.id = id;
            this.orderId = orderId;
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }

        public OrderItem(long orderId, long productId, int quantity, double price) {
            this.orderId = orderId;
            this.productId = productId;
            this.quantity = quantity;
            this.price = price;
        }

        // Getters and Setters

        public long getId() { return id; }
        public void setId(long id) { this.id = id; }

        public long getOrderId() { return orderId; }
        public void setOrderId(long orderId) { this.orderId = orderId; }

        public long getProductId() { return productId; }
        public void setProductId(long productId) { this.productId = productId; }

        public int getQuantity() { return quantity; }
        public void setQuantity(int quantity) { this.quantity = quantity; }

        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }

        public String getCreatedAt() { return createdAt; }
        public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

        public String getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}


