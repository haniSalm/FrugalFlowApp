package com.sdaproject.frugalflowapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;

public class CategoryExploreActivity extends AppCompatActivity {
    private DatabaseHelper databaseHelper;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        backButton = findViewById(R.id.back_button);
        backButton.setOnClickListener(v -> finish());

        databaseHelper = new DatabaseHelper(this);

        setupCategoryCardListeners();
    }

    private void setupCategoryCardListeners() {
        MaterialCardView foodCard = findViewById(R.id.food_card);
        MaterialCardView transportationCard = findViewById(R.id.transportation_card);
        MaterialCardView housingCard = findViewById(R.id.housing_card);
        MaterialCardView entertainmentCard = findViewById(R.id.entertainment_card);
        MaterialCardView utilitiesCard = findViewById(R.id.utilities_card);
        MaterialCardView healthcareCard = findViewById(R.id.healthcare_card);
        MaterialCardView clothingCard = findViewById(R.id.clothing_card);

        foodCard.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryExploreActivity.this, FoodCategoryActivity.class);
            startActivity(intent);
        });


        transportationCard.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryExploreActivity.this, TransportationCategoryActivity.class);
            startActivity(intent);
        });
        housingCard.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryExploreActivity.this, HousingCategoryActivity.class);
            startActivity(intent);
        });
        entertainmentCard.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryExploreActivity.this, EntertainmentCategoryActivity.class);
            startActivity(intent);
        });
        utilitiesCard.setOnClickListener(v -> {
            Intent intent = new Intent(CategoryExploreActivity.this, UtilitiesCategoryActivity.class);
            startActivity(intent);
        });
        healthcareCard.setOnClickListener(v ->  {
            Intent intent = new Intent(CategoryExploreActivity.this, HealthCareCategoryActivity.class);
            startActivity(intent);
        });
        clothingCard.setOnClickListener(v ->  {
            Intent intent = new Intent(CategoryExploreActivity.this, ClothingCategoryActivity.class);
            startActivity(intent);
        });
    }

    private void showCategoryToast(String categoryName) {
        Toast.makeText(this, "Selected category: " + categoryName, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        databaseHelper.close();
        super.onDestroy();
    }
}