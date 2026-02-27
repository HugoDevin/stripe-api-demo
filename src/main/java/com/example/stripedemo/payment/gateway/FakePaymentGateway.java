package com.example.stripedemo.payment.gateway;

import com.example.stripedemo.order.Order;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Profile("dev-offline")
public class FakePaymentGateway implements PaymentGateway {
    @Override
    public PaymentCreateResult createPayment(Order order) {
        String id = "fake_pi_" + UUID.randomUUID();
        return new PaymentCreateResult(id, id + "_secret", "FAKE");
    }
    @Override
    public void refund(String paymentId) { }
}
