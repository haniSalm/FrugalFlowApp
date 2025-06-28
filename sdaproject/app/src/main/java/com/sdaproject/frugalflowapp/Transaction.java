package com.sdaproject.frugalflowapp;

public class Transaction {
    private String id;
    private double amount;
    private String description;
    private String category;
    private String date;

    public Transaction() {
        // Default constructor required for Firebase
    }

    public Transaction(double amount, String description, String category, String date) {
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.date = date;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}