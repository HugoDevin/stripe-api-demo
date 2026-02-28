package com.example.ecommerce.payment;

public record PaymentCreateResult(String clientSecret, String providerIntentId) {}
