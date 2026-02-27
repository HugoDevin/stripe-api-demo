package com.example.ecommerce.notification;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="notifications")
public class NotificationEntity {
  @Id public UUID id = UUID.randomUUID();
  public UUID orderId;
  public String channel;
  public String toAddr;
  public String subject;
  @Column(columnDefinition="text") public String body;
  public String status;
  public Instant sentAt;
  public Instant createdAt = Instant.now();
}
