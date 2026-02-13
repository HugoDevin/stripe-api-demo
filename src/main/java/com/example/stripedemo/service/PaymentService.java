package com.example.stripedemo.service;

import com.example.stripedemo.controller.api.dto.CardData;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.param.PaymentIntentConfirmParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public PaymentService(@Value("${stripe.secret-key}") String secretKey) {
        Stripe.apiKey = secretKey;
    }

    public PaymentIntent createPayment(Long amount, String currency, String description) throws Exception {
        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amount)
                .setCurrency(currency)
                .setDescription(description)
                .build();
        return PaymentIntent.create(params);
    }

    public PaymentIntent payWithCard(String paymentIntentId, CardData cardData) throws Exception {
        PaymentMethodCreateParams paymentMethodParams = PaymentMethodCreateParams.builder()
                .setType(PaymentMethodCreateParams.Type.CARD)
                .setCard(
                        PaymentMethodCreateParams.CardDetails.builder()
                                .setNumber(cardData.number())
                                .setExpMonth(cardData.expMonth())
                                .setExpYear(cardData.expYear())
                                .setCvc(cardData.cvc())
                                .build()
                )
                .build();

        PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodParams);

        PaymentIntentConfirmParams confirmParams = PaymentIntentConfirmParams.builder()
                .setPaymentMethod(paymentMethod.getId())
                .build();

        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        return paymentIntent.confirm(confirmParams);
    }
}
