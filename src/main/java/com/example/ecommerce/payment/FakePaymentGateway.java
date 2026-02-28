package com.example.ecommerce.payment;

import com.example.ecommerce.order.OrderEntity;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component @Profile("!dev & !staging & !prod")
public class FakePaymentGateway implements PaymentGateway {
  public PaymentCreateResult createPayment(OrderEntity order){
    return new PaymentCreateResult("fake_cs_"+order.id, "pi_fake_"+UUID.randomUUID());
  }
  public void refund(String paymentId) {}
}
