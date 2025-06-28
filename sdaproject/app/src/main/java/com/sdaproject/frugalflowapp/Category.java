package com.sdaproject.frugalflowapp;

import java.util.List;

public class Category {
    private String name;
    private String emoji;
    private String description;
    private List<Product> products;
    private boolean isExpanded;

    public Category(String name, String emoji, String description, List<Product> products) {
        this.name = name;
        this.emoji = emoji;
        this.description = description;
        this.products = products;
        this.isExpanded = false;
    }

    public String getName() {
        return name;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getDescription() {
        return description;
    }

    public List<Product> getProducts() {
        return products;
    }

    public boolean isExpanded() {
        return isExpanded;
    }

    public void setExpanded(boolean expanded) {
        isExpanded = expanded;
    }
}