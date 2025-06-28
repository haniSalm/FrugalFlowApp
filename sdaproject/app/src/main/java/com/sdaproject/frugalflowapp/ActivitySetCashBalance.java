package com.sdaproject.frugalflowapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivitySetCashBalance extends AppCompatActivity {

    private EditText cashBalanceInput;
    private Button finishSetupButton;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_cash_balance);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            return; // Exit if the user is not logged in
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize UI
        cashBalanceInput = findViewById(R.id.cash_balance_input);
        finishSetupButton = findViewById(R.id.save_currency_button);

        finishSetupButton.setOnClickListener(v -> {
            String balanceText = cashBalanceInput.getText().toString();

            if (!balanceText.isEmpty()) {
                try {
                    double balance = Double.parseDouble(balanceText);
                    if (balance < 0) {
                        throw new NumberFormatException();
                    }

                    // Save balance to Firebase
                    saveCashBalanceToFirebase(balance);

                } catch (NumberFormatException e) {
                    Toast.makeText(this, "Please enter a valid positive number!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Please enter a valid balance!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCashBalanceToFirebase(double balance) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = databaseReference.child(userId);

            // Save cash balance and initialize netCashBalance to the same value
            userRef.child("cashBalance").setValue(balance);
            userRef.child("netCashBalance").setValue(balance) // Initial net cash balance is the same
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ActivitySetCashBalance.this, "Cash Balance saved!", Toast.LENGTH_SHORT).show();
                        navigateToDashboard(balance);
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ActivitySetCashBalance.this, "Failed to save balance", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void navigateToDashboard(double balance) {
        Intent intent = new Intent(this, ActivityDashboard.class);
        intent.putExtra("CASH_BALANCE", balance);  // Pass the cash balance to the next activity
        startActivity(intent);
        finish(); // Close this activity so user can't go back
    }
}
