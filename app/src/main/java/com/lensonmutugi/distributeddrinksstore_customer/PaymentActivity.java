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

import androidx.annotation.NonNull;
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
    private static final String BASE_URL = "https://kasie-nongranular-darwin.ngrok-free.dev/"; 

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

        android.content.SharedPreferences prefs = getSharedPreferences("CustomerPrefs", MODE_PRIVATE);
        String orderJson = prefs.getString("order", null);
        String branchName = prefs.getString("selected_branch", "Unknown");
        String totalAmountStr = prefs.getString("total_amount", "0.0");
        double totalAmount = Double.parseDouble(totalAmountStr);

        if (orderJson == null) {
            Toast.makeText(this, "No order found.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            org.json.JSONArray orderArray = new org.json.JSONArray(orderJson);
            if (orderArray.length() == 0) {
                Toast.makeText(this, "Your cart is empty.", Toast.LENGTH_SHORT).show();
                return;
            }

            // The API expects a single drink, so we'll use the first item.
            org.json.JSONObject firstItem = orderArray.getJSONObject(0);
            String drinkName = firstItem.getString("name");
            int quantity = firstItem.getInt("quantity");

            toggleLoading(true, "Requesting STK Push...");

            MpesaRequest request = new MpesaRequest(phone, drinkName, quantity, totalAmount, branchName);

            apiService.sendStkPush(request).enqueue(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<MpesaResponse> call, @NonNull Response<MpesaResponse> response) {
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
                public void onFailure(@NonNull Call<MpesaResponse> call, @NonNull Throwable t) {
                    toggleLoading(false, null);
                    Toast.makeText(PaymentActivity.this, "Network error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (org.json.JSONException e) {
            android.util.Log.e("PaymentActivity", "Error parsing order JSON", e);
            Toast.makeText(this, "Error processing your order.", Toast.LENGTH_SHORT).show();
        }
    }

    private void startPolling(String checkoutID) {
        pollingCount = 0;
        tvMpesaInstruction.setText(R.string.mpesa_push_instruction);
        checkPaymentStatus(checkoutID);
    }

    private void checkPaymentStatus(String checkoutID) {
        if (pollingCount >= MAX_POLLING_ATTEMPTS) {
            toggleLoading(false, "Polling timed out.");
            Toast.makeText(this, "Verification timed out. Please check your M-Pesa messages.", Toast.LENGTH_LONG).show();
            return;
        }

        pollingCount++;
        apiService.checkStatus(checkoutID).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<MpesaResponse> call, @NonNull Response<MpesaResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String status = response.body().getStatus();
                    
                    if ("success".equalsIgnoreCase(status)) {
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
            public void onFailure(@NonNull Call<MpesaResponse> call, @NonNull Throwable t) {
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
        
        // Pass Order Details
        if (response.getOrder() != null) {
            intent.putExtra("ORDER_ID", response.getOrder().getOrderID());
            intent.putExtra("DRINK_NAME", response.getOrder().getDrink());
            intent.putExtra("DRINK_QUANTITY", response.getOrder().getQuantity());
            intent.putExtra("ORDER_TOTAL", response.getOrder().getTotal());
            intent.putExtra("BRANCH_NAME", response.getOrder().getBranch());
        } else {
            intent.putExtra("ORDER_ID", "N/A");
            intent.putExtra("DRINK_NAME", "N/A");
            intent.putExtra("DRINK_QUANTITY", 0);
            intent.putExtra("ORDER_TOTAL", 0.0);
            intent.putExtra("BRANCH_NAME", "N/A");
        }

        // Pass Transaction Details
        if (response.getTransaction() != null) {
            intent.putExtra("TRANSACTION_AMOUNT", response.getTransaction().getAmount());
            intent.putExtra("MPESA_RECEIPT", response.getTransaction().getMpesaCode());
            intent.putExtra("TRANSACTION_DATE", response.getTransaction().getTransactionDate());
        } else {
            intent.putExtra("TRANSACTION_AMOUNT", 0.0);
            intent.putExtra("MPESA_RECEIPT", "N/A");
            intent.putExtra("TRANSACTION_DATE", "N/A");
        }

                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();        }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        statusHandler.removeCallbacksAndMessages(null);
    }
}
