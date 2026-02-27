package com.example.stripedemo.reporting;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.OffsetDateTime;

@Entity
@Table(name = "sales_daily_fact")
public class SalesDailyFact {
    @Id
    private LocalDate date;
    private Long totalAmount;
    private Long orderCount;
    private OffsetDateTime updatedAt;
    public LocalDate getDate() {return date;} public void setDate(LocalDate date) {this.date = date;}
    public Long getTotalAmount() {return totalAmount;} public void setTotalAmount(Long totalAmount) {this.totalAmount = totalAmount;}
    public Long getOrderCount() {return orderCount;} public void setOrderCount(Long orderCount) {this.orderCount = orderCount;}
    public OffsetDateTime getUpdatedAt() {return updatedAt;} public void setUpdatedAt(OffsetDateTime updatedAt) {this.updatedAt = updatedAt;}
}
