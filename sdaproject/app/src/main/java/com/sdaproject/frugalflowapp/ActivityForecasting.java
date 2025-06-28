package com.sdaproject.frugalflowapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.text.SimpleDateFormat;
import java.util.*;

public class ActivityForecasting extends AppCompatActivity {

    private BarChart barChart;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecasting);

        barChart = findViewById(R.id.barChart);
        backButton = findViewById(R.id.back_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fetchDataAndForecast();
    }

    private void fetchDataAndForecast() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Map<String, Double> incomeData = new TreeMap<>();
                Map<String, Double> expenseData = new TreeMap<>();

                if (snapshot.child("monthlyIncomeHistory").exists()) {
                    for (DataSnapshot snap : snapshot.child("monthlyIncomeHistory").getChildren()) {
                        incomeData.put(snap.getKey(), snap.getValue(Double.class));
                    }
                }

                if (snapshot.child("monthlyExpenseHistory").exists()) {
                    for (DataSnapshot snap : snapshot.child("monthlyExpenseHistory").getChildren()) {
                        expenseData.put(snap.getKey(), snap.getValue(Double.class));
                    }
                }

                Map<String, Double> incomeForecast = forecast(incomeData);
                Map<String, Double> expenseForecast = forecast(expenseData);

                plotBarChart(incomeForecast, expenseForecast);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
    }

    private Map<String, Double> forecast(Map<String, Double> data) {
        List<String> sortedKeys = new ArrayList<>(data.keySet());
        Collections.sort(sortedKeys);

        int n = sortedKeys.size();
        if (n == 0) return new HashMap<>();

        double[] x = new double[n];
        double[] y = new double[n];

        for (int i = 0; i < n; i++) {
            x[i] = i + 1;
            y[i] = data.get(sortedKeys.get(i));
        }

        double sumX = 0, sumY = 0, sumXY = 0, sumX2 = 0;
        for (int i = 0; i < n; i++) {
            sumX += x[i];
            sumY += y[i];
            sumXY += x[i] * y[i];
            sumX2 += x[i] * x[i];
        }

        double b = (n * sumXY - sumX * sumY) / (n * sumX2 - sumX * sumX);
        double a = (sumY - b * sumX) / n;

        Map<String, Double> forecast = new LinkedHashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());

        for (int i = n + 1; i <= n + 6; i++) {
            double predictedValue = a + b * i;
            calendar.add(Calendar.MONTH, 1);
            String monthKey = sdf.format(calendar.getTime());
            forecast.put(monthKey, Math.max(predictedValue, 0));
        }

        return forecast;
    }

    private void plotBarChart(Map<String, Double> incomeForecast, Map<String, Double> expenseForecast) {
        List<BarEntry> incomeEntries = new ArrayList<>();
        List<BarEntry> expenseEntries = new ArrayList<>();
        List<BarEntry> savingsEntries = new ArrayList<>();
        List<String> monthLabels = new ArrayList<>();

        int index = 0;
        for (String month : incomeForecast.keySet()) {
            float income = incomeForecast.getOrDefault(month, 0.0).floatValue();
            float expense = expenseForecast.getOrDefault(month, 0.0).floatValue();
            float savings = income - expense;

            incomeEntries.add(new BarEntry(index, income));
            expenseEntries.add(new BarEntry(index, expense));
            savingsEntries.add(new BarEntry(index, savings));

            monthLabels.add(formatMonth(month));
            index++;
        }

        BarDataSet incomeDataSet = new BarDataSet(incomeEntries, "Income");
        incomeDataSet.setColor(getResources().getColor(R.color.blue));

        BarDataSet expenseDataSet = new BarDataSet(expenseEntries, "Expenses");
        expenseDataSet.setColor(getResources().getColor(R.color.red));

        BarDataSet savingsDataSet = new BarDataSet(savingsEntries, "Net Savings");
        savingsDataSet.setColor(getResources().getColor(R.color.green));

        BarData barData = new BarData(incomeDataSet, expenseDataSet, savingsDataSet);
        barData.setBarWidth(0.25f);
        barData.groupBars(0f, 0.3f, 0.05f);

        barChart.setData(barData);
        barChart.setFitBars(true);
        barChart.getDescription().setEnabled(false);

        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(barData.getGroupWidth(0.3f, 0.05f) * monthLabels.size());

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int val = Math.round(value);
                if (val >= 0 && val < monthLabels.size()) {
                    return monthLabels.get(val);
                }
                return "";
            }
        });

        barChart.invalidate(); // Refresh chart
    }

    private String formatMonth(String monthKey) {
        try {
            String[] parts = monthKey.split("-");
            int year = Integer.parseInt(parts[0]);
            int month = Integer.parseInt(parts[1]);

            String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
                    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

            return months[month - 1] + " " + year;
        } catch (Exception e) {
            return monthKey;
        }
    }
}
