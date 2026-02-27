package com.example.ecommerce.order;

import java.math.BigDecimal;
import java.util.List;

public class OrderPricing {
  public static BigDecimal total(List<OrderItem> items){
    return items.stream().map(i -> i.unitPrice.multiply(BigDecimal.valueOf(i.qty))).reduce(BigDecimal.ZERO, BigDecimal::add);
  }
}
