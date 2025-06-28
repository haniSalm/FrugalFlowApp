package com.sdaproject.frugalflowapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivitySetCurrency extends AppCompatActivity {

    // Declare UI elements
    private Spinner currencySpinner;
    private Button saveCurrencyButton;

    // Array of currencies
    private final String[] currencies = {"PKR", "SAR", "USD"};
    private String selectedCurrency = ""; // Variable to store the selected currency
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_currency); // Replace with your XML file name

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Please log in first", Toast.LENGTH_SHORT).show();
            return; // Exit if the user is not logged in
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        // Initialize UI elements
        currencySpinner = findViewById(R.id.currency_spinner);
        saveCurrencyButton = findViewById(R.id.save_currency_button);

        // Set up Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, currencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(adapter);

        // Handle Spinner item selection
        currencySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCurrency = currencies[position]; // Save selected currency
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Optionally handle case when no selection is made
            }
        });

        // Handle Save button click
        saveCurrencyButton.setOnClickListener(v -> {
            if (!selectedCurrency.isEmpty()) {
                Toast.makeText(ActivitySetCurrency.this, "Currency Set to: " + selectedCurrency, Toast.LENGTH_SHORT).show();

                // Save currency to Firebase
                saveCurrencyToFirebase(selectedCurrency);

            } else {
                Toast.makeText(ActivitySetCurrency.this, "Please select a currency", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveCurrencyToFirebase(String currency) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Initialize user's cash balance and net cash balance to 0 (you can modify this if needed)
            double cashBalance = 0.0;
            double netCashBalance = cashBalance;  // Net cash balance starts equal to cash balance

            // Create a map to store user data
            DatabaseReference userRef = databaseReference.child(userId);

            // Save currency and netCashBalance to Firebase
            userRef.child("currency").setValue(currency);
            userRef.child("cashBalance").setValue(cashBalance);
            userRef.child("netCashBalance").setValue(netCashBalance) // Save the net cash balance
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(ActivitySetCurrency.this, "Currency and cash balance saved!", Toast.LENGTH_SHORT).show();
                        navigateToNextActivity();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(ActivitySetCurrency.this, "Failed to save currency", Toast.LENGTH_SHORT).show();
                    });
        }
    }

    private void navigateToNextActivity() {
        Intent intent = new Intent(ActivitySetCurrency.this, ActivitySetCashBalance.class); // Go to next activity
        startActivity(intent);
        finish(); // Close the current activity
    }
}
