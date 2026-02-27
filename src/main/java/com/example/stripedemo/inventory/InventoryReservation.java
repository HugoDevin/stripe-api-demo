package com.example.stripedemo.inventory;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_reservations", uniqueConstraints = @UniqueConstraint(columnNames = {"orderId", "sku"}))
public class InventoryReservation {
    @Id
    private String id = UUID.randomUUID().toString();
    private String orderId;
    private String sku;
    private Long qty;
    @Enumerated(EnumType.STRING)
    private InventoryReservationStatus status;
    private OffsetDateTime expiresAt;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    public String getId() {return id;} public String getOrderId() {return orderId;} public void setOrderId(String orderId) {this.orderId = orderId;}
    public String getSku() {return sku;} public void setSku(String sku) {this.sku = sku;}
    public Long getQty() {return qty;} public void setQty(Long qty) {this.qty = qty;}
    public InventoryReservationStatus getStatus() {return status;} public void setStatus(InventoryReservationStatus status) {this.status = status;}
    public OffsetDateTime getExpiresAt() {return expiresAt;} public void setExpiresAt(OffsetDateTime expiresAt) {this.expiresAt = expiresAt;}
    public OffsetDateTime getCreatedAt() {return createdAt;} public void setCreatedAt(OffsetDateTime createdAt) {this.createdAt = createdAt;}
    public OffsetDateTime getUpdatedAt() {return updatedAt;} public void setUpdatedAt(OffsetDateTime updatedAt) {this.updatedAt = updatedAt;}
}
