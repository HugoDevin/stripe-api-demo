package com.example.ecommerce.admin.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "admin_email_outbox")
public class AdminEmailOutbox {
  @Id public UUID id = UUID.randomUUID();
  public String toEmail;
  public String subject;
  @Column(columnDefinition = "text") public String body;
  public String verifyUrl;
  public Instant createdAt = Instant.now();
}
