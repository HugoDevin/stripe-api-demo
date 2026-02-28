package com.example.ecommerce.payment;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="webhook_events")
public class WebhookEventEntity {
  @Id public UUID id = UUID.randomUUID();
  @Column(unique = true) public String providerEventId;
  public String type;
  @Column(columnDefinition="text") public String payloadJson;
  public Instant receivedAt = Instant.now();
}
