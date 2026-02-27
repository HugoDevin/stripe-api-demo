package com.example.stripedemo.payment.webhook;

import com.example.stripedemo.payment.PaymentService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/stripe")
@Profile({"prod", "staging", "default"})
public class StripeWebhookController {
    private final String webhookSecret;
    private final WebhookEventRepository repo;
    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    public StripeWebhookController(@Value("${stripe.webhook-secret:}") String webhookSecret, WebhookEventRepository repo, PaymentService paymentService, ObjectMapper objectMapper) {
        this.webhookSecret = webhookSecret;
        this.repo = repo;
        this.paymentService = paymentService;
        this.objectMapper = objectMapper;
    }

    @PostMapping("/webhook") @Transactional
    public String webhook(@RequestHeader("Stripe-Signature") String sig, @RequestBody String payload) throws Exception {
        Event event;
        try { event = Webhook.constructEvent(payload, sig, webhookSecret); }
        catch (SignatureVerificationException e) { throw new RuntimeException("invalid signature"); }
        if (repo.existsByProviderEventId(event.getId())) return "OK";

        WebhookEventEntity we = new WebhookEventEntity();
        we.setProviderEventId(event.getId()); we.setType(event.getType()); we.setPayloadJson(payload); we.setReceivedAt(OffsetDateTime.now()); repo.save(we);

        JsonNode root = objectMapper.readTree(payload);
        String intentId = root.at("/data/object/id").asText();
        if ("payment_intent.succeeded".equals(event.getType())) paymentService.onPaymentSucceeded(event.getId(), intentId);
        if ("payment_intent.payment_failed".equals(event.getType())) paymentService.onPaymentFailed(event.getId(), intentId);
        return "OK";
    }
}
