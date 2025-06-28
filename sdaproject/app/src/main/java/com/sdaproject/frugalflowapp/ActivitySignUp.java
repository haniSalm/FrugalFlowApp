package com.sdaproject.frugalflowapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class ActivitySignUp extends AppCompatActivity {

    private EditText firstNameInput, lastNameInput, emailInput, passwordInput, confirmPasswordInput;
    private ProgressBar progressBar;
    private Button signupButton, backButton;
    private TextView loginRedirect;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI elements
        firstNameInput = findViewById(R.id.multi_line_text_box);
        lastNameInput = findViewById(R.id.last_name);
        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);
        confirmPasswordInput = findViewById(R.id.confirm_password);
        progressBar = findViewById(R.id.progressBar);
        signupButton = findViewById(R.id.signup_button);
        loginRedirect = findViewById(R.id.login_redirect_text);
        backButton = findViewById(R.id.back_button);

        // Back button logic
        backButton.setOnClickListener(v -> finish());

        // Login redirect logic
        loginRedirect.setOnClickListener(v -> {
            startActivity(new Intent(ActivitySignUp.this, ActivitySignIn.class));
            finish();
        });

        // Sign Up button logic
        signupButton.setOnClickListener(v -> {
            String firstName = firstNameInput.getText().toString().trim();
            String lastName = lastNameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String confirmPassword = confirmPasswordInput.getText().toString().trim();

            if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() ||
                    password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(ActivitySignUp.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(ActivitySignUp.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            // Create user with Firebase
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE);
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                String uid = user.getUid();
                                User newUser = new User(firstName, lastName, email);

                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(uid)
                                        .setValue(newUser)
                                        .addOnCompleteListener(dbTask -> {
                                            if (dbTask.isSuccessful()) {
                                                Toast.makeText(ActivitySignUp.this, "Sign up successful!", Toast.LENGTH_SHORT).show();
                                                // Redirect to set currency activity
                                                startActivity(new Intent(ActivitySignUp.this, ActivitySetCurrency.class));
                                                finish();
                                            } else {
                                                Toast.makeText(ActivitySignUp.this, "Failed to save user data", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(ActivitySignUp.this, "Sign up failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    // Simple model class for storing user info
    public static class User {
        public String firstName, lastName, email;

        public User() {
            // Default constructor required for Firebase
        }

        public User(String firstName, String lastName, String email) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.email = email;
        }
    }
}
