package com.lensonmutugi.distributeddrinksstore_customer;

import android.util.Log;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
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
        String orderId = getIntent().getStringExtra("ORDER_ID");
        String drinkName = getIntent().getStringExtra("DRINK_NAME");
        int drinkQuantity = getIntent().getIntExtra("DRINK_QUANTITY", 0);
        double orderTotal = getIntent().getDoubleExtra("ORDER_TOTAL", 0.0);
        String branchName = getIntent().getStringExtra("BRANCH_NAME");
        double transactionAmount = getIntent().getDoubleExtra("TRANSACTION_AMOUNT", 0.0);
        String mpesaReceipt = getIntent().getStringExtra("MPESA_RECEIPT");
        String transactionDate = getIntent().getStringExtra("TRANSACTION_DATE");
        
        // Format date if available
        String formattedDate;
        if (transactionDate != null && !transactionDate.equals("N/A")) {
            try {
                SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
                Date date = parser.parse(transactionDate);
                formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(date);
            } catch (ParseException e) {
                Log.e("ReceiptActivity", "Error parsing transaction date: " + transactionDate, e);
                formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
            }
        } else {
            formattedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(new Date());
        }


        StringBuilder receiptBuilder = new StringBuilder();
        receiptBuilder.append("----- Order Details -----\n");
        receiptBuilder.append("Order ID: ").append(orderId != null ? orderId : "N/A").append("\n");
        receiptBuilder.append("Branch: ").append(branchName != null ? branchName : "N/A").append("\n");
        receiptBuilder.append("Item: ").append(drinkName != null ? drinkName : "N/A").append("\n");
        receiptBuilder.append("Quantity: ").append(drinkQuantity > 0 ? drinkQuantity : "N/A").append("\n");
        receiptBuilder.append("Order Total: Ksh ").append(String.format(Locale.getDefault(), "%.2f", orderTotal)).append("\n\n");

        receiptBuilder.append("--- Transaction Details ---\n");
        receiptBuilder.append("Amount Paid: Ksh ").append(String.format(Locale.getDefault(), "%.2f", transactionAmount)).append("\n");
        receiptBuilder.append("M-Pesa Ref: ").append(mpesaReceipt != null ? mpesaReceipt : "N/A").append("\n");
        receiptBuilder.append("Date: ").append(formattedDate);

        tvReceiptDetails.setText(receiptBuilder.toString());

        btnBackHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, BranchSelectionActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
