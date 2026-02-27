package com.example.stripedemo.order;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id
    private String id = UUID.randomUUID().toString();
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
    private String sku;
    private String name;
    private Long unitPrice;
    private Long qty;
    private Long lineTotal;
    public String getId() {return id;}
    public Order getOrder() {return order;} public void setOrder(Order order) {this.order = order;}
    public String getSku() {return sku;} public void setSku(String sku) {this.sku = sku;}
    public String getName() {return name;} public void setName(String name) {this.name = name;}
    public Long getUnitPrice() {return unitPrice;} public void setUnitPrice(Long unitPrice) {this.unitPrice = unitPrice;}
    public Long getQty() {return qty;} public void setQty(Long qty) {this.qty = qty;}
    public Long getLineTotal() {return lineTotal;} public void setLineTotal(Long lineTotal) {this.lineTotal = lineTotal;}
}
