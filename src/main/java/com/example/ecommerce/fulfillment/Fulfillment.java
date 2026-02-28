package com.example.ecommerce.fulfillment;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="fulfillments")
public class Fulfillment {
  @Id public UUID id = UUID.randomUUID();
  @Column(unique = true) public UUID orderId;
  public String status;
  public Instant activatedAt = Instant.now();
  @Column(columnDefinition="text") public String detailsJson;
}
