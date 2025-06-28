package com.sdaproject.frugalflowapp;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddBudgetActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_budget);

        // Initialize Firebase Auth and Database
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Spinner categorySpinner = findViewById(R.id.category_spinner);
        EditText budgetAmount = findViewById(R.id.budget_amount);
        Button saveBudgetButton = findViewById(R.id.save_budget_button);
        Button backButton = findViewById(R.id.back_button);

        // Back button goes to previous screen
        backButton.setOnClickListener(v -> finish());

        // Set up spinner with category options
        String[] categories = {
                "Food", "Transportation", "Housing", "Entertainment", "Utilities", "Healthcare", "Clothing", "Other"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        saveBudgetButton.setOnClickListener(v -> {
            String selectedCategory = categorySpinner.getSelectedItem().toString();
            String amount = budgetAmount.getText().toString();

            if (!amount.isEmpty()) {
                double budgetAmountValue = Double.parseDouble(amount);
                // Check if the cash balance is sufficient before adding the budget
                checkCashBalance(budgetAmountValue, selectedCategory);
            } else {
                Toast.makeText(this, "Please enter an amount.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkCashBalance(double budgetAmount, String category) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Fetch the current cash balance
            mDatabase.child("Users").child(userId).child("cashBalance").get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Double currentCashBalance = task.getResult().getValue(Double.class);

                    if (currentCashBalance != null && currentCashBalance >= budgetAmount) {
                        // Enough balance to add the budget
                        double newBalance = currentCashBalance - budgetAmount;

                        // Save the budget to Firebase
                        saveBudgetToFirebase(userId, category, budgetAmount);

                        // Update the cash balance in Firebase
                        updateCashBalance(userId, newBalance);
                    } else {
                        // Not enough balance
                        Toast.makeText(this, "Not enough balance to add this budget.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Failed to fetch current balance.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void saveBudgetToFirebase(String userId, String category, double budgetAmount) {
        // Create a budget entry
        Map<String, Object> budgetEntry = new HashMap<>();
        budgetEntry.put("category", category);
        budgetEntry.put("amount", budgetAmount);

        // Save the budget to Firebase under user's budgets
        mDatabase.child("Users").child(userId).child("budgets").child(category).setValue(budgetEntry)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Budget saved successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to save budget", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateCashBalance(String userId, double newBalance) {
        // Update the cash balance in Firebase
        mDatabase.child("Users").child(userId).child("cashBalance").setValue(newBalance)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Cash balance updated successfully!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to update cash balance", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
