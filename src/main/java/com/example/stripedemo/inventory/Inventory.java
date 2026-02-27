package com.example.stripedemo.inventory;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "inventory")
public class Inventory {
    @Id
    private String sku;
    private Long availableQty;
    private Long reservedQty;
    private Long version;
    private OffsetDateTime updatedAt;
    public String getSku() {return sku;} public void setSku(String sku) {this.sku = sku;}
    public Long getAvailableQty() {return availableQty;} public void setAvailableQty(Long availableQty) {this.availableQty = availableQty;}
    public Long getReservedQty() {return reservedQty;} public void setReservedQty(Long reservedQty) {this.reservedQty = reservedQty;}
    public Long getVersion() {return version;} public void setVersion(Long version) {this.version = version;}
    public OffsetDateTime getUpdatedAt() {return updatedAt;} public void setUpdatedAt(OffsetDateTime updatedAt) {this.updatedAt = updatedAt;}
}
