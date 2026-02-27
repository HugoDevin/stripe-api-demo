package com.example.stripedemo.payment.gateway;

import com.example.stripedemo.order.Order;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"prod", "staging", "default"})
public class StripePaymentGateway implements PaymentGateway {
    public StripePaymentGateway(@Value("${stripe.secret-key:}") String secretKey) { Stripe.apiKey = secretKey; }
    @Override
    public PaymentCreateResult createPayment(Order order) throws Exception {
        PaymentIntent pi = PaymentIntent.create(PaymentIntentCreateParams.builder()
                .setAmount(order.getTotalAmount())
                .setCurrency(order.getCurrency().toLowerCase())
                .putMetadata("orderId", order.getId())
                .build());
        return new PaymentCreateResult(pi.getId(), pi.getClientSecret(), "STRIPE");
    }
    @Override
    public void refund(String paymentId) { }
}
