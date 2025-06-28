package com.sdaproject.frugalflowapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivitySignIn extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText;
    private Button signInButton, backButton;
    private TextView signUpRedirectText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        signInButton = findViewById(R.id.signin_button);
        backButton = findViewById(R.id.back_button);
        signUpRedirectText = findViewById(R.id.signup_redirect_text);

        signInButton.setOnClickListener(v -> handleSignIn());
        backButton.setOnClickListener(v -> finish());
        signUpRedirectText.setOnClickListener(v -> {
            Intent intent = new Intent(ActivitySignIn.this, ActivitySignUp.class);
            startActivity(intent);
        });
    }

    private void handleSignIn() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(email)) {
            showToast("Please enter your Email");
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid Email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            showToast("Please enter your Password");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        showToast("Sign-In Successful!");
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        if (currentUser != null) {
                            checkUserSetup(currentUser.getUid());
                        }
                    } else {
                        showToast("Sign-In Failed: " + task.getException().getMessage());
                    }
                });
    }

    private void checkUserSetup(String userId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult().exists()) {
                DataSnapshot snapshot = task.getResult();
                String currency = snapshot.child("currency").getValue(String.class);
                Double cashBalance = snapshot.child("cashBalance").getValue(Double.class);

                if (currency == null || currency.isEmpty()) {
                    startActivity(new Intent(ActivitySignIn.this, ActivitySetCurrency.class));
                } else if (cashBalance == null || cashBalance <= 0) {
                    startActivity(new Intent(ActivitySignIn.this, ActivitySetCashBalance.class));
                } else {
                    Intent intent = new Intent(ActivitySignIn.this, ActivityDashboard.class);
                    intent.putExtra("CASH_BALANCE", cashBalance);
                    startActivity(intent);
                }
                finish();
            } else {
                showToast("Failed to fetch user setup data.");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(ActivitySignIn.this, message, Toast.LENGTH_SHORT).show();
    }
}
