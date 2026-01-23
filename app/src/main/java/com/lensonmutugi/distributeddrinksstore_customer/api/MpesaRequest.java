package com.lensonmutugi.distributeddrinksstore_customer.api;

public class MpesaRequest {
    private String phone;
    private String orderId;

    public MpesaRequest(String phone, String orderId) {
        this.phone = phone;
        this.orderId = orderId;
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
}
