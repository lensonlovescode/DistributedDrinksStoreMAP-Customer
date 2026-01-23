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

        // Get data from Intent
        String total = getIntent().getStringExtra("TOTAL_AMOUNT");
        String orderId = getIntent().getStringExtra("ORDER_ID");
        String mpesaReceipt = getIntent().getStringExtra("MPESA_RECEIPT");
        
        String date = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());

        StringBuilder receiptBuilder = new StringBuilder();
        receiptBuilder.append("Order ID: ").append(orderId != null ? orderId : "N/A").append("\n");
        receiptBuilder.append("M-Pesa Ref: ").append(mpesaReceipt != null ? mpesaReceipt : "N/A").append("\n");
        receiptBuilder.append("Total Paid: ").append(total != null ? total : "Ksh 0.00").append("\n");
        receiptBuilder.append("Date: ").append(date);

        tvReceiptDetails.setText(receiptBuilder.toString());

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, BranchSelectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
