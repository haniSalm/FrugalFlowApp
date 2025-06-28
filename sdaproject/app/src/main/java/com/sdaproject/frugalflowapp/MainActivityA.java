package com.sdaproject.frugalflowapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class MainActivityA extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView resultTextView;
    private MaterialButton btn0, btn1, btn2, btn3, btn4, btn5, btn6, btn7, btn8, btn9;
    private MaterialButton btnPlus, btnMinus, btnMultiply, btnEquals, btnDot, btnAC;
    private Button btnIncome, btnExpense, btnTransfer;
    private Button btnHome;

    private double firstNumber = 0;
    private String currentOperation = "";
    private boolean isNewOperation = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            initializeComponents();
            setupNumberButtonListeners();
            setupOperationButtonListeners();
            setupNavigationButtonListeners();
            Log.d(TAG, "MainActivity Launched");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing MainActivity", e);
            Toast.makeText(this, "Error setting up calculator: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void initializeComponents() {
        resultTextView = findViewById(R.id.result_tv);

        // Null checks for all buttons
        btn0 = safelyFindMaterialButton(R.id.button_0);
        btn1 = safelyFindMaterialButton(R.id.button_1);
        btn2 = safelyFindMaterialButton(R.id.button_2);
        btn3 = safelyFindMaterialButton(R.id.button_3);
        btn4 = safelyFindMaterialButton(R.id.button_4);
        btn5 = safelyFindMaterialButton(R.id.button_5);
        btn6 = safelyFindMaterialButton(R.id.button_6);
        btn7 = safelyFindMaterialButton(R.id.button_7);
        btn8 = safelyFindMaterialButton(R.id.button_8);
        btn9 = safelyFindMaterialButton(R.id.button_9);

        btnPlus = safelyFindMaterialButton(R.id.button_plus);
        btnMinus = safelyFindMaterialButton(R.id.button_minus);
        btnMultiply = safelyFindMaterialButton(R.id.button_multiply);
        btnEquals = safelyFindMaterialButton(R.id.button_equals);
        btnDot = safelyFindMaterialButton(R.id.button_ac);
        btnAC = safelyFindMaterialButton(R.id.button_ac);

        btnIncome = safelyFindButton(R.id.btnIncome);
        btnExpense = safelyFindButton(R.id.btnExpense);
        btnTransfer = safelyFindButton(R.id.btnTransfer);
        btnHome = safelyFindButton(R.id.btnHome);
    }

    // Helper method to safely find MaterialButton with logging
    private MaterialButton safelyFindMaterialButton(int resourceId) {
        MaterialButton button = findViewById(resourceId);
        if (button == null) {
            Log.e(TAG, "MaterialButton with ID " + resourceId + " not found");
        }
        return button;
    }

    // Helper method to safely find Button with logging
    private Button safelyFindButton(int resourceId) {
        Button button = findViewById(resourceId);
        if (button == null) {
            Log.e(TAG, "Button with ID " + resourceId + " not found");
        }
        return button;
    }

    private void setupNumberButtonListeners() {
        View.OnClickListener numberListener = v -> {
            if (v instanceof MaterialButton) {
                MaterialButton button = (MaterialButton) v;
                String buttonText = button.getText().toString();

                if (isNewOperation) {
                    resultTextView.setText("");
                    isNewOperation = false;
                }
                resultTextView.append(buttonText);
            }
        };

        // Null checks before setting listeners
        safeSetNumberListener(btn0, numberListener);
        safeSetNumberListener(btn1, numberListener);
        safeSetNumberListener(btn2, numberListener);
        safeSetNumberListener(btn3, numberListener);
        safeSetNumberListener(btn4, numberListener);
        safeSetNumberListener(btn5, numberListener);
        safeSetNumberListener(btn6, numberListener);
        safeSetNumberListener(btn7, numberListener);
        safeSetNumberListener(btn8, numberListener);
        safeSetNumberListener(btn9, numberListener);
        safeSetNumberListener(btnDot, numberListener);
    }

    // Helper method to safely set number listeners
    private void safeSetNumberListener(MaterialButton button, View.OnClickListener listener) {
        if (button != null) {
            button.setOnClickListener(listener);
        } else {
            Log.w(TAG, "Attempted to set listener on null button");
        }
    }

    private void setupOperationButtonListeners() {
        // Null checks for operation buttons
        safeSetOperationListener(btnPlus, v -> performOperation("+"));
        safeSetOperationListener(btnMinus, v -> performOperation("-"));
        safeSetOperationListener(btnMultiply, v -> performOperation("*"));
        safeSetOperationListener(btnEquals, v -> calculateResult());
        safeSetOperationListener(btnAC, v -> {
            resultTextView.setText("0");
            firstNumber = 0;
            currentOperation = "";
            isNewOperation = true;
        });
    }

    // Helper method to safely set operation listeners
    private void safeSetOperationListener(MaterialButton button, View.OnClickListener listener) {
        if (button != null) {
            button.setOnClickListener(listener);
        } else {
            Log.w(TAG, "Attempted to set listener on null operation button");
        }
    }

    private void setupNavigationButtonListeners() {
        // Null checks for navigation buttons
        safeSetNavigationListener(btnHome, v -> {
            Intent intent = new Intent(MainActivityA.this, HomeActivity.class);
            startActivity(intent);
            finish();
        });

        safeSetNavigationListener(btnIncome, v -> {
            Intent intent = new Intent(MainActivityA.this, MainActivityA.class);
            startActivity(intent);
        });

        safeSetNavigationListener(btnExpense, v -> {
            Intent intent = new Intent(MainActivityA.this, MainActivity2.class);
            startActivity(intent);
        });

        safeSetNavigationListener(btnTransfer, v -> {
            Intent intent = new Intent(MainActivityA.this, TransferActivity.class);
            startActivity(intent);
        });
    }

    // Helper method to safely set navigation listeners
    private void safeSetNavigationListener(Button button, View.OnClickListener listener) {
        if (button != null) {
            button.setOnClickListener(listener);
        } else {
            Log.w(TAG, "Attempted to set listener on null navigation button");
        }
    }

    private void performOperation(String operation) {
        try {
            firstNumber = Double.parseDouble(resultTextView.getText().toString());
            currentOperation = operation;
            isNewOperation = true;
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error parsing number for operation", e);
            Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show();
        }
    }

    private void calculateResult() {
        try {
            double secondNumber = Double.parseDouble(resultTextView.getText().toString());
            double result = 0;

            if ("+".equals(currentOperation)) result = firstNumber + secondNumber;
            else if ("-".equals(currentOperation)) result = firstNumber - secondNumber;
            else if ("*".equals(currentOperation)) result = firstNumber * secondNumber;

            resultTextView.setText(String.valueOf(result));
            firstNumber = result;
            isNewOperation = true;
            currentOperation = "";
        } catch (NumberFormatException e) {
            Log.e(TAG, "Error calculating result", e);
            Toast.makeText(this, "Calculation error", Toast.LENGTH_SHORT).show();
        }
    }
}