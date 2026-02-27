package com.example.stripedemo.fulfillment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "fulfillments")
public class Fulfillment {
    @Id
    private String id = UUID.randomUUID().toString();
    private String orderId;
    private String status;
    private OffsetDateTime activatedAt;
    @Column(columnDefinition = "TEXT")
    private String detailsJson;
    public String getId() {return id;} public String getOrderId() {return orderId;} public void setOrderId(String orderId) {this.orderId = orderId;}
    public String getStatus() {return status;} public void setStatus(String status) {this.status = status;}
    public OffsetDateTime getActivatedAt() {return activatedAt;} public void setActivatedAt(OffsetDateTime activatedAt) {this.activatedAt = activatedAt;}
    public String getDetailsJson() {return detailsJson;} public void setDetailsJson(String detailsJson) {this.detailsJson = detailsJson;}
}
