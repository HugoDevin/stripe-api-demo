package com.example.ecommerce.payment;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="payments")
public class PaymentEntity {
  @Id public UUID id = UUID.randomUUID();
  @Column(unique = true) public UUID orderId;
  public String provider;
  public String providerIntentId;
  @Enumerated(EnumType.STRING) public PaymentStatus status;
  public BigDecimal amount;
  public String currency;
  public String clientSecret;
  public Instant createdAt = Instant.now();
  public Instant updatedAt = Instant.now();
}
