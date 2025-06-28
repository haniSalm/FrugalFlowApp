package com.sdaproject.frugalflowapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sdaproject.frugalflowapp.R;

import java.util.List;

public class IncomeSourcesAdapter extends RecyclerView.Adapter<IncomeSourcesAdapter.IncomeViewHolder> {

    private final List<IncomeSource> incomeSources;

    public IncomeSourcesAdapter(List<IncomeSource> incomeSources) {
        this.incomeSources = incomeSources;
    }

    @NonNull
    @Override
    public IncomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_income_source, parent, false);
        return new IncomeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncomeViewHolder holder, int position) {
        IncomeSource income = incomeSources.get(position);
        holder.tvIncomeName.setText(income.getName());
        holder.tvIncomeAmount.setText(income.getAmount());
        holder.tvIncomeGrowth.setText(income.getGrowth());
    }

    @Override
    public int getItemCount() {
        return incomeSources.size();
    }

    static class IncomeViewHolder extends RecyclerView.ViewHolder {
        TextView tvIncomeName, tvIncomeAmount, tvIncomeGrowth;

        public IncomeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIncomeName = itemView.findViewById(R.id.tvIncomeName);
            tvIncomeAmount = itemView.findViewById(R.id.tvIncomeAmount);
            tvIncomeGrowth = itemView.findViewById(R.id.tvIncomeGrowth);
        }
    }
}
