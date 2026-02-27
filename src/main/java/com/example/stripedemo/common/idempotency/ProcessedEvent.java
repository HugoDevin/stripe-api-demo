package com.example.stripedemo.common.idempotency;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "processed_events")
public class ProcessedEvent {
    @Id
    private String id = UUID.randomUUID().toString();
    private String eventId;
    private String consumerName;
    private OffsetDateTime processedAt;
    public String getId() {return id;} public String getEventId() {return eventId;} public void setEventId(String eventId) {this.eventId = eventId;}
    public String getConsumerName() {return consumerName;} public void setConsumerName(String consumerName) {this.consumerName = consumerName;}
    public OffsetDateTime getProcessedAt() {return processedAt;} public void setProcessedAt(OffsetDateTime processedAt) {this.processedAt = processedAt;}
}
