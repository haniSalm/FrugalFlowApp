package com.sdaproject.frugalflowapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Locale;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder> {
    private List<Transaction> transactions;

    public TransactionAdapter(List<Transaction> transactions) {
        this.transactions = transactions;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_transaction, parent, false);
        return new TransactionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        Transaction transaction = transactions.get(position);
        holder.descriptionText.setText(transaction.getDescription());
        holder.dateText.setText(transaction.getDate());
        holder.amountText.setText(String.format(Locale.getDefault(), "PKR %.2f", transaction.getAmount()));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        TextView descriptionText;
        TextView dateText;
        TextView amountText;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            descriptionText = itemView.findViewById(R.id.transaction_description);
            dateText = itemView.findViewById(R.id.transaction_date);
            amountText = itemView.findViewById(R.id.transaction_amount);
        }
    }
}