package com.example.hacktj20.ui.dashboard;

public class SavedItem {
    private String brand;
    private String productName;
    private String priceScore;

    public SavedItem(String brand, String productName, String priceScore) {
        this.brand = brand;
        this.productName = productName;
        this.priceScore = priceScore;
    }

    public String getBrand() {
        return brand;
    }

    public String getProductName() {
        return productName;
    }

    public String getPriceScore() {
        return priceScore;
    }
}