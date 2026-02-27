package com.example.stripedemo.service;

import com.example.stripedemo.controller.api.dto.CardData;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Profile("dev")
public class MockPaymentService implements PaymentService {

    private final Map<String, PaymentSession> sessions = new ConcurrentHashMap<>();

    @Override
    public PaymentSession createPayment(Long amount, String currency, String description) {
        String paymentIntentId = "pi_dev_" + UUID.randomUUID().toString().replace("-", "");
        String clientSecret = paymentIntentId + "_secret_dev";
        PaymentSession created = new PaymentSession(paymentIntentId, clientSecret, "requires_confirmation", amount, currency);
        sessions.put(paymentIntentId, created);
        return created;
    }

    @Override
    public PaymentSession payWithCard(String paymentIntentId, CardData cardData) {
        PaymentSession created = sessions.get(paymentIntentId);
        if (created == null) {
            return new PaymentSession(paymentIntentId, paymentIntentId + "_secret_dev", "failed", 0L, "unknown");
        }
        return new PaymentSession(paymentIntentId, created.clientSecret(), "succeeded", created.amount(), created.currency());
    }
}
