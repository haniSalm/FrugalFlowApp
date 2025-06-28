package com.sdaproject.frugalflowapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.auth.FirebaseAuth;

public class ActivitySettings extends AppCompatActivity {

    private Switch darkModeSwitch;
    private Button notificationsButton, logoutButton, backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Initialize views
        darkModeSwitch = findViewById(R.id.dark_mode_switch);
        notificationsButton = findViewById(R.id.notifications_button);
        logoutButton = findViewById(R.id.logout_button);
        backButton = findViewById(R.id.back_button);

        // Load dark mode state from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("settings", MODE_PRIVATE);
        boolean darkMode = preferences.getBoolean("dark_mode", false);
        darkModeSwitch.setChecked(darkMode);

        // Apply theme on launch
        AppCompatDelegate.setDefaultNightMode(darkMode ?
                AppCompatDelegate.MODE_NIGHT_YES :
                AppCompatDelegate.MODE_NIGHT_NO);

        // Dark mode switch listener
        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = getSharedPreferences("settings", MODE_PRIVATE).edit();
            editor.putBoolean("dark_mode", isChecked);
            editor.apply();

            AppCompatDelegate.setDefaultNightMode(isChecked ?
                    AppCompatDelegate.MODE_NIGHT_YES :
                    AppCompatDelegate.MODE_NIGHT_NO);
        });

        // Notifications button logic
        notificationsButton.setOnClickListener(v -> {
            Intent intent = new Intent(ActivitySettings.this, ActivityNotifications.class);
            startActivity(intent);
        });

        // Logout button logic with confirmation dialog
        logoutButton.setOnClickListener(v -> {
            new AlertDialog.Builder(ActivitySettings.this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();
                        Toast.makeText(ActivitySettings.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ActivitySettings.this, ActivityMainPage.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        // Back button logic
        backButton.setOnClickListener(v -> onBackPressed());
    }
}
