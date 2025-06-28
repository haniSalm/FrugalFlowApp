package com.sdaproject.frugalflowapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExpenseCategoryAdapter extends RecyclerView.Adapter<ExpenseCategoryAdapter.CategoryViewHolder> {
    private List<ExpenseCategory> categories;
    private OnItemClickListener onItemClickListener;
    private List<Expense> expenses = Collections.emptyList(); // Initialize with an empty list

    public interface OnItemClickListener {
        void onItemClick(ExpenseCategory category);
    }
    public void submitList(List<Expense> newExpenses) {
        this.expenses = newExpenses != null ? new ArrayList<>(newExpenses) : Collections.emptyList();
        notifyDataSetChanged();
    }
    public ExpenseCategoryAdapter(List<ExpenseCategory> categories, OnItemClickListener listener) {
        this.categories = new ArrayList<>(categories); // Defensive copying
        this.onItemClickListener = listener;
    }

    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses != null ? new ArrayList<>(expenses) : Collections.emptyList(); // Avoid null values
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        ExpenseCategory category = categories.get(position);
        holder.bind(category);
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {
        private TextView categoryName;
        private ImageView categoryIcon;
        private TextView categoryAmount;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.tvUniqueCategoryName);
            categoryIcon = itemView.findViewById(R.id.ivUniqueCategoryIcon);
            categoryAmount = itemView.findViewById(R.id.tvUniqueCategoryAmount);
        }

        public void bind(final ExpenseCategory category) {
            categoryName.setText(category.getName());
            categoryIcon.setImageResource(category.getIconResourceId());

            double totalAmount = calculateCategoryTotal(category);
            categoryAmount.setText(String.format("$%.2f", totalAmount));

            // Allow any category to be clicked (no unique functionality)
            itemView.setOnClickListener(v -> {
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(category); // Invoke listener for any category
                }
            });
        }

        private double calculateCategoryTotal(ExpenseCategory category) {
            if (expenses.isEmpty()) return 0.0;

            return expenses.stream()
                    .filter(expense -> expense.getCategory().equals(category))
                    .mapToDouble(Expense::getTotalAmount)
                    .sum();
        }
    }
}