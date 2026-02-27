package com.example.stripedemo.messaging.outbox;

import com.example.stripedemo.messaging.RabbitTopologyConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;

@Component
public class OutboxPublisherJob {
    private final OutboxRepository outboxRepository;
    private final RabbitTemplate rabbitTemplate;

    public OutboxPublisherJob(OutboxRepository outboxRepository, RabbitTemplate rabbitTemplate) {
        this.outboxRepository = outboxRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Scheduled(fixedDelay = 1500)
    public void publish() {
        for (OutboxEvent e : outboxRepository.findTop100ByStatusOrderByCreatedAtAsc(OutboxStatus.PENDING)) {
            String key = "PaymentSucceeded".equals(e.getEventType()) ? "payment.succeeded" : "payment.failed";
            rabbitTemplate.convertAndSend(RabbitTopologyConfig.EXCHANGE, key, e.getPayloadJson());
            e.setStatus(OutboxStatus.SENT); e.setSentAt(OffsetDateTime.now()); outboxRepository.save(e);
        }
    }
}
