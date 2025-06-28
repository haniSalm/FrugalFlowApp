package com.sdaproject.frugalflowapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityDashboard extends AppCompatActivity {

    private static final String TAG = "ActivityDashboard";
    private TextView cashBalanceAmount;
    private DatabaseReference databaseReference;
    private LineChart balanceTrendChart;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        initializeViews();
        setupBalanceTrendChart();
        checkForBalanceData();
        setupClickListeners();
    }

    private void initializeViews() {
        cashBalanceAmount = findViewById(R.id.cash_balance_amount);
        balanceTrendChart = findViewById(R.id.balance_trend_chart);
    }

    private void checkForBalanceData() {
        if (getIntent().hasExtra("CASH_BALANCE")) {
            double balance = getIntent().getDoubleExtra("CASH_BALANCE", 0.0);
            String currency = getIntent().getStringExtra("CURRENCY");
            updateBalanceAndChart(balance, currency);
        } else {
            loadUserDataFromFirebase();
        }
    }

    private void setupClickListeners() {
        ImageButton addTransactionButton = findViewById(R.id.add_transaction_button);
        ImageView notificationButton = findViewById(R.id.notification_button);

        addTransactionButton.setOnClickListener(v -> navigateToActivity(AddTransactionActivity.class));
        notificationButton.setOnClickListener(v -> navigateToActivity(ActivityNotifications.class));

        findViewById(R.id.nav_home).setOnClickListener(v -> navigateToHome());
        findViewById(R.id.nav_settings).setOnClickListener(v -> navigateToActivity(ActivitySettings.class));
        findViewById(R.id.nav_planning).setOnClickListener(v -> navigateToActivity(ActivityPlanning.class));
        findViewById(R.id.nav_forecasting).setOnClickListener(v -> navigateToActivity(ActivityForecasting.class));
        findViewById(R.id.nav_networth).setOnClickListener(v -> navigateToActivity(ActivityNetWorthCalculation.class));
    }

    private void navigateToHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void navigateToActivity(Class<?> targetActivity) {
        startActivity(new Intent(this, targetActivity));
    }

    private void setupBalanceTrendChart() {
        balanceTrendChart.getDescription().setEnabled(false);
        balanceTrendChart.setTouchEnabled(true);
        balanceTrendChart.setDragEnabled(true);
        balanceTrendChart.setScaleEnabled(true);
        balanceTrendChart.setPinchZoom(true);
        balanceTrendChart.setDrawGridBackground(false);

        XAxis xAxis = balanceTrendChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setValueFormatter(new DateValueFormatter());

        YAxis leftAxis = balanceTrendChart.getAxisLeft();
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMinimum(0f);
        balanceTrendChart.getAxisRight().setEnabled(false);
        balanceTrendChart.getLegend().setEnabled(false);
    }

    private class DateValueFormatter extends ValueFormatter {
        private final SimpleDateFormat mFormat = new SimpleDateFormat("MMM dd", Locale.getDefault());

        @Override
        public String getFormattedValue(float value) {
            return mFormat.format(new Date((long) value));
        }
    }

    private void updateBalanceAndChart(double balance, String currencyCode) {
        String currencySymbol = getCurrencySymbol(currencyCode);
        String display = currencySymbol + String.format(Locale.getDefault(), "%.2f", balance);
        cashBalanceAmount.setText(display);
        Log.d(TAG, "Displaying balance: " + display);

        // Create a LineDataSet using all transaction history (with balance changes over time)
        fetchTransactionHistoryAndUpdateChart(balance);
    }

    private String getCurrencySymbol(String code) {
        if (code == null) return "";
        code = code.trim().split(" ")[0].toUpperCase(Locale.ROOT);
        switch (code) {
            case "USD": return "$";
            case "PKR": return "PKR";
            case "SAR": return "SAR";
            default: return code;
        }
    }

    private void fetchTransactionHistoryAndUpdateChart(double initialBalance) {
        String currentUserId = mAuth.getCurrentUser().getUid();

        // Get all transactions for the user
        databaseReference.child(currentUserId).child("transactions")
                .orderByChild("timestamp")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Entry> entries = new ArrayList<>();
                        double balance = initialBalance;
                        long now = System.currentTimeMillis();

                        // Process each transaction and track balance over time
                        for (DataSnapshot transactionSnapshot : dataSnapshot.getChildren()) {
                            double amount = transactionSnapshot.child("amount").getValue(Double.class);
                            long timestamp = transactionSnapshot.child("timestamp").getValue(Long.class);
                            balance += amount;

                            // Add each transaction point to the chart
                            entries.add(new Entry(timestamp, (float) balance));
                        }

                        LineDataSet dataSet = new LineDataSet(entries, "Balance Trend");
                        dataSet.setColor(Color.parseColor("#366F9A"));
                        dataSet.setValueTextColor(Color.BLACK);
                        dataSet.setLineWidth(2f);
                        dataSet.setCircleColors(new int[]{Color.parseColor("#366F9A"), Color.TRANSPARENT});
                        dataSet.setCircleRadius(4f);
                        dataSet.setCircleHoleRadius(3f);
                        dataSet.setDrawValues(false);

                        LineData lineData = new LineData(dataSet);
                        balanceTrendChart.setData(lineData);
                        balanceTrendChart.invalidate();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(ActivityDashboard.this, "Failed to load transactions", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadUserDataFromFirebase() {
        String currentUserId = mAuth.getCurrentUser().getUid();

        databaseReference.child(currentUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double balance = dataSnapshot.child("netCashBalance").getValue(Double.class);
                    String currency = dataSnapshot.child("currency").getValue(String.class);
                    updateBalanceAndChart(balance != null ? balance : 0.0, currency != null ? currency : "PKR");
                } else {
                    updateBalanceAndChart(0.0, "PKR");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ActivityDashboard.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                updateBalanceAndChart(0.0, "PKR");
            }
        });
    }
}
