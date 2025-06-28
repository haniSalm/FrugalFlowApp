package com.sdaproject.frugalflowapp;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.List;

public class ExpenseViewModel extends AndroidViewModel {
    private final MutableLiveData<List<Expense>> foodExpenses = new MutableLiveData<>();
    private final MutableLiveData<Double> foodTotal = new MutableLiveData<>(0.0);
    private final DatabaseHelper databaseHelper;

    public ExpenseViewModel(@NonNull Application application) {
        super(application);
        databaseHelper = new DatabaseHelper(application);
        refreshFoodData();
    }

    public LiveData<List<Expense>> getFoodExpenses() {
        return foodExpenses;
    }

    public LiveData<Double> getFoodTotal() {
        return foodTotal;
    }

    public void refreshFoodData() {
        new Thread(() -> {
            List<Expense> expenses = databaseHelper.getExpensesByCategory("Food");
            double total = databaseHelper.getMonthlyTotalByCategory("Food");
            foodExpenses.postValue(expenses);
            foodTotal.postValue(total);
        }).start();
    }

    public void addExpense(double amount, String category, String description) {
        new Thread(() -> {
            databaseHelper.addExpense(amount, category, description);
            refreshFoodData();
        }).start();
    }
}