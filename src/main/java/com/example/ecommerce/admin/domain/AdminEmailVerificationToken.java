package com.example.ecommerce.admin.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "admin_email_verification_tokens")
public class AdminEmailVerificationToken {
  @Id public UUID id = UUID.randomUUID();
  @Column(nullable = false) public UUID userId;
  @Column(unique = true, nullable = false) public String token;
  @Column(nullable = false) public Instant expiresAt;
  public Instant usedAt;
  @Column(nullable = false) public Instant createdAt = Instant.now();
}
