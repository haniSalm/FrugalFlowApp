package com.sdaproject.frugalflowapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityBudget extends AppCompatActivity {

    private Button backButton;
    private Button addBudgetButton;
    private Button viewBudgetsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);

        // Initialize views
        backButton = findViewById(R.id.back_button);
        addBudgetButton = findViewById(R.id.add_budget_button);
        viewBudgetsButton = findViewById(R.id.view_budgets_button);

        // Set up click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the current activity and return to the previous one
                finish();
            }
        });

        // Set up click listener for the Add Budget button
        addBudgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the Add Budget activity (replace AddBudgetActivity with your target activity)
                Intent intent = new Intent(ActivityBudget.this, AddBudgetActivity.class);
                startActivity(intent);
            }
        });

        // Set up click listener for the View Budgets button
        viewBudgetsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the View Budgets activity (replace ViewBudgetsActivity with your target activity)
                Intent intent = new Intent(ActivityBudget.this, ViewBudgetsActivity.class);
                startActivity(intent);
            }
        });
    }
}
