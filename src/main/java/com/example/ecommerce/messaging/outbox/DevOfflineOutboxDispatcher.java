package com.example.ecommerce.messaging.outbox;

import com.example.ecommerce.messaging.consumer.PaymentEventConsumers;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("dev-offline")
public class DevOfflineOutboxDispatcher {
  private final OutboxRepository outbox;
  private final PaymentEventConsumers consumers;

  public DevOfflineOutboxDispatcher(OutboxRepository outbox, PaymentEventConsumers consumers) {
    this.outbox = outbox;
    this.consumers = consumers;
  }

  @Scheduled(fixedDelay = 1500)
  @Transactional
  public void dispatch() {
    for (OutboxEvent e : outbox.findTop50ByStatusOrderByCreatedAtAsc("PENDING")) {
      try {
        if ("payment.succeeded".equals(e.eventType)) {
          consumers.receipt(e.payloadJson);
          consumers.fulfill(e.payloadJson);
          consumers.notification(e.payloadJson);
          consumers.accounting(e.payloadJson);
          consumers.reporting(e.payloadJson);
        } else if ("payment.failed".equals(e.eventType)) {
          consumers.notification(e.payloadJson);
          consumers.inventory(e.payloadJson + " payment.failed");
        }
        e.status = "SENT";
        e.sentAt = java.time.Instant.now();
        outbox.save(e);
      } catch (Exception ignored) {}
    }
  }
}
