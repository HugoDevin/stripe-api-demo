package com.example.ecommerce.invoicing;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="receipts")
public class Receipt {
  @Id public UUID id = UUID.randomUUID();
  @Column(unique = true) public UUID orderId;
  @Column(unique = true) public String receiptNo;
  public Instant issuedAt = Instant.now();
}
