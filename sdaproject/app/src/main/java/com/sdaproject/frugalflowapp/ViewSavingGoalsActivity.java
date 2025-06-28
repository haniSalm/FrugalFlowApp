package com.sdaproject.frugalflowapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ViewSavingGoalsActivity extends AppCompatActivity {

    private Button backButton;
    private ListView savingGoalsListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_saving_goals); // Ensure the layout file is named appropriately

        // Initialize UI components
        backButton = findViewById(R.id.back_button);
        savingGoalsListView = findViewById(R.id.saving_goals_list);

        // Set up the back button click listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the previous activity
                finish(); // Close this activity
            }
        });

        // Set up the ListView with data
        populateSavingGoals();
    }

    /**
     * Populates the ListView with dummy data for saving goals.
     * Replace this with dynamic data from your database (e.g., Firebase) as needed.
     */
    private void populateSavingGoals() {
        // Sample data for saving goals
        List<String> savingGoals = new ArrayList<>();
        savingGoals.add("Vacation Fund - $1,000");
        savingGoals.add("Emergency Savings - $5,000");
        savingGoals.add("New Car - $15,000");
        savingGoals.add("Home Renovation - $10,000");
        savingGoals.add("Education Fund - $20,000");

        // Create an adapter to bind the data to the ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1, // Default list item layout
                savingGoals
        );

        // Set the adapter to the ListView
        savingGoalsListView.setAdapter(adapter);
    }
}
