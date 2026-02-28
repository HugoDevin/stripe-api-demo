package com.example.ecommerce.messaging.outbox;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="outbox_events")
public class OutboxEvent {
  @Id public UUID id = UUID.randomUUID();
  public String eventType;
  public String aggregateId;
  @Column(columnDefinition = "text") public String payloadJson;
  public String status = "PENDING";
  public Instant createdAt = Instant.now();
  public Instant sentAt;
}
