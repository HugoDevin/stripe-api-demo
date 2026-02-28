package com.example.ecommerce.payment;

import com.example.ecommerce.common.error.AppException;
import com.example.ecommerce.messaging.PaymentEventPayload;
import com.example.ecommerce.messaging.outbox.OutboxEvent;
import com.example.ecommerce.messaging.outbox.OutboxRepository;
import com.example.ecommerce.order.*;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {
  private final PaymentRepository paymentRepository; private final OrderRepository orderRepository; private final PaymentGateway gateway; private final OutboxRepository outbox;
  public PaymentService(PaymentRepository p, OrderRepository o, PaymentGateway g, OutboxRepository outbox){this.paymentRepository=p;this.orderRepository=o;this.gateway=g;this.outbox=outbox;}

  @Transactional
  public PaymentEntity createPayment(UUID orderId){
    OrderEntity order = orderRepository.findById(orderId).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,"order not found"));
    var created = gateway.createPayment(order);
    PaymentEntity payment = paymentRepository.findByOrderId(orderId).orElseGet(PaymentEntity::new);
    payment.orderId=orderId; payment.provider = created.providerIntentId().startsWith("pi_fake")?"FAKE":"STRIPE"; payment.providerIntentId=created.providerIntentId(); payment.clientSecret=created.clientSecret(); payment.amount=order.totalAmount; payment.currency=order.currency; payment.status=PaymentStatus.INITIATED;
    return paymentRepository.save(payment);
  }

  @Transactional
  public void markSuccess(UUID orderId, String providerEventId){
    PaymentEntity p = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,"payment missing"));
    p.status=PaymentStatus.SUCCEEDED; paymentRepository.save(p);
    OrderEntity o = orderRepository.findById(orderId).orElseThrow(); o.status=OrderStatus.PAID; orderRepository.save(o);
    outbox.save(toOutbox("payment.succeeded", p, providerEventId));
  }
  @Transactional
  public void markFailed(UUID orderId, String providerEventId){
    PaymentEntity p = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,"payment missing"));
    p.status=PaymentStatus.FAILED; paymentRepository.save(p);
    OrderEntity o = orderRepository.findById(orderId).orElseThrow(); o.status=OrderStatus.CANCELED; orderRepository.save(o);
    outbox.save(toOutbox("payment.failed", p, providerEventId));
  }

  private OutboxEvent toOutbox(String type, PaymentEntity p, String eventId){
    PaymentEventPayload payload = new PaymentEventPayload(eventId, p.orderId, p.id, p.provider, p.providerIntentId, p.amount, p.currency, Instant.now());
    OutboxEvent o = new OutboxEvent(); o.eventType=type; o.aggregateId=p.orderId.toString();
    o.payloadJson = "{\"eventId\":\"%s\",\"orderId\":\"%s\",\"paymentId\":\"%s\",\"provider\":\"%s\",\"providerIntentId\":\"%s\",\"amount\":%s,\"currency\":\"%s\",\"occurredAt\":\"%s\"}"
        .formatted(payload.eventId(), payload.orderId(), payload.paymentId(), payload.provider(), payload.providerIntentId(), payload.amount(), payload.currency(), payload.occurredAt());
    return o;
  }
}
