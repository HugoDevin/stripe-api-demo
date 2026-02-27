package com.example.stripedemo.messaging.outbox;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
public class OutboxEvent {
    @Id
    private String id = UUID.randomUUID().toString();
    private String eventType;
    private String aggregateId;
    @Column(columnDefinition = "TEXT")
    private String payloadJson;
    @Enumerated(EnumType.STRING)
    private OutboxStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime sentAt;
    public String getId() {return id;} public String getEventType() {return eventType;} public void setEventType(String eventType) {this.eventType = eventType;}
    public String getAggregateId() {return aggregateId;} public void setAggregateId(String aggregateId) {this.aggregateId = aggregateId;}
    public String getPayloadJson() {return payloadJson;} public void setPayloadJson(String payloadJson) {this.payloadJson = payloadJson;}
    public OutboxStatus getStatus() {return status;} public void setStatus(OutboxStatus status) {this.status = status;}
    public OffsetDateTime getCreatedAt() {return createdAt;} public void setCreatedAt(OffsetDateTime createdAt) {this.createdAt = createdAt;}
    public OffsetDateTime getSentAt() {return sentAt;} public void setSentAt(OffsetDateTime sentAt) {this.sentAt = sentAt;}
}
