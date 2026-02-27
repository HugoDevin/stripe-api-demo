package com.example.stripedemo.payment.webhook;

import org.springframework.data.jpa.repository.JpaRepository;

public interface WebhookEventRepository extends JpaRepository<WebhookEventEntity, String> {
    boolean existsByProviderEventId(String providerEventId);
}
