package com.sdaproject.frugalflowapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class TransferActivity extends AppCompatActivity {
    private Button btnIncome, btnExpense, btnCategory;
    private TextView resultTv;
    private Button btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    private Button btnPlus, btnMinus, btnMultiply, btnEquals, btnDot, btnAC;
    private Button btnHome;

    private double currentValue = 0;
    private double tempValue = 0;
    private char currentOperation = '\0';
    private boolean isNewInput = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        // Initialize UI Elements
        initializeViews();
        setupNavigationButtons();
        setupCalculatorButtons();
    }

    private void initializeViews() {
        // Navigation Buttons
        btnIncome = findViewById(R.id.btnIncome);
        btnExpense = findViewById(R.id.btnExpense);
        btnCategory = findViewById(R.id.btnCategory);
        resultTv = findViewById(R.id.result_tv);

        // Number Buttons
        btn0 = findViewById(R.id.button_0);
        btn1 = findViewById(R.id.button_1);
        btn2 = findViewById(R.id.button_2);
        btn3 = findViewById(R.id.button_3);
        btn4 = findViewById(R.id.button_4);
        btn5 = findViewById(R.id.button_5);
        btn6 = findViewById(R.id.button_6);
        btn7 = findViewById(R.id.button_7);
        btn8 = findViewById(R.id.button_8);
        btn9 = findViewById(R.id.button_9);

        // Operation Buttons
        btnPlus = findViewById(R.id.button_plus);
        btnMinus = findViewById(R.id.button_minus);
        btnMultiply = findViewById(R.id.button_multiply);
        btnEquals = findViewById(R.id.button_equals);
        btnDot = findViewById(R.id.button_ac);
        btnAC = findViewById(R.id.button_ac);
        btnHome = findViewById(R.id.btnHome);

    }

    private void setupNavigationButtons() {
        // Navigate to Home page
        btnHome = findViewById(R.id.btnHome);
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(TransferActivity.this, MainActivityA.class);
            startActivity(intent);
        });

        btnExpense.setOnClickListener(v -> {
            Intent intent = new Intent(TransferActivity.this, MainActivity2.class);
            startActivity(intent);
        });

        btnIncome = findViewById(R.id.btnIncome); // Ensure this ID matches your layout
        btnIncome.setOnClickListener(v -> {
            Intent intent = new Intent(TransferActivity.this, MainActivityA.class);
            startActivity(intent);
        });

        // Optional: Handle category button
        btnCategory.setOnClickListener(v -> {
            Toast.makeText(this, "Select Transfer Account", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupCalculatorButtons() {
        // Number Button Listeners
        Button[] numberButtons = {btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9};
        for (Button btn : numberButtons) {
            btn.setOnClickListener(v -> {
                if (isNewInput) {
                    resultTv.setText("");
                    isNewInput = false;
                }
                String currentText = resultTv.getText().toString();
                resultTv.setText(currentText + btn.getText());
            });
        }

        // Operation Button Listeners
        btnPlus.setOnClickListener(v -> performOperation('+'));
        btnMinus.setOnClickListener(v -> performOperation('-'));
        btnMultiply.setOnClickListener(v -> performOperation('*'));

        // Equals Button
        btnEquals.setOnClickListener(v -> calculateResult());

        // Dot Button for decimal
        btnDot.setOnClickListener(v -> {
            String currentText = resultTv.getText().toString();
            if (!currentText.contains(".")) {
                resultTv.setText(currentText + ".");
            }
        });

        // All Clear Button
        btnAC.setOnClickListener(v -> {
            resultTv.setText("0");
            currentValue = 0;
            tempValue = 0;
            currentOperation = '\0';
            isNewInput = true;
        });
    }

    private void performOperation(char operation) {
        if (!isNewInput) {
            tempValue = Double.parseDouble(resultTv.getText().toString());
            currentOperation = operation;
            isNewInput = true;
        }
    }

    private void calculateResult() {
        if (!isNewInput) {
            double inputValue = Double.parseDouble(resultTv.getText().toString());
            switch (currentOperation) {
                case '+':
                    currentValue = tempValue + inputValue;
                    break;
                case '-':
                    currentValue = tempValue - inputValue;
                    break;
                case '*':
                    currentValue = tempValue * inputValue;
                    break;
                default:
                    currentValue = inputValue;
            }
            resultTv.setText(String.format("%.2f pkr", currentValue));
            isNewInput = true;
        }
    }
}