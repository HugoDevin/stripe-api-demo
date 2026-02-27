package com.example.stripedemo.common.idempotency;

import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class ConsumerIdempotencyService {
    private final ProcessedEventRepository repository;

    public ConsumerIdempotencyService(ProcessedEventRepository repository) { this.repository = repository; }

    public boolean alreadyProcessed(String eventId, String consumer) {
        if (repository.existsByEventIdAndConsumerName(eventId, consumer)) return true;
        ProcessedEvent e = new ProcessedEvent(); e.setEventId(eventId); e.setConsumerName(consumer); e.setProcessedAt(OffsetDateTime.now());
        repository.save(e);
        return false;
    }
}
