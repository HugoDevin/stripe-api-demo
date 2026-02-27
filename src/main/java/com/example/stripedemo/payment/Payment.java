package com.example.stripedemo.payment;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
public class Payment {
    @Id
    private String id = UUID.randomUUID().toString();
    private String orderId;
    private String provider;
    private String providerIntentId;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    private Long amount;
    private String currency;
    private String clientSecret;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    public String getId() {return id;} public String getOrderId() {return orderId;} public void setOrderId(String orderId) {this.orderId = orderId;}
    public String getProvider() {return provider;} public void setProvider(String provider) {this.provider = provider;}
    public String getProviderIntentId() {return providerIntentId;} public void setProviderIntentId(String providerIntentId) {this.providerIntentId = providerIntentId;}
    public PaymentStatus getStatus() {return status;} public void setStatus(PaymentStatus status) {this.status = status;}
    public Long getAmount() {return amount;} public void setAmount(Long amount) {this.amount = amount;}
    public String getCurrency() {return currency;} public void setCurrency(String currency) {this.currency = currency;}
    public String getClientSecret() {return clientSecret;} public void setClientSecret(String clientSecret) {this.clientSecret = clientSecret;}
    public OffsetDateTime getCreatedAt() {return createdAt;} public void setCreatedAt(OffsetDateTime createdAt) {this.createdAt = createdAt;}
    public OffsetDateTime getUpdatedAt() {return updatedAt;} public void setUpdatedAt(OffsetDateTime updatedAt) {this.updatedAt = updatedAt;}
}
