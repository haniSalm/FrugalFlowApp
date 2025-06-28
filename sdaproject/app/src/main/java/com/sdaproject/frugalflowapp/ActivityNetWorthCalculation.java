package com.sdaproject.frugalflowapp;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityNetWorthCalculation extends AppCompatActivity {

    private TextView expenseTextView, incomeTextView, expenseAmtTextView, incomeAmtTextView, netWorthTextView;
    private TextView categoryFood, categoryTransportation, categoryHousing, categoryEntertainment;
    private TextView categoryUtilities, categoryHealthcare, categoryClothing, categoryOther;

    private ProgressBar progressBarFood, progressBarTransportation, progressBarHousing, progressBarEntertainment;
    private ProgressBar progressBarUtilities, progressBarHealthcare, progressBarClothing, progressBarOther;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_net_worth_calculation);

        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        expenseTextView = findViewById(R.id.expenseTextView);
        incomeTextView = findViewById(R.id.incomeTextView);
        expenseAmtTextView = findViewById(R.id.expenseAmtTextView);
        incomeAmtTextView = findViewById(R.id.incomeAmtTextView);
        netWorthTextView = findViewById(R.id.netWorthTextView);

        categoryFood = findViewById(R.id.category_food);
        categoryTransportation = findViewById(R.id.category_transportation);
        categoryHousing = findViewById(R.id.category_housing);
        categoryEntertainment = findViewById(R.id.category_entertainment);
        categoryUtilities = findViewById(R.id.category_utilities);
        categoryHealthcare = findViewById(R.id.category_healthcare);
        categoryClothing = findViewById(R.id.category_clothing);
        categoryOther = findViewById(R.id.category_other);

        progressBarFood = findViewById(R.id.progressBarFood);
        progressBarTransportation = findViewById(R.id.progressBarTransportation);
        progressBarHousing = findViewById(R.id.progressBarHousing);
        progressBarEntertainment = findViewById(R.id.progressBarEntertainment);
        progressBarUtilities = findViewById(R.id.progressBarUtilities);
        progressBarHealthcare = findViewById(R.id.progressBarHealthcare);
        progressBarClothing = findViewById(R.id.progressBarClothing);
        progressBarOther = findViewById(R.id.progressBarOther);

        // Set values
        expenseAmtTextView.setText("$0");
        incomeAmtTextView.setText("$50,000");
        netWorthTextView.setText("Net Worth: $0");

        // Animate progress bars (replace with actual values later)
        animateProgressBar(progressBarFood, 40);
        animateProgressBar(progressBarTransportation, 60);
        animateProgressBar(progressBarHousing, 45);
        animateProgressBar(progressBarEntertainment, 10);
        animateProgressBar(progressBarUtilities, 30);
        animateProgressBar(progressBarHealthcare, 0);
        animateProgressBar(progressBarClothing, 0);
        animateProgressBar(progressBarOther, 0);
    }

    private void animateProgressBar(ProgressBar progressBar, int progress) {
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 0, progress);
        progressAnimator.setDuration(1000);
        progressAnimator.start();
    }
}
