package com.example.ecommerce.order;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

@Entity @Table(name="orders")
public class OrderEntity {
  @Id public UUID id = UUID.randomUUID();
  public String customerEmail;
  @Enumerated(EnumType.STRING) public OrderStatus status;
  public BigDecimal totalAmount;
  public String currency;
  public Instant createdAt = Instant.now();
  public Instant updatedAt = Instant.now();
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
  public List<OrderItem> items = new ArrayList<>();
}
