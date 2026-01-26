package com.lensonmutugi.distributeddrinksstore_customer.api;

public class MpesaRequest {
    private String phone;
    private String drink;
    private int quantity;
    private double total;
    private String branch;

    public MpesaRequest(String phone, String drink, int quantity, double total, String branch) {
        this.phone = phone;
        this.drink = drink;
        this.quantity = quantity;
        this.total = total;
        this.branch = branch;
    }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDrink() { return drink; }
    public void setDrink(String drink) { this.drink = drink; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getBranch() { return branch; }
    public void setBranch(String branch) { this.branch = branch; }
}