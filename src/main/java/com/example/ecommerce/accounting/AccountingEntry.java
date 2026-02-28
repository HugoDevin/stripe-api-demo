package com.example.ecommerce.accounting;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="accounting_entries")
public class AccountingEntry {
  @Id public UUID id = UUID.randomUUID();
  @Column(unique = true) public UUID orderId;
  public String debitAccount;
  public String creditAccount;
  public BigDecimal amount;
  public String currency;
  public Instant createdAt = Instant.now();
}
