package com.sdaproject.frugalflowapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<Category> categories;
    private OnCategorySelectListener selectListener;

    public interface OnCategorySelectListener {
        void onCategorySelected(Category category);
    }

    public CategoryAdapter(List<Category> categories, OnCategorySelectListener listener) {
        this.categories = categories;
        this.selectListener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);

        // Set category details
        holder.categoryEmoji.setText(category.getEmoji());
        holder.categoryName.setText(category.getName());

        // Setup product list RecyclerView
        if (category.getProducts() != null && !category.getProducts().isEmpty()) {
            ProductAdapter productAdapter = new ProductAdapter(category.getProducts());
            holder.productsRecyclerView.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
            holder.productsRecyclerView.setAdapter(productAdapter);
        }

        // Toggle dropdown visibility
        holder.dropdownIcon.setOnClickListener(v -> {
            category.setExpanded(!category.isExpanded());
            updateDropdownVisibility(holder, category);
        });

        // Main item click listener
        holder.itemView.setOnClickListener(v -> {
            if (selectListener != null) {
                selectListener.onCategorySelected(category);
            }
            category.setExpanded(!category.isExpanded());
            updateDropdownVisibility(holder, category);
        });

        // Initial dropdown state
        updateDropdownVisibility(holder, category);
    }

    private void updateDropdownVisibility(@NonNull CategoryViewHolder holder, Category category) {
        if (category.getProducts() == null || category.getProducts().isEmpty()) {
            holder.dropdownIcon.setVisibility(View.GONE);
            holder.productsRecyclerView.setVisibility(View.GONE);
            return;
        }

        if (category.isExpanded()) {
            holder.productsRecyclerView.setVisibility(View.VISIBLE);
            holder.dropdownIcon.setRotation(180); // Rotate icon to point up
        } else {
            holder.productsRecyclerView.setVisibility(View.GONE);
            holder.dropdownIcon.setRotation(0); // Reset icon rotation
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryEmoji;
        TextView categoryName;
        ImageView dropdownIcon;
        RecyclerView productsRecyclerView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryEmoji = itemView.findViewById(R.id.category_emoji);
            categoryName = itemView.findViewById(R.id.category_name);
            dropdownIcon = itemView.findViewById(R.id.dropdown_icon);
            productsRecyclerView = itemView.findViewById(R.id.products_recycler_view);
        }
    }
}