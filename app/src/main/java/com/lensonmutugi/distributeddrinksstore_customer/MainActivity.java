package com.lensonmutugi.distributeddrinksstore_customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Show splash / logo UI
        setContentView(R.layout.activity_main);

        // 2. Fetch token
        SharedPreferences prefs = getSharedPreferences("auth", MODE_PRIVATE);
        String token = prefs.getString("token", null);

        // 3. Delay navigation so user sees logo
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            Intent intent;

            if (token == null) {
                // Not logged in
                intent = new Intent(MainActivity.this, LoginActivity.class);
            } else {
                // Logged in
                intent = new Intent(MainActivity.this, BranchSelectionActivity.class);
            }

            startActivity(intent);
            finish();

        }, 1500); // 1.5 seconds splash delay
    }
}
