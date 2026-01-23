package com.lensonmutugi.distributeddrinksstore_customer.api;

public class OrderItem {
    private String productId;
    private int quantity;
    private double subtotal;

    public OrderItem(String productId, int quantity, double subtotal) {
        this.productId = productId;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }

    // Getters and Setters
    public String getProductId() { return productId; }
    public int getQuantity() { return quantity; }
    public double getSubtotal() { return subtotal; }
}
