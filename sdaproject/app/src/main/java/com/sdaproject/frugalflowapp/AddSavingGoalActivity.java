package com.sdaproject.frugalflowapp;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class AddSavingGoalActivity extends AppCompatActivity {
    static ArrayList<String> savingGoalsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_saving_goal);

        EditText savingGoal = findViewById(R.id.budget_name);
        Button saveSavingGoalButton = findViewById(R.id.save_budget_button);
        Button backButton = findViewById(R.id.back_button); // ← Add this line

        // Back button logic
        backButton.setOnClickListener(v -> finish()); // ← Add this line

        saveSavingGoalButton.setOnClickListener(v -> {
            String goal = savingGoal.getText().toString();

            if (!goal.isEmpty()) {
                savingGoalsList.add(goal);
                Toast.makeText(this, "Saving Goal Added!", Toast.LENGTH_SHORT).show();
                savingGoal.setText("");
            } else {
                Toast.makeText(this, "Please fill the field.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
