package com.example.ecommerce.reporting;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Entity @Table(name="sales_daily_fact")
public class SalesDailyFact {
  @Id public LocalDate date;
  public BigDecimal totalAmount = BigDecimal.ZERO;
  public long orderCount = 0;
  public Instant updatedAt = Instant.now();
}
