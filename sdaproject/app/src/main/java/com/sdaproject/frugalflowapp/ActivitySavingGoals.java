package com.sdaproject.frugalflowapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class ActivitySavingGoals extends AppCompatActivity {

    private Button backButton;
    private Button addSavingGoalButton;
    private Button viewSavingGoalsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saving_goals);

        // Initialize views
        backButton = findViewById(R.id.back_button);
        addSavingGoalButton = findViewById(R.id.add_saving_goal_button);
        viewSavingGoalsButton = findViewById(R.id.view_saving_goals_button);

        // Set up click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the current activity and return to the previous one
                finish();
            }
        });

        // Set up click listener for the Add Saving Goal button
        addSavingGoalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the Add Saving Goal activity (replace AddSavingGoalActivity with your target activity)
                Intent intent = new Intent(ActivitySavingGoals.this, AddSavingGoalActivity.class);
                startActivity(intent);
            }
        });

        // Set up click listener for the View Saving Goals button
        viewSavingGoalsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to the View Saving Goals activity (replace ViewSavingGoalsActivity with your target activity)
                Intent intent = new Intent(ActivitySavingGoals.this, ViewSavingGoalsActivity.class);
                startActivity(intent);
            }
        });
    }
}
