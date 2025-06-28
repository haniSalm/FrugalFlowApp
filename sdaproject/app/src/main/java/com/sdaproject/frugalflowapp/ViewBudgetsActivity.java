package com.sdaproject.frugalflowapp;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ViewBudgetsActivity extends AppCompatActivity {
    private ListView budgetsList;
    private ArrayList<String> budgetCategoryList;
    private ArrayList<String> budgetAmountList;
    private DatabaseReference mDatabase;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_budget);

        // Initialize Firebase
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users").child(userId).child("budgets");

        // Set up ListView and Arrays to store budget info
        budgetsList = findViewById(R.id.budgets_list);
        budgetCategoryList = new ArrayList<>();
        budgetAmountList = new ArrayList<>();

        // Back button listener
        Button backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        // Fetch budgets from Firebase
        fetchBudgetsFromFirebase();
    }

    private void fetchBudgetsFromFirebase() {
        Log.d("ViewBudgetsActivity", "Fetching budgets from Firebase...");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                budgetCategoryList.clear();
                budgetAmountList.clear();

                // Loop through all budget categories (e.g., "Food", "Toys", etc.)
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String category = snapshot.child("category").getValue(String.class);
                    // Check if amount is a number or string, and handle accordingly
                    Object amountObj = snapshot.child("amount").getValue();
                    String amount = "0"; // Default value if amount is null

                    if (amountObj instanceof String) {
                        amount = (String) amountObj;
                    } else if (amountObj instanceof Long) {
                        amount = String.valueOf(amountObj); // Convert long to string
                    } else if (amountObj instanceof Double) {
                        amount = String.valueOf(amountObj); // Convert double to string
                    }

                    Log.d("ViewBudgetsActivity", "Category: " + category + " Amount: " + amount);

                    if (category != null && amount != null) {
                        budgetCategoryList.add(category);
                        budgetAmountList.add(amount);
                    } else {
                        Log.e("ViewBudgetsActivity", "Invalid data in Firebase: Category or Amount is null.");
                    }
                }

                // If we have data, set up custom adapter
                if (!budgetCategoryList.isEmpty() && !budgetAmountList.isEmpty()) {
                    CustomBudgetAdapter adapter = new CustomBudgetAdapter();
                    budgetsList.setAdapter(adapter);
                } else {
                    Toast.makeText(ViewBudgetsActivity.this, "No budgets available.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle cancellation error
                Log.e("ViewBudgetsActivity", "Error fetching data: " + databaseError.getMessage());
                Toast.makeText(ViewBudgetsActivity.this, "Failed to load budgets.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    // Custom Adapter to display category and amount in each item
    private class CustomBudgetAdapter extends ArrayAdapter<String> {
        public CustomBudgetAdapter() {
            super(ViewBudgetsActivity.this, R.layout.list_item_budget, budgetCategoryList);
        }

        @Override
        public View getView(int position, View convertView, android.view.ViewGroup parent) {
            // Inflate custom layout
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_budget, parent, false);
            }

            // Get references to the TextViews
            TextView categoryText = convertView.findViewById(R.id.category_text);
            TextView amountText = convertView.findViewById(R.id.amount_text);

            // Get category and amount data
            String category = budgetCategoryList.get(position);
            String amount = budgetAmountList.get(position);

            Log.d("ViewBudgetsActivity", "Category: " + category + " Amount: " + amount);

            try {
                // Format amount with currency symbol
                double budgetAmount = Double.parseDouble(amount); // Parse the amount to a double
                String formattedAmount = String.format("%s %.2f", "SAR", budgetAmount); // Format with SAR symbol

                // Set category and formatted amount
                categoryText.setText(category);
                amountText.setText(formattedAmount);
            } catch (NumberFormatException e) {
                // If there's an error parsing the amount, display a fallback message
                Log.e("ViewBudgetsActivity", "Error parsing amount: " + amount, e);
                amountText.setText("Invalid Amount");
            }

            return convertView;
        }
    }
}
