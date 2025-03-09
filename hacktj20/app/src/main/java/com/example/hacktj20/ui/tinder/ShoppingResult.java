package com.example.hacktj20.ui.tinder;

public class ShoppingResult {
    private String title;
    private String price;
    private String source;
    private String thumbnail;

    public ShoppingResult(String title, String price, String source, String thumbnail) {
        this.title = title;
        this.price = price;
        this.source = source;
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getPrice() {
        return price;
    }

    public String getSource() {
        return source;
    }

    public String getThumbnail() {
        return thumbnail;
    }
}