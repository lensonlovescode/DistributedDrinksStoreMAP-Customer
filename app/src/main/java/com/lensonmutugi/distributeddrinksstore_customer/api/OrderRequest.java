package com.lensonmutugi.distributeddrinksstore_customer.api;

import java.util.List;

public class OrderRequest {
    private String customerId;
    private String branchId;
    private List<OrderItem> items;
    private String paymentMethod;

    public OrderRequest(String customerId, String branchId, List<OrderItem> items, String paymentMethod) {
        this.customerId = customerId;
        this.branchId = branchId;
        this.items = items;
        this.paymentMethod = paymentMethod;
    }

    // For Cash orders
    public OrderRequest(String customerId, String branchId, List<OrderItem> items) {
        this.customerId = customerId;
        this.branchId = branchId;
        this.items = items;
    }

    public String getCustomerId() { return customerId; }
    public String getBranchId() { return branchId; }
    public List<OrderItem> getItems() { return items; }
    public String getPaymentMethod() { return paymentMethod; }
}
