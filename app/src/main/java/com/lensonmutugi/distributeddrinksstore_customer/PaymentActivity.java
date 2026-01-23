package com.lensonmutugi.distributeddrinksstore_customer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.lensonmutugi.distributeddrinksstore_customer.api.ApiService;
import com.lensonmutugi.distributeddrinksstore_customer.api.MpesaRequest;
import com.lensonmutugi.distributeddrinksstore_customer.api.MpesaResponse;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaymentActivity extends AppCompatActivity {

    private TextView tvTotalAmount, tvMpesaInstruction;
    private EditText etPhoneNumber;
    private Button btnPayNow;
    private ProgressBar progressBar;
    private ApiService apiService;
    
    private final Handler statusHandler = new Handler(Looper.getMainLooper());
    private static final int POLLING_INTERVAL = 3000;
    private int pollingCount = 0;
    private static final int MAX_POLLING_ATTEMPTS = 20;

    // Change this to your server URL (10.0.2.2 is localhost for emulator)
    private static final String BASE_URL = "http://10.0.2.2:5000/"; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment);

        initRetrofit();

        tvTotalAmount = findViewById(R.id.tvTotalAmount);
        tvMpesaInstruction = findViewById(R.id.tvMpesaInstruction);
        etPhoneNumber = findViewById(R.id.etPhoneNumber);
        btnPayNow = findViewById(R.id.btnPayNow);
        progressBar = findViewById(R.id.progressBar);

        String total = getIntent().getStringExtra("TOTAL_AMOUNT");
        if (total != null) {
            tvTotalAmount.setText(total.startsWith("Ksh") ? total : "Total: Ksh " + total);
        }

        btnPayNow.setOnClickListener(v -> {
            if (validateFields()) {
                initiateMpesaPush();
            }
        });
    }

    private void initRetrofit() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(logging).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        apiService = retrofit.create(ApiService.class);
    }

    private boolean validateFields() {
        if (etPhoneNumber.getText().toString().trim().isEmpty()) {
            Toast.makeText(this, "Enter M-Pesa number", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void initiateMpesaPush() {
        String phone = etPhoneNumber.getText().toString().trim();
        // The backend expects orderId. Assuming it was passed from the previous activity.
        String orderId = getIntent().getStringExtra("ORDER_ID"); 
        
        if (orderId == null) {
            // For testing/fallback if ORDER_ID isn't implemented in the previous screen yet
            orderId = "653a1234567890abcdef1234"; 
        }

        toggleLoading(true, "Requesting STK Push...");

        MpesaRequest request = new MpesaRequest(phone, orderId);
        
        apiService.sendStkPush(request).enqueue(new Callback<MpesaResponse>() {
            @Override
            public void onResponse(Call<MpesaResponse> call, Response<MpesaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String checkoutID = response.body().getCheckoutRequestID();
                    if (checkoutID != null) {
                        startPolling(checkoutID);
                    } else {
                        toggleLoading(false, null);
                        Toast.makeText(PaymentActivity.this, "STK Error: " + response.body().getMessage(), Toast.LENGTH_LONG).show();
                    }
                } else {
                    toggleLoading(false, null);
                    Toast.makeText(PaymentActivity.this, "Server error during STK request", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MpesaResponse> call, Throwable t) {
                toggleLoading(false, null);
                Toast.makeText(PaymentActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startPolling(String checkoutID) {
        pollingCount = 0;
        tvMpesaInstruction.setText("Push sent! Enter M-Pesa PIN on your phone...");
        checkPaymentStatus(checkoutID);
    }

    private void checkPaymentStatus(String checkoutID) {
        if (pollingCount >= MAX_POLLING_ATTEMPTS) {
            toggleLoading(false, "Polling timed out.");
            Toast.makeText(this, "Verification timed out. Please check your M-Pesa messages.", Toast.LENGTH_LONG).show();
            return;
        }

        pollingCount++;
        apiService.checkStatus(checkoutID).enqueue(new Callback<MpesaResponse>() {
            @Override
            public void onResponse(Call<MpesaResponse> call, Response<MpesaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String status = response.body().getStatus();
                    
                    if ("completed".equalsIgnoreCase(status)) {
                        navigateToReceipt(response.body());
                    } else if ("failed".equalsIgnoreCase(status)) {
                        toggleLoading(false, "Payment Failed.");
                        Toast.makeText(PaymentActivity.this, "Payment rejected or failed.", Toast.LENGTH_LONG).show();
                    } else {
                        // Still pending, poll again
                        statusHandler.postDelayed(() -> checkPaymentStatus(checkoutID), POLLING_INTERVAL);
                    }
                } else {
                    // Endpoint might return 404 while polling if Redis hasn't updated yet, keep polling
                    statusHandler.postDelayed(() -> checkPaymentStatus(checkoutID), POLLING_INTERVAL);
                }
            }

            @Override
            public void onFailure(Call<MpesaResponse> call, Throwable t) {
                statusHandler.postDelayed(() -> checkPaymentStatus(checkoutID), POLLING_INTERVAL);
            }
        });
    }

    private void toggleLoading(boolean isLoading, String message) {
        btnPayNow.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        if (message != null) tvMpesaInstruction.setText(message);
    }

    private void navigateToReceipt(MpesaResponse response) {
        Intent intent = new Intent(this, ReceiptActivity.class);
        intent.putExtra("TOTAL_AMOUNT", tvTotalAmount.getText().toString());
        intent.putExtra("ORDER_ID", response.getOrderId());
        intent.putExtra("MPESA_RECEIPT", response.getMpesaReceiptNumber());
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        statusHandler.removeCallbacksAndMessages(null);
    }
}
