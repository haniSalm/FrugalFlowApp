package com.sdaproject.frugalflowapp;

import java.util.Objects;

public class Expense {
    private String description;
    private double totalAmount;
    private ExpenseCategory category;
    private String date;

    public Expense(String description, double totalAmount, ExpenseCategory category, String date) {
        this.description = description;
        this.totalAmount = totalAmount;
        this.category = category;
        this.date = date;
    }

    // Getters
    public String getDescription() { return description; }
    public double getTotalAmount() { return totalAmount; }
    public ExpenseCategory getCategory() { return category; }
    public String getDate() { return date; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Double.compare(expense.totalAmount, totalAmount) == 0 &&
                Objects.equals(description, expense.description) &&
                Objects.equals(category, expense.category) &&
                Objects.equals(date, expense.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, totalAmount, category, date);
    }
}