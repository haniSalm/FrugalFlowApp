package com.sdaproject.frugalflowapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClothingCategoryActivity extends AppCompatActivity {
    private DatabaseReference userRef;
    private TextView budgetAmountView;
    private TextView remainingBudgetView;
    private RecyclerView transactionsRecyclerView;
    private LinearLayout emptyState;
    private TransactionAdapter transactionAdapter;
    private List<Transaction> transactionList;
    private double currentBudget = 0;
    private double totalSpent = 0;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing);

        // Initialize views
        budgetAmountView = findViewById(R.id.budget_amount);
        remainingBudgetView = findViewById(R.id.remaining_budget);
        FloatingActionButton fab = findViewById(R.id.add_clothing_expense);
        transactionsRecyclerView = findViewById(R.id.transactions_recycler);
        emptyState = findViewById(R.id.empty_state);

        backButton = findViewById(R.id.back_button);

        // Back button logic
        backButton.setOnClickListener(v -> finish());
        // Initialize Firebase
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        // Initialize RecyclerView
        transactionList = new ArrayList<>();
        transactionAdapter = new TransactionAdapter(transactionList);
        transactionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        transactionsRecyclerView.setAdapter(transactionAdapter);
        transactionsRecyclerView.setHasFixedSize(true);

        // Load data
        loadBudget();
        loadClothingTransactions();

        // Setup FAB to add new expense
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddTransactionActivity.class);
            intent.putExtra("category", "Clothing");
            startActivity(intent);
        });
    }

    private void loadBudget() {
        userRef.child("clothing").child("Clothing").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Double budget = dataSnapshot.child("amount").getValue(Double.class);
                    if (budget != null) {
                        currentBudget = budget;
                        updateBudgetViews();
                    }
                } else {
                    budgetAmountView.setText(getString(R.string.no_budget_set));
                    remainingBudgetView.setText("");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ClothingCategoryActivity.this,
                        R.string.budget_load_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadClothingTransactions() {
        userRef.child("clothing").child("Clothing").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                transactionList.clear();
                totalSpent = 0;

                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Transaction transaction = snapshot.getValue(Transaction.class);
                        if (transaction != null) {
                            transactionList.add(transaction);
                            totalSpent += transaction.getAmount();
                        }
                    }
                }

                updateBudgetViews();
                transactionAdapter.notifyDataSetChanged();

                // Update visibility
                if (transactionList.isEmpty()) {
                    emptyState.setVisibility(View.VISIBLE);
                    transactionsRecyclerView.setVisibility(View.GONE);
                } else {
                    emptyState.setVisibility(View.GONE);
                    transactionsRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(ClothingCategoryActivity.this,
                        R.string.transaction_load_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBudgetViews() {
        if (currentBudget > 0) {
            String budgetText = String.format(Locale.getDefault(),
                    getString(R.string.budget_amount_format),
                    getString(R.string.currency_pkr),
                    currentBudget);
            budgetAmountView.setText(budgetText);

            double remaining = currentBudget - totalSpent;
            String remainingText = String.format(Locale.getDefault(),
                    getString(R.string.remaining_budget_format),
                    getString(R.string.currency_pkr),
                    remaining);
            remainingBudgetView.setText(remainingText);

            // Update text color based on remaining budget
            int colorId = remaining < 0 ?
                    android.R.color.holo_red_dark :
                    android.R.color.holo_green_dark;
            remainingBudgetView.setTextColor(ContextCompat.getColor(this, colorId));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning from AddTransactionActivity
        loadBudget();
        loadClothingTransactions();
    }
}