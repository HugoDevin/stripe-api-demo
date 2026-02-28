package com.example.ecommerce.admin.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "admin_users")
public class AdminUser {
  @Id public UUID id = UUID.randomUUID();
  @Column(unique = true, nullable = false) public String email;
  @Column(nullable = false) public String passwordHash;
  public String displayName;
  @Enumerated(EnumType.STRING) @Column(nullable = false) public AdminRole role;
  @Column(nullable = false) public boolean emailVerified = false;
  @Column(nullable = false) public boolean enabled = false;
  @Column(nullable = false) public boolean locked = false;
  public Instant lastLoginAt;
  @Column(nullable = false) public Instant createdAt = Instant.now();
  @Column(nullable = false) public Instant updatedAt = Instant.now();
}
