package com.sdaproject.frugalflowapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.*;

public class ActivityFinancialReport extends AppCompatActivity {

    private TextView totalIncomeText, totalExpenseText, netSavingsText, topExpensesText, financialTipText;
    private PieChart pieChart;
    private final String[] financialTips = {
            "Track your expenses daily.",
            "Stick to a monthly budget.",
            "Save before you spend.",
            "Avoid unnecessary subscriptions.",
            "Invest in a diversified portfolio."
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_financial_report);

        totalIncomeText = findViewById(R.id.total_income);
        totalExpenseText = findViewById(R.id.total_expense);
        netSavingsText = findViewById(R.id.net_savings);
        topExpensesText = findViewById(R.id.top_expenses);
        financialTipText = findViewById(R.id.financial_tip);
        pieChart = findViewById(R.id.pieChart);

        showRandomFinancialTip();
        loadFinancialData();
    }

    private void showRandomFinancialTip() {
        Random rand = new Random();
        financialTipText.setText("💡 Tip: " + financialTips[rand.nextInt(financialTips.length)]);
    }

    private void loadFinancialData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        ref.child("transactions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double totalIncome = 0;
                double totalExpense = 0;
                Map<String, Double> categorySums = new HashMap<>();
                List<String> topExpenses = new ArrayList<>();

                for (DataSnapshot snap : snapshot.getChildren()) {
                    String category = snap.child("category").getValue(String.class);
                    String type = snap.child("type").getValue(String.class);
                    Double amountValue = snap.child("amount").getValue(Double.class);

                    if (category == null || type == null || amountValue == null) continue;

                    double amount = amountValue;

                    if (type.equalsIgnoreCase("income")) {
                        totalIncome += amount;
                    } else if (type.equalsIgnoreCase("expense")) {
                        totalExpense += amount;
                        categorySums.put(category, categorySums.getOrDefault(category, 0.0) + amount);
                        topExpenses.add(category + ": ₹" + amount);
                    }
                }


                double netSavings = totalIncome - totalExpense;

                totalIncomeText.setText("Total Income: ₹" + String.format("%.2f", totalIncome));
                totalExpenseText.setText("Total Expenses: ₹" + String.format("%.2f", totalExpense));
                netSavingsText.setText("Net Savings: ₹" + String.format("%.2f", netSavings));

                showCategoryBreakdown(categorySums);
                showTopExpenses(topExpenses);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                totalIncomeText.setText("Failed to load data.");
            }
        });
    }

    private void showCategoryBreakdown(Map<String, Double> categorySums) {
        List<PieEntry> entries = new ArrayList<>();
        for (String category : categorySums.keySet()) {
            entries.add(new PieEntry(categorySums.get(category).floatValue(), category));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Category Breakdown");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData pieData = new PieData(dataSet);

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Expenses");
        pieChart.animateY(1000);
        pieChart.invalidate();
    }

    private void showTopExpenses(List<String> expenses) {
        Collections.sort(expenses, (a, b) -> {
            double valA = Double.parseDouble(a.split("₹")[1]);
            double valB = Double.parseDouble(b.split("₹")[1]);
            return Double.compare(valB, valA);
        });

        StringBuilder sb = new StringBuilder("Top 3 Expenses:\n");
        for (int i = 0; i < Math.min(3, expenses.size()); i++) {
            sb.append("• ").append(expenses.get(i)).append("\n");
        }

        topExpensesText.setText(sb.toString());
    }
}
