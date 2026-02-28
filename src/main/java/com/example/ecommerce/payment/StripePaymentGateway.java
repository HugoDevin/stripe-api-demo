package com.example.ecommerce.payment;

import com.example.ecommerce.order.OrderEntity;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component @Profile({"dev","staging","prod"})
public class StripePaymentGateway implements PaymentGateway {
  @Value("${stripe.secret-key:}") String secret;
  @PostConstruct void init(){Stripe.apiKey = secret;}
  public PaymentCreateResult createPayment(OrderEntity order){
    try {
      var params = PaymentIntentCreateParams.builder().setAmount(order.totalAmount.movePointRight(2).longValue()).setCurrency(order.currency.toLowerCase())
          .putMetadata("orderId", order.id.toString()).setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()).build();
      PaymentIntent pi = PaymentIntent.create(params);
      return new PaymentCreateResult(pi.getClientSecret(), pi.getId());
    } catch (StripeException e) { throw new RuntimeException(e); }
  }
  public void refund(String paymentId) {}
}
