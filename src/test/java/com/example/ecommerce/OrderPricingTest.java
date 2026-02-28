package com.example.ecommerce;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.ecommerce.order.OrderItem;
import com.example.ecommerce.order.OrderPricing;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class OrderPricingTest {
  @Test void calculatesTotal(){
    OrderItem a = new OrderItem(); a.unitPrice = new BigDecimal("10.00"); a.qty = 2;
    OrderItem b = new OrderItem(); b.unitPrice = new BigDecimal("5.50"); b.qty = 1;
    assertEquals(new BigDecimal("25.50"), OrderPricing.total(List.of(a,b)));
  }
}
