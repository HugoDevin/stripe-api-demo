package com.example.stripedemo.catalog;

import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id
    private String sku;
    private String name;
    private Long price;
    private String currency;
    private boolean active;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    public String getSku() {return sku;} public void setSku(String sku) {this.sku = sku;}
    public String getName() {return name;} public void setName(String name) {this.name = name;}
    public Long getPrice() {return price;} public void setPrice(Long price) {this.price = price;}
    public String getCurrency() {return currency;} public void setCurrency(String currency) {this.currency = currency;}
    public boolean isActive() {return active;} public void setActive(boolean active) {this.active = active;}
    public OffsetDateTime getCreatedAt() {return createdAt;} public void setCreatedAt(OffsetDateTime createdAt) {this.createdAt = createdAt;}
    public OffsetDateTime getUpdatedAt() {return updatedAt;} public void setUpdatedAt(OffsetDateTime updatedAt) {this.updatedAt = updatedAt;}
}
