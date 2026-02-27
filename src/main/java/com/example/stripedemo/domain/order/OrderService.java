package com.example.stripedemo.domain.order;

import com.example.stripedemo.model.Order;
import com.example.stripedemo.repository.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createPendingOrder(
            String product,
            long amount,
            String currency,
            String paymentIntentId,
            String customerId
    ) {
        String orderId = UUID.randomUUID().toString();
        Order order = new Order();
        order.setId(orderId);
        order.setProduct(product);
        order.setAmount(amount);
        order.setCurrency(currency);
        order.setStatus("pending");
        order.setPaymentIntentId(paymentIntentId);
        order.setCustomerId(customerId);
        order.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        return orderRepository.save(order);
    }

    public Order completeOrder(String orderId) {
        Order order = getOrder(orderId);
        order.setStatus("succeeded");
        return orderRepository.save(order);
    }

    public Order getOrder(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + orderId));
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
}
