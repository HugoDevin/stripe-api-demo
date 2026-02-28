package com.example.ecommerce.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface WebhookEventRepository extends JpaRepository<WebhookEventEntity, UUID> {
  boolean existsByProviderEventId(String providerEventId);
}
