package com.example.stripedemo.payment.gateway;

import com.example.stripedemo.order.Order;

public interface PaymentGateway {
    PaymentCreateResult createPayment(Order order) throws Exception;
    void refund(String paymentId) throws Exception;

    record PaymentCreateResult(String providerIntentId, String clientSecret, String provider) {}
}
