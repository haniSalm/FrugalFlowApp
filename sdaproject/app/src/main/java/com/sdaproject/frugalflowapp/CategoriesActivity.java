// CategoriesActivity.java
package com.sdaproject.frugalflowapp;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {
    private RecyclerView categoryRecyclerView;
    private CategoryAdapter categoryAdapter;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        // Initialize database helper
        databaseHelper = new DatabaseHelper(this);

        // Initialize RecyclerView
        categoryRecyclerView = findViewById(R.id.category_recycler_view);
        categoryRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Load categories from database
        loadCategories();
    }

    private void loadCategories() {
        List<Category> categoryList = databaseHelper.getAllCategoriesWithProducts();

        // Create and set adapter
        categoryAdapter = new CategoryAdapter(categoryList, new CategoryAdapter.OnCategorySelectListener() {
            @Override
            public void onCategorySelected(Category category) {
                // Handle category selection
                Toast.makeText(CategoriesActivity.this,
                        "Selected Category: " + category.getName(),
                        Toast.LENGTH_SHORT).show();
            }
        });
        categoryRecyclerView.setAdapter(categoryAdapter);
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}