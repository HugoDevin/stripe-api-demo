package com.example.stripedemo.payment;

import com.example.stripedemo.common.error.ApiException;
import com.example.stripedemo.messaging.outbox.OutboxEvent;
import com.example.stripedemo.messaging.outbox.OutboxRepository;
import com.example.stripedemo.messaging.outbox.OutboxStatus;
import com.example.stripedemo.order.Order;
import com.example.stripedemo.order.OrderService;
import com.example.stripedemo.payment.gateway.PaymentGateway;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Map;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final PaymentGateway gateway;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public PaymentService(PaymentRepository paymentRepository, OrderService orderService, PaymentGateway gateway, OutboxRepository outboxRepository, ObjectMapper objectMapper) {
        this.paymentRepository = paymentRepository;
        this.orderService = orderService;
        this.gateway = gateway;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Payment createPayment(String orderId) throws Exception {
        Order order = orderService.getOrder(orderId);
        Payment existing = paymentRepository.findByOrderId(orderId).orElse(null);
        if (existing != null) return existing;
        PaymentGateway.PaymentCreateResult result = gateway.createPayment(order);
        Payment p = new Payment();
        p.setOrderId(orderId); p.setProvider(result.provider()); p.setProviderIntentId(result.providerIntentId());
        p.setStatus(PaymentStatus.INITIATED); p.setAmount(order.getTotalAmount()); p.setCurrency(order.getCurrency());
        p.setClientSecret(result.clientSecret()); p.setCreatedAt(OffsetDateTime.now()); p.setUpdatedAt(OffsetDateTime.now());
        return paymentRepository.save(p);
    }

    public Payment getById(String id) { return paymentRepository.findById(id).orElseThrow(() -> new ApiException(404, "payment not found")); }

    @Transactional
    public void onPaymentSucceeded(String providerEventId, String providerIntentId) {
        Payment p = paymentRepository.findByProviderIntentId(providerIntentId).orElseThrow(() -> new ApiException(404, "payment intent missing"));
        p.setStatus(PaymentStatus.SUCCEEDED); p.setUpdatedAt(OffsetDateTime.now()); paymentRepository.save(p);
        orderService.markPaid(p.getOrderId());
        writeOutbox("PaymentSucceeded", p.getOrderId(), providerEventId, p);
    }

    @Transactional
    public void onPaymentFailed(String providerEventId, String providerIntentId) {
        Payment p = paymentRepository.findByProviderIntentId(providerIntentId).orElseThrow(() -> new ApiException(404, "payment intent missing"));
        p.setStatus(PaymentStatus.FAILED); p.setUpdatedAt(OffsetDateTime.now()); paymentRepository.save(p);
        orderService.cancelOrder(p.getOrderId());
        writeOutbox("PaymentFailed", p.getOrderId(), providerEventId, p);
    }

    private void writeOutbox(String type, String aggregateId, String eventId, Payment p) {
        try {
            String json = objectMapper.writeValueAsString(Map.of(
                    "eventId", eventId,
                    "orderId", p.getOrderId(),
                    "paymentId", p.getId(),
                    "provider", p.getProvider(),
                    "providerIntentId", p.getProviderIntentId(),
                    "amount", p.getAmount(),
                    "currency", p.getCurrency(),
                    "occurredAt", OffsetDateTime.now().toString()
            ));
            OutboxEvent ob = new OutboxEvent();
            ob.setEventType(type); ob.setAggregateId(aggregateId); ob.setPayloadJson(json); ob.setStatus(OutboxStatus.PENDING); ob.setCreatedAt(OffsetDateTime.now());
            outboxRepository.save(ob);
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
