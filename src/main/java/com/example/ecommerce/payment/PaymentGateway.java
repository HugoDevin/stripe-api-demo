package com.example.ecommerce.payment;

import com.example.ecommerce.order.OrderEntity;

public interface PaymentGateway {
  PaymentCreateResult createPayment(OrderEntity order);
  void refund(String paymentId);
}
