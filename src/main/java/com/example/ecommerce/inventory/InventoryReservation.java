package com.example.ecommerce.inventory;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="inventory_reservations")
public class InventoryReservation {
  @Id public UUID id = UUID.randomUUID();
  @Column(unique = true) public UUID orderId;
  public String sku;
  public int qty;
  @Enumerated(EnumType.STRING) public ReservationStatus status;
  public Instant expiresAt;
  public Instant createdAt = Instant.now();
  public Instant updatedAt = Instant.now();
}
