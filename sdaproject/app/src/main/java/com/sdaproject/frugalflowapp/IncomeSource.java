package com.sdaproject.frugalflowapp;

public class IncomeSource {
    private String name;
    private String amount;
    private String growth;

    public IncomeSource(String name, String amount, String growth) {
        this.name = name;
        this.amount = amount;
        this.growth = growth;
    }

    public String getName() {
        return name;
    }

    public String getAmount() {
        return amount;
    }

    public String getGrowth() {
        return growth;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setGrowth(String growth) {
        this.growth = growth;
    }
}
