package com.example.ecommerce.messaging.outbox;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPublisher {
  private final OutboxRepository repo; private final RabbitTemplate rabbit;
  public OutboxPublisher(OutboxRepository r, RabbitTemplate rabbit){repo=r;this.rabbit=rabbit;}
  @Scheduled(fixedDelay = 1500)
  @Transactional
  public void publish(){
    for (OutboxEvent e: repo.findTop50ByStatusOrderByCreatedAtAsc("PENDING")){
      try { rabbit.convertAndSend("domain.events", e.eventType, e.payloadJson); e.status="SENT"; e.sentAt=java.time.Instant.now(); repo.save(e);} catch (Exception ignore) {}
    }
  }
}
