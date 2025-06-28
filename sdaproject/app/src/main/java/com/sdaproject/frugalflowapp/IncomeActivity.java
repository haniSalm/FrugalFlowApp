package com.sdaproject.frugalflowapp;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class IncomeActivity extends AppCompatActivity {

    private RecyclerView recyclerViewIncomeSources;
    private IncomeSourcesAdapter incomeSourcesAdapter;
    private final List<IncomeSource> incomeSources = new ArrayList<>();
    private TextView tvTotalMonthlyIncome;
    private TextView tvIncomeGrowth;
    private DatabaseReference userRef;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_view);

        recyclerViewIncomeSources = findViewById(R.id.recyclerViewIncomeSources);
        MaterialButton btnAddIncome = findViewById(R.id.btnAddIncome);
        tvTotalMonthlyIncome = findViewById(R.id.tvTotalMonthlyIncome);
        tvIncomeGrowth = findViewById(R.id.tvIncomeGrowth);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        incomeSourcesAdapter = new IncomeSourcesAdapter(incomeSources);
        recyclerViewIncomeSources.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewIncomeSources.setAdapter(incomeSourcesAdapter);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        fetchAndDisplayIncome();
        btnAddIncome.setOnClickListener(v -> showAddIncomeDialog());
    }

    private void fetchAndDisplayIncome() {
        userRef.child("cashBalance").get().addOnSuccessListener(cashSnapshot -> {
            double cashBalance = cashSnapshot.exists() ? cashSnapshot.getValue(Double.class) : 0;
            tvTotalMonthlyIncome.setText(String.format(Locale.getDefault(), "$%.2f", cashBalance));
            storeMonthlyIncome(cashBalance);
            calculateAndDisplayIncomeGrowth();
        });

        userRef.child("income").get().addOnSuccessListener(incomeSnapshot -> {
            incomeSources.clear();
            if (incomeSnapshot.exists()) {
                for (DataSnapshot income : incomeSnapshot.getChildren()) {
                    String name = income.child("name").getValue(String.class);
                    Double amount = income.child("amount").getValue(Double.class);
                    if (name != null && amount != null) {
                        incomeSources.add(new IncomeSource(name, String.format(Locale.getDefault(), "$%.2f", amount), "+0%"));
                    }
                }
            }
            incomeSourcesAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> {
            tvTotalMonthlyIncome.setText("Error");
            e.printStackTrace();
        });
    }

    private void storeMonthlyIncome(double currentIncome) {
        String monthKey = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        userRef.child("monthlyIncomeHistory").child(monthKey).setValue(currentIncome);
    }

    private void calculateAndDisplayIncomeGrowth() {
        DatabaseReference incomeHistoryRef = userRef.child("monthlyIncomeHistory");
        incomeHistoryRef.get().addOnSuccessListener(snapshot -> {
            if (!snapshot.exists()) {
                tvIncomeGrowth.setText("No previous data");
                return;
            }

            String currentMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());

            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.MONTH, -1);
            String lastMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(cal.getTime());

            Double lastMonthIncome = snapshot.child(lastMonth).getValue(Double.class);
            Double currentMonthIncome = snapshot.child(currentMonth).getValue(Double.class);

            if (lastMonthIncome != null && currentMonthIncome != null && lastMonthIncome > 0) {
                double growth = ((currentMonthIncome - lastMonthIncome) / lastMonthIncome) * 100;
                String arrow = growth > 0 ? "📈" : (growth < 0 ? "📉" : "➖");
                tvIncomeGrowth.setText(String.format(Locale.getDefault(), "%s This month: %.2f%%", arrow, growth));
            } else {
                tvIncomeGrowth.setText("➖ This month: 0%");
            }
        });
    }


    private void showAddIncomeDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Add Income Source");

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_income, null);
        dialogBuilder.setView(dialogView);

        TextInputEditText etIncomeName = dialogView.findViewById(R.id.etIncomeName);
        TextInputEditText etIncomeAmount = dialogView.findViewById(R.id.etIncomeAmount);

        dialogBuilder.setPositiveButton("Add", (dialog, which) -> {
            String name = etIncomeName.getText() != null ? etIncomeName.getText().toString().trim() : "";
            String amountStr = etIncomeAmount.getText() != null ? etIncomeAmount.getText().toString().trim() : "";

            if (!name.isEmpty() && !amountStr.isEmpty()) {
                try {
                    double amount = Double.parseDouble(amountStr);
                    addIncomeToFirebase(name, amount);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });

        dialogBuilder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        dialogBuilder.create().show();
    }

    private void addIncomeToFirebase(String name, double amount) {
        DatabaseReference incomeRef = userRef.child("income");
        String incomeId = incomeRef.push().getKey();

        Map<String, Object> incomeData = new HashMap<>();
        incomeData.put("name", name);
        incomeData.put("amount", amount);

        if (incomeId != null) {
            incomeRef.child(incomeId).setValue(incomeData)
                    .addOnSuccessListener(unused -> {
                        updateBalances(amount);
                        addIncomeNotification(name, amount);
                        updateMonthlyIncomeHistory(amount);  // <-- Add this
                        fetchAndDisplayIncome();
                    });
        }
    }
    private void updateMonthlyIncomeHistory(double addedAmount) {
        String monthKey = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        DatabaseReference monthlyIncomeRef = userRef.child("monthlyIncomeHistory").child(monthKey);

        monthlyIncomeRef.get().addOnSuccessListener(snapshot -> {
            double current = snapshot.exists() ? snapshot.getValue(Double.class) : 0;
            monthlyIncomeRef.setValue(current + addedAmount);
        });
    }


    private void updateBalances(double amount) {
        userRef.get().addOnSuccessListener(snapshot -> {
            double currentCash = snapshot.child("cashBalance").exists() ? snapshot.child("cashBalance").getValue(Double.class) : 0;
            double currentNetCash = snapshot.child("netCashBalance").exists() ? snapshot.child("netCashBalance").getValue(Double.class) : 0;

            Map<String, Object> updates = new HashMap<>();
            updates.put("cashBalance", currentCash + amount);
            updates.put("netCashBalance", currentNetCash + amount);

            userRef.updateChildren(updates);
        }).addOnFailureListener(Throwable::printStackTrace);
    }

    private void addIncomeNotification(String name, double amount) {
        DatabaseReference notificationRef = userRef.child("notifications").push();

        Map<String, Object> notifData = new HashMap<>();
        notifData.put("category", "Income");
        notifData.put("description", "Income added under \"" + name + "\"");
        notifData.put("amount", amount);
        notifData.put("remainingBalance", 0);
        notifData.put("timestamp", System.currentTimeMillis());

        notificationRef.setValue(notifData);
    }
}
