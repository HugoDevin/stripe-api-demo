package com.example.ecommerce.catalog;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity @Table(name = "products")
public class Product {
  @Id public String sku;
  public String name;
  public BigDecimal price;
  public String currency;
  public boolean active = true;
  public Instant createdAt = Instant.now();
  public Instant updatedAt = Instant.now();
}
