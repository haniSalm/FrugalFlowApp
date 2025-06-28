package com.sdaproject.frugalflowapp;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class AddTransactionActivity extends AppCompatActivity {
    private EditText amountEditText, descriptionEditText, dateEditText;
    private Spinner categorySpinner;
    private Button saveButton;
    private Calendar selectedDate = Calendar.getInstance();
    private DatabaseReference databaseReference;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_transaction);

        View backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        amountEditText = findViewById(R.id.amount_edit_text);
        descriptionEditText = findViewById(R.id.description_edit_text);
        dateEditText = findViewById(R.id.date_edit_text);
        categorySpinner = findViewById(R.id.category_spinner);
        saveButton = findViewById(R.id.save_button);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.transaction_categories,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);

        updateDateText();
        dateEditText.setOnClickListener(v -> showDatePickerDialog());
        saveButton.setOnClickListener(v -> saveTransaction());
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    updateDateText();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateDateText() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        dateEditText.setText(sdf.format(selectedDate.getTime()));
    }

    private void saveTransaction() {
        String amountString = amountEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String category = categorySpinner.getSelectedItem().toString();
        String date = dateEditText.getText().toString();

        if (amountString.isEmpty()) {
            amountEditText.setError("Amount is required");
            return;
        }

        try {
            double amount = Double.parseDouble(amountString);
            if (amount <= 0) {
                amountEditText.setError("Amount must be positive");
                return;
            }

            checkBudgetAndSaveTransaction(amount, category, description, date);
        } catch (NumberFormatException e) {
            amountEditText.setError("Invalid amount");
        }
    }

    private void checkBudgetAndSaveTransaction(double amount, String category, String description, String date) {
        databaseReference.child("budgets").child(category).child("amount")
                .get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Double currentBudget = task.getResult().getValue(Double.class);
                        if (currentBudget != null) {
                            if (amount > currentBudget) {
                                Toast.makeText(this, "Transaction amount exceeds the budget!", Toast.LENGTH_SHORT).show();
                            } else {
                                String transactionId = databaseReference.child("transactions").push().getKey();

                                Map<String, Object> transactionData = new HashMap<>();
                                transactionData.put("amount", amount);
                                transactionData.put("description", description);
                                transactionData.put("category", category);
                                transactionData.put("date", date);
                                transactionData.put("timestamp", System.currentTimeMillis());

                                databaseReference.child("transactions").child(transactionId)
                                        .setValue(transactionData)
                                        .addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                updateUserBalance(amount, category, currentBudget);
                                                updateMonthlyExpenses(amount); // <-- NEW
                                            } else {
                                                Toast.makeText(this, "Failed to save transaction", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(this, "No budget set for this category!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Failed to get current budget", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateMonthlyExpenses(double amount) {
        // Extract year-month key like "2025-05"
        SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        String monthKey = monthFormat.format(selectedDate.getTime());

        DatabaseReference monthlyExpenseRef = databaseReference.child("monthlyExpenseHistory").child(monthKey);
        monthlyExpenseRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Double currentTotal = task.getResult().getValue(Double.class);
                if (currentTotal == null) currentTotal = 0.0;
                monthlyExpenseRef.setValue(currentTotal + amount);
            }
        });
    }

    private void updateUserBalance(double transactionAmount, String category, Double currentBudget) {
        double newBudget = currentBudget - transactionAmount;

        databaseReference.child("budgets").child(category).child("amount")
                .setValue(newBudget)
                .addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        databaseReference.child("netCashBalance").get().addOnCompleteListener(netCashTask -> {
                            if (netCashTask.isSuccessful()) {
                                Double currentNetCash = netCashTask.getResult().getValue(Double.class);
                                if (currentNetCash != null && currentNetCash >= transactionAmount) {
                                    double newNetCash = currentNetCash - transactionAmount;
                                    databaseReference.child("netCashBalance").setValue(newNetCash);

                                    String notificationId = databaseReference.child("notifications").push().getKey();

                                    Map<String, Object> notification = new HashMap<>();
                                    notification.put("category", category);
                                    notification.put("description", descriptionEditText.getText().toString().trim());
                                    notification.put("amount", transactionAmount);
                                    notification.put("remainingBalance", newNetCash);
                                    notification.put("timestamp", System.currentTimeMillis());

                                    databaseReference.child("notifications").child(notificationId).setValue(notification);
                                }
                            }
                        });

                        Toast.makeText(this, "Transaction saved successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Failed to update budget", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
