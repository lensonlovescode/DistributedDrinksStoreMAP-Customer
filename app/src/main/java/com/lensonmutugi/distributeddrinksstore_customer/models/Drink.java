package com.lensonmutugi.distributeddrinksstore_customer.models;

public class Drink {

    private final String name;
    private int quantity;

    public Drink(String name) {
        this.name = name;
        this.quantity = 0;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void increase() {
        quantity++;
    }

    public void decrease() {
        if (quantity > 0) {
            quantity--;
        }
    }
}
