package com.example.ecommerce.order;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity @Table(name="order_items")
public class OrderItem {
  @Id public UUID id = UUID.randomUUID();
  @ManyToOne @JoinColumn(name="order_id") public OrderEntity order;
  public String sku;
  public String name;
  public BigDecimal unitPrice;
  public int qty;
  public BigDecimal lineTotal;
}
