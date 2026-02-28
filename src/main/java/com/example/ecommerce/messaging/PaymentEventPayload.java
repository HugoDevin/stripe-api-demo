package com.example.ecommerce.messaging;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentEventPayload(String eventId, UUID orderId, UUID paymentId, String provider, String providerIntentId,
                                  BigDecimal amount, String currency, Instant occurredAt) {}
