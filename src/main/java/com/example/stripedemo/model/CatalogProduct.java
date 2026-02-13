package com.example.stripedemo.model;

public class CatalogProduct {

    private final String name;
    private final long price;
    private final boolean active;

    public CatalogProduct(String name, long price, boolean active) {
        this.name = name;
        this.price = price;
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public long getPrice() {
        return price;
    }

    public boolean isActive() {
        return active;
    }

    public CatalogProduct withPrice(long newPrice) {
        return new CatalogProduct(name, newPrice, active);
    }

    public CatalogProduct withActive(boolean newStatus) {
        return new CatalogProduct(name, price, newStatus);
    }
}
