package com.sdaproject.frugalflowapp;



import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityMainPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);

        initializeUI();
    }

    // SRP: Separate method to initialize UI components
    private void initializeUI() {
        findViewById(R.id.signup_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToSignUp();
            }
        });

        findViewById(R.id.login_prompt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToSignIn();
            }
        });
    }

    // OCP: Extendable navigation methods
    private void navigateToSignUp() {
        Intent intent = new Intent(this, ActivitySignUp.class);
        startActivity(intent);
    }

    private void navigateToSignIn() {
        Intent intent = new Intent(this, ActivitySignIn.class);
        startActivity(intent);
    }
}