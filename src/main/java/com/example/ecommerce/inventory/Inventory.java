package com.example.ecommerce.inventory;

import jakarta.persistence.*;
import java.time.Instant;

@Entity @Table(name="inventory")
public class Inventory {
  @Id public String sku;
  public int availableQty;
  public int reservedQty;
  @Version public long version;
  public Instant updatedAt = Instant.now();
}
