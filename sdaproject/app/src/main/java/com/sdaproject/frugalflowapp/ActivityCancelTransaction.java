package com.sdaproject.frugalflowapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class ActivityCancelTransaction extends AppCompatActivity {

    private Button backButton;
    private Button cancelTransactionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cancel_transaction);

        // Initialize views
        backButton = findViewById(R.id.back_button);
        cancelTransactionButton = findViewById(R.id.cancel_transaction_button);

        // Set up click listener for the back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the current activity and return to the previous one
                finish();
            }
        });

        // Set up click listener for the Cancel Transaction button
        cancelTransactionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform cancel transaction logic
                cancelTransaction();
            }
        });
    }

    // Method to cancel the transaction
    private void cancelTransaction() {
        // Here you can add your logic to cancel the transaction, such as
        // updating the database, calling an API, or clearing any relevant data.
        // For now, show a Toast message as an example of the action.

        Toast.makeText(ActivityCancelTransaction.this, "Transaction Canceled", Toast.LENGTH_SHORT).show();

        // After canceling, you may want to finish the activity or navigate to another screen
        finish(); // Optionally, you can call finish() to return to the previous activity.
    }
}
