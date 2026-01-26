package com.lensonmutugi.distributeddrinksstore_customer.api;

import com.google.gson.annotations.SerializedName;

public class MpesaResponse {
    private String status;
    private String message;
    
    @SerializedName("CheckoutRequestID")
    private String checkoutRequestID;

    private OrderDetails order;
    private TransactionDetails transaction;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getCheckoutRequestID() { return checkoutRequestID; }

    public OrderDetails getOrder() { return order; }
    public TransactionDetails getTransaction() { return transaction; }
    
    // Checkers for our logic
    public boolean isSuccess() { 
        return "success".equalsIgnoreCase(status) || checkoutRequestID != null; 
    }

    public static class OrderDetails {
        @SerializedName("orderID")
        private String orderID;
        private String drink;
        private int quantity;
        private double total;
        private String branch;
        private String paid;

        public String getOrderID() { return orderID; }
        public String getDrink() { return drink; }
        public int getQuantity() { return quantity; }
        public double getTotal() { return total; }
        public String getBranch() { return branch; }
        public String getPaid() { return paid; }
    }

    public static class TransactionDetails {
        private double amount;
        @SerializedName("mpesaCode")
        private String mpesaCode;
        private String transactionDate;
        private String phone;

        public double getAmount() { return amount; }
        public String getMpesaCode() { return mpesaCode; }
        public String getTransactionDate() { return transactionDate; }
        public String getPhone() { return phone; }
    }
}