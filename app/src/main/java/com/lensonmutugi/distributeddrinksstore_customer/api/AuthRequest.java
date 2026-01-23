package com.lensonmutugi.distributeddrinksstore_customer.api;

public class AuthRequest {
    private String name;
    private String email;
    private String password;
    private String phone;

    // For Login
    public AuthRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    // For Signup
    public AuthRequest(String name, String email, String password, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }
}
