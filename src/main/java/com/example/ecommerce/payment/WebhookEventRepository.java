package com.example.ecommerce.payment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.UUID;

public interface WebhookEventRepository extends JpaRepository<WebhookEventEntity, UUID> {
  boolean existsByProviderEventId(String providerEventId);

  @Modifying
  @Query(value = """
      insert into webhook_events (id, provider_event_id, type, payload_json, received_at)
      values (:id, :providerEventId, :type, :payloadJson, now())
      on conflict (provider_event_id) do nothing
      """, nativeQuery = true)
  int insertIgnore(@Param("id") UUID id,
      @Param("providerEventId") String providerEventId,
      @Param("type") String type,
      @Param("payloadJson") String payloadJson);
}
