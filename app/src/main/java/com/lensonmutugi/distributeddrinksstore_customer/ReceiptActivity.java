package com.lensonmutugi.distributeddrinksstore_customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReceiptActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt);

        TextView tvReceiptDetails = findViewById(R.id.tvReceiptDetails);
        Button btnBackHome = findViewById(R.id.btnBackHome);

        String total = getIntent().getStringExtra("TOTAL_AMOUNT");
        String orderId = "#" + (int) (Math.random() * 100000);
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        String receiptText = "Order ID: " + orderId + "\n" +
                "Total Paid: " + (total != null ? total : "$0.00") + "\n" +
                "Date: " + date;

        tvReceiptDetails.setText(receiptText);

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, BranchSelectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
