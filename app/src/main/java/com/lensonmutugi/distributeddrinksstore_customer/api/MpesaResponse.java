package com.lensonmutugi.distributeddrinksstore_customer.api;

import com.google.gson.annotations.SerializedName;

public class MpesaResponse {
    private String status;
    private String message;
    
    @SerializedName("CheckoutRequestID")
    private String checkoutRequestID;
    
    private String orderId;
    private String mpesaReceiptNumber;
    private double amount;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getCheckoutRequestID() { return checkoutRequestID; }
    public String getOrderId() { return orderId; }
    public String getMpesaReceiptNumber() { return mpesaReceiptNumber; }
    public double getAmount() { return amount; }
    
    // Checkers for our logic
    public boolean isSuccess() { 
        // Backend returns status "completed" for success in status check
        // or a CheckoutRequestID in the initial push
        return "completed".equalsIgnoreCase(status) || checkoutRequestID != null; 
    }
}
