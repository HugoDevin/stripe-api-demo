package com.example.stripedemo.controller.api.dto;

public record CheckoutResponse(String clientSecret, String orderId, String product, Long amount, String currency) {}
