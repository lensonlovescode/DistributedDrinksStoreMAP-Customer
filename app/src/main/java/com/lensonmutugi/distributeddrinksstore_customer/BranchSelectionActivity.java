package com.lensonmutugi.distributeddrinksstore_customer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BranchSelectionActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "CustomerPrefs";
    public static final String KEY_BRANCH = "selected_branch";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_selection);

        Button btnNairobi = findViewById(R.id.btnNairobi);
        Button btnKisumu = findViewById(R.id.btnKisumu);
        Button btnMombasa = findViewById(R.id.btnMombasa);
        Button btnNakuru = findViewById(R.id.btnNakuru);
        Button btnEldoret = findViewById(R.id.btnEldoret);

        btnNairobi.setOnClickListener(v -> selectBranch("Nairobi"));
        btnKisumu.setOnClickListener(v -> selectBranch("Kisumu"));
        btnMombasa.setOnClickListener(v -> selectBranch("Mombasa"));
        btnNakuru.setOnClickListener(v -> selectBranch("Nakuru"));
        btnEldoret.setOnClickListener(v -> selectBranch("Eldoret"));
    }

    private void selectBranch(String branchName) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        prefs.edit().putString(KEY_BRANCH, branchName).apply();

        Toast.makeText(this, "Branch selected: " + branchName, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, DrinksSelectionActivity.class);
        startActivity(intent);
        finish();
    }
}
