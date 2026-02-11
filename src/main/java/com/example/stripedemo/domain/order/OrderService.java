package com.example.stripedemo.domain.order;

import com.example.stripedemo.model.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderService {

    private final Map<String, Order> orderDB = new ConcurrentHashMap<>();

    public Order createPendingOrder(String product, long amount, String currency) {
        String orderId = UUID.randomUUID().toString();
        Order order = new Order();
        order.setId(orderId);
        order.setProduct(product);
        order.setAmount(amount);
        order.setCurrency(currency);
        order.setStatus("pending");
        orderDB.put(orderId, order);
        return order;
    }

    public Order completeOrder(String orderId) {
        Order order = orderDB.get(orderId);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + orderId);
        }
        order.setStatus("succeeded");
        return order;
    }

    public List<Order> getAllOrders() {
        return orderDB.values().stream().toList();
    }
}
