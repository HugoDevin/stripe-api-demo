package com.example.stripedemo.order;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    private String id = UUID.randomUUID().toString();
    private String customerEmail;
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    private Long totalAmount;
    private String currency;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    public String getId() {return id;}
    public String getCustomerEmail() {return customerEmail;} public void setCustomerEmail(String customerEmail) {this.customerEmail = customerEmail;}
    public OrderStatus getStatus() {return status;} public void setStatus(OrderStatus status) {this.status = status;}
    public Long getTotalAmount() {return totalAmount;} public void setTotalAmount(Long totalAmount) {this.totalAmount = totalAmount;}
    public String getCurrency() {return currency;} public void setCurrency(String currency) {this.currency = currency;}
    public OffsetDateTime getCreatedAt() {return createdAt;} public void setCreatedAt(OffsetDateTime createdAt) {this.createdAt = createdAt;}
    public OffsetDateTime getUpdatedAt() {return updatedAt;} public void setUpdatedAt(OffsetDateTime updatedAt) {this.updatedAt = updatedAt;}
    public List<OrderItem> getItems() {return items;}
}
