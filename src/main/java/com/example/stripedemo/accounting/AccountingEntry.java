package com.example.stripedemo.accounting;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "accounting_entries")
public class AccountingEntry {
    @Id
    private String id = UUID.randomUUID().toString();
    private String orderId;
    private String debitAccount;
    private String creditAccount;
    private Long amount;
    private String currency;
    private OffsetDateTime createdAt;
    public String getId() {return id;} public String getOrderId() {return orderId;} public void setOrderId(String orderId) {this.orderId = orderId;}
    public String getDebitAccount() {return debitAccount;} public void setDebitAccount(String debitAccount) {this.debitAccount = debitAccount;}
    public String getCreditAccount() {return creditAccount;} public void setCreditAccount(String creditAccount) {this.creditAccount = creditAccount;}
    public Long getAmount() {return amount;} public void setAmount(Long amount) {this.amount = amount;}
    public String getCurrency() {return currency;} public void setCurrency(String currency) {this.currency = currency;}
    public OffsetDateTime getCreatedAt() {return createdAt;} public void setCreatedAt(OffsetDateTime createdAt) {this.createdAt = createdAt;}
}
