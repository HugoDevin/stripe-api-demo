package com.example.stripedemo.service;

import com.example.stripedemo.controller.api.dto.CardData;

public interface PaymentService {

    PaymentSession createPayment(Long amount, String currency, String description) throws Exception;

    PaymentSession payWithCard(String paymentIntentId, CardData cardData) throws Exception;

    record PaymentSession(String id, String clientSecret, String status, Long amount, String currency) {
    }
}
