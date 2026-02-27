package com.example.stripedemo.notification;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "notifications")
public class NotificationEntity {
    @Id
    private String id = UUID.randomUUID().toString();
    private String orderId;
    private String channel;
    private String toAddr;
    private String subject;
    private String body;
    private String status;
    private OffsetDateTime sentAt;
    private OffsetDateTime createdAt;
    public String getId() {return id;} public String getOrderId() {return orderId;} public void setOrderId(String orderId) {this.orderId = orderId;}
    public String getChannel() {return channel;} public void setChannel(String channel) {this.channel = channel;}
    public String getToAddr() {return toAddr;} public void setToAddr(String toAddr) {this.toAddr = toAddr;}
    public String getSubject() {return subject;} public void setSubject(String subject) {this.subject = subject;}
    public String getBody() {return body;} public void setBody(String body) {this.body = body;}
    public String getStatus() {return status;} public void setStatus(String status) {this.status = status;}
    public OffsetDateTime getSentAt() {return sentAt;} public void setSentAt(OffsetDateTime sentAt) {this.sentAt = sentAt;}
    public OffsetDateTime getCreatedAt() {return createdAt;} public void setCreatedAt(OffsetDateTime createdAt) {this.createdAt = createdAt;}
}
