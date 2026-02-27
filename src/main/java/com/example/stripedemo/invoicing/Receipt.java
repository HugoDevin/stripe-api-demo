package com.example.stripedemo.invoicing;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "receipts")
public class Receipt {
    @Id
    private String id = UUID.randomUUID().toString();
    private String orderId;
    private String receiptNo;
    private OffsetDateTime issuedAt;
    public String getId() {return id;} public String getOrderId() {return orderId;} public void setOrderId(String orderId) {this.orderId = orderId;}
    public String getReceiptNo() {return receiptNo;} public void setReceiptNo(String receiptNo) {this.receiptNo = receiptNo;}
    public OffsetDateTime getIssuedAt() {return issuedAt;} public void setIssuedAt(OffsetDateTime issuedAt) {this.issuedAt = issuedAt;}
}
