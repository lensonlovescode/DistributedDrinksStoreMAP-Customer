package com.lensonmutugi.distributeddrinksstore_customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lensonmutugi.distributeddrinksstore_customer.adapters.DrinkAdapter;
import com.lensonmutugi.distributeddrinksstore_customer.models.Drink;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.List;

public class DrinksSelectionActivity extends AppCompatActivity {

    private List<Drink> drinks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drinks_selection);

        SharedPreferences prefs = getSharedPreferences("CustomerPrefs", MODE_PRIVATE);
        String branch = prefs.getString("selected_branch", "Unknown Branch");

        TextView txtBranch = findViewById(R.id.txtBranch);
        txtBranch.setText("Buying from: " + branch);

        drinks = Arrays.asList(
                new Drink("Coke"),
                new Drink("Fanta"),
                new Drink("Sprite")
        );

        RecyclerView recyclerView = findViewById(R.id.drinksRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new DrinkAdapter(drinks));

        findViewById(R.id.btnProceed).setOnClickListener(v -> saveOrderAndContinue());
    }

    private void saveOrderAndContinue() {
        JSONArray orderArray = new JSONArray();
        double totalAmount = 0;
        final double pricePerDrink = 50.0;

        try {
            for (Drink drink : drinks) {
                if (drink.getQuantity() > 0) {
                    JSONObject obj = new JSONObject();
                    obj.put("name", drink.getName());
                    obj.put("quantity", drink.getQuantity());
                    orderArray.put(obj);
                    totalAmount += drink.getQuantity() * pricePerDrink;
                }
            }

            SharedPreferences prefs = getSharedPreferences("CustomerPrefs", MODE_PRIVATE);
            prefs.edit()
                    .putString("order", orderArray.toString())
                    .putString("total_amount", String.valueOf(totalAmount))
                    .apply();

            startActivity(new Intent(this, SelectPaymentMethodActivity.class));

        } catch (Exception e) {
            android.util.Log.e("DrinksSelectionActivity", "Error saving order", e);
        }
    }
}
