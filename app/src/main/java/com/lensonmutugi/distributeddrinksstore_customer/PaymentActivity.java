package com.lensonmutugi.distributeddrinksstore_customer;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvTotalAmount;
    private EditText etCardNumber, etExpiry, etCVV;
    private Button btnPayNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);

        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiry = findViewById(R.id.etExpiry);
        etCVV = findViewById(R.id.etCVV);
        btnPayNow = findViewById(R.id.btnPayNow);

        // Get total amount from intent (assuming it was passed)
        String total = getIntent().getStringExtra("TOTAL_AMOUNT");
        if (total != null) {
            tvTotalAmount.setText("Total: " + total);
        }

        btnPayNow.setOnClickListener(v -> {
            if (validateFields()) {
                processPayment();
            }
        });
    }

    private boolean validateFields() {
        if (etCardNumber.getText().toString().isEmpty() ||
            etExpiry.getText().toString().isEmpty() ||
            etCVV.getText().toString().isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void processPayment() {
        // Mock payment processing
        Intent intent = new Intent(this, ReceiptActivity.class);
        intent.putExtra("TOTAL_AMOUNT", tvTotalAmount.getText().toString());
        startActivity(intent);
        finish();
    }
}
