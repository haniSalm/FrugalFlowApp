package com.sdaproject.frugalflowapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

public class ExpenseAdapter extends ListAdapter<Expense, ExpenseAdapter.ExpenseViewHolder> {

    protected ExpenseAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_expense, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = getItem(position);
        holder.bind(expense);
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        private final ImageView categoryIcon;
        private final TextView description;
        private final TextView amount;
        private final TextView category;
        private final TextView date;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryIcon = itemView.findViewById(R.id.expenseCategoryIcon);
            description = itemView.findViewById(R.id.expenseDescription);
            amount = itemView.findViewById(R.id.expenseAmount);
            category = itemView.findViewById(R.id.expenseCategory);
            date = itemView.findViewById(R.id.expenseDate);
        }

        public void bind(Expense expense) {
            description.setText(expense.getDescription());
            amount.setText(String.format("$%.2f", expense.getTotalAmount()));
            category.setText(expense.getCategory().getName());
            date.setText(expense.getDate());

            // Set category icon (you'll need to implement getIconResource() in ExpenseCategory)
            categoryIcon.setImageResource(expense.getCategory().getIconResource());
        }
    }

    private static final DiffUtil.ItemCallback<Expense> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Expense>() {
                @Override
                public boolean areItemsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
                    return oldItem.getDescription().equals(newItem.getDescription()) &&
                            oldItem.getDate().equals(newItem.getDate());
                }

                @Override
                public boolean areContentsTheSame(@NonNull Expense oldItem, @NonNull Expense newItem) {
                    return oldItem.equals(newItem);
                }
            };
}