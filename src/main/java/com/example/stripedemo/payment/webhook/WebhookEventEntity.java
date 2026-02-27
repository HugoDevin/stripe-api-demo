package com.example.stripedemo.payment.webhook;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "webhook_events")
public class WebhookEventEntity {
    @Id
    private String id = UUID.randomUUID().toString();
    private String providerEventId;
    private String type;
    @Column(columnDefinition = "TEXT")
    private String payloadJson;
    private OffsetDateTime receivedAt;
    public String getId() {return id;} public String getProviderEventId() {return providerEventId;} public void setProviderEventId(String providerEventId) {this.providerEventId = providerEventId;}
    public String getType() {return type;} public void setType(String type) {this.type = type;}
    public String getPayloadJson() {return payloadJson;} public void setPayloadJson(String payloadJson) {this.payloadJson = payloadJson;}
    public OffsetDateTime getReceivedAt() {return receivedAt;} public void setReceivedAt(OffsetDateTime receivedAt) {this.receivedAt = receivedAt;}
}
