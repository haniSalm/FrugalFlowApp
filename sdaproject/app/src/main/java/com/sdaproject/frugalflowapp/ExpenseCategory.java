package com.sdaproject.frugalflowapp;

public class ExpenseCategory {
    private String name;


    private int iconResource; // This should match your drawable resources (ic_food, etc.)

    // Constructor
    public ExpenseCategory(String name, int iconResource) {
        this.name = name;
        this.iconResource = iconResource;
    }

    private int iconResourceId;
    private String color; // Added color field

    // Constructor with all parameters
    public ExpenseCategory(String name, int iconResourceId, String color) {
        this.name = name;
        this.iconResourceId = iconResourceId;
        this.color = color;
    }

    // Getters and setters
    public String getName() {
        return name;
    }
    public int getIconResource() { return iconResource; }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconResourceId() {
        return iconResourceId;
    }

    public void setIconResourceId(int iconResourceId) {
        this.iconResourceId = iconResourceId;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}