package com.lensonmutugi.distributeddrinksstore_customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput, passwordInput;
    private Button loginBtn;
    private ProgressBar loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Bind UI elements
        emailInput = findViewById(R.id.inputEmail);
        passwordInput = findViewById(R.id.inputPassword);
        loginBtn = findViewById(R.id.btnLogin);
        loadingBar = findViewById(R.id.loadingBar);

        loginBtn.setOnClickListener(v -> attemptLogin());
    }

    private void attemptLogin() {
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        // 2. Basic validation
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        loadingBar.setVisibility(View.VISIBLE);
        loginBtn.setEnabled(false);

        // 3. Prepare request
        String url = "https://kasie-nongranular-darwin.ngrok-free.dev/login";

        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                body,
                response -> {
                    loadingBar.setVisibility(View.GONE);
                    loginBtn.setEnabled(true);

                    try {
                        // 4. Extract token
                        String token = response.getString("token");

                        // 5. Save token
                        SharedPreferences prefs =
                                getSharedPreferences("auth", MODE_PRIVATE);
                        prefs.edit().putString("token", token).apply();

                        // 6. Navigate to BranchSelection (clear back stack)
                        Intent intent = new Intent(
                                LoginActivity.this,
                                BranchSelectionActivity.class
                        );
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);

                    } catch (JSONException e) {
                        Toast.makeText(this,
                                "Invalid server response",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    loadingBar.setVisibility(View.GONE);
                    loginBtn.setEnabled(true);
                    Toast.makeText(this,
                            "Login failed. Check credentials.",
                            Toast.LENGTH_SHORT).show();
                }
        );

        // 7. Send request
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}
