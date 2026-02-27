package com.example.ecommerce.common.idempotency;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="processed_events", uniqueConstraints=@UniqueConstraint(columnNames={"eventId","consumerName"}))
public class ProcessedEvent {
  @Id public UUID id = UUID.randomUUID();
  public String eventId;
  public String consumerName;
  public Instant processedAt = Instant.now();
}
