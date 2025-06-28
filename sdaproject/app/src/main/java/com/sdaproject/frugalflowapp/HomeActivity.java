package com.sdaproject.frugalflowapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d(TAG, "HomeActivity created and displayed");
        setupAllButtons();
        setupBackButton();
    }

    private void setupAllButtons() {
        // View Income Button
        setupButton(R.id.btnViewIncome, IncomeActivity.class, "Income View");

        // Select Category Button
        setupButton(R.id.btnSelectCategory, CategoryExploreActivity.class, "Category Explorer");

        // View Financial Report Button
       // setupButton(R.id.btnViewReport, ActivityFinancialReport.class, "Financial Report");
    }

    private void setupBackButton() {
        Button backButton = findViewById(R.id.back_button);
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                // Navigate back to Dashboard
                Intent intent = new Intent(HomeActivity.this, ActivityDashboard.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }
    }

    private void setupButton(int buttonId, Class<?> targetActivity, String activityName) {
        Button button = findViewById(buttonId);
        if (button != null) {
            button.setOnClickListener(v -> {
                try {
                    Log.d(TAG, "Attempting to open: " + activityName);
                    startActivity(new Intent(HomeActivity.this, targetActivity));
                } catch (Exception e) {
                    Log.e(TAG, "Failed to open " + activityName, e);
                    Toast.makeText(HomeActivity.this,
                            "Could not open " + activityName,
                            Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        // Navigate back to Dashboard when back button is pressed
        Intent intent = new Intent(this, ActivityDashboard.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}