package com.sdaproject.frugalflowapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class IncomeView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Find the View Income button
        Button btnViewIncome = findViewById(R.id.btnViewIncome);
        Button btnSelectCategory = findViewById(R.id.btnSelectCategory);
      //  Button btnViewReport = findViewById(R.id.btnViewReport);

        // Set click listener to navigate to Income View
        btnViewIncome.setOnClickListener(v -> {
            Intent intent = new Intent(IncomeView.this, IncomeActivity.class);
            startActivity(intent);
        });

        // Add other button click listeners as needed

        btnSelectCategory.setOnClickListener(v -> {
            // TODO: Implement Category Selection
        });

      //  btnViewReport.setOnClickListener(v -> {
            // TODO: Implement Financial Report
     //   });
    }
}