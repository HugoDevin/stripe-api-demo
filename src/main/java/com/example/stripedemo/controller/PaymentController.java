package com.example.stripedemo.controller;

import com.example.stripedemo.model.Order;
import com.example.stripedemo.repository.OrderRepository;
import com.example.stripedemo.service.PaymentService;
import com.stripe.model.PaymentIntent;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Controller
public class PaymentController {

    private final PaymentService paymentService;
    private final OrderRepository orderRepository;

    public PaymentController(PaymentService paymentService, OrderRepository orderRepository) {
        this.paymentService = paymentService;
        this.orderRepository = orderRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        Map<String, Long> products = new LinkedHashMap<>();
        products.put("Book", 1000L);
        products.put("Pen", 500L);
        products.put("Notebook", 750L);

        long firstAmount = products.values().stream().findFirst().orElse(0L);
        model.addAttribute("firstAmount", firstAmount);
        model.addAttribute("products", products);
        return "index";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam String product, @RequestParam Long amount, Model model) throws Exception {
        PaymentIntent intent = paymentService.createPayment(amount, "usd", product);

        String orderId = UUID.randomUUID().toString();
        Order order = new Order();
        order.setId(orderId);
        order.setProduct(product);
        order.setAmount(amount);
        order.setCurrency("USD");
        order.setStatus("pending");
        orderRepository.save(order);

        model.addAttribute("clientSecret", intent.getClientSecret());
        model.addAttribute("orderId", orderId);
        model.addAttribute("product", product);
        model.addAttribute("amount", amount);
        return "checkout";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "orders";
    }

    @PostMapping("/webhook")
    @ResponseBody
    public String webhook(@RequestBody Map<String, Object> payload) {
        return "OK";
    }
}
