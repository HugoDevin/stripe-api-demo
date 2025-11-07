package com.example.stripedemo.controller;

import com.example.stripedemo.model.Order;
import com.example.stripedemo.service.PaymentService;
import com.stripe.model.PaymentIntent;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class PaymentController {

    private final PaymentService paymentService;
    private final Map<String, Order> orderDB = new HashMap<>();

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

//    @GetMapping("/")
//    public String index(Model model) {
//        Map<String, Long> products = Map.of("Book", 1000L, "Pen", 500L);
//        model.addAttribute("products", products);
//        return "index";
//    }

    @GetMapping("/")
    public String index(Model model) {
        // 商品名稱 -> 金額 (以 cents 為單位)
        Map<String, Long> products = new LinkedHashMap<>();
        products.put("Book", 1000L); // $10.00
        products.put("Pen", 500L);   // $5.00
        products.put("Notebook", 750L); // $7.50
        // 直接取第一個商品的金額，傳給模板
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
        orderDB.put(orderId, order);

        model.addAttribute("clientSecret", intent.getClientSecret());
        model.addAttribute("orderId", orderId);
        model.addAttribute("product", product);
        model.addAttribute("amount", amount);
        return "checkout";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderDB.values());
        return "orders";
    }

    @PostMapping("/webhook")
    @ResponseBody
    public String webhook(@RequestBody Map<String, Object> payload) {
        return "OK";
    }
}
