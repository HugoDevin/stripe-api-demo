package com.example.stripedemo.controller;

import com.example.stripedemo.model.Order;
import com.example.stripedemo.service.PaymentService;
import com.stripe.model.PaymentIntent;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.*;

@Controller
public class PaymentController {

    private final PaymentService paymentService;
    private final Map<String, Order> orderDB = new HashMap<>();
    private static final Map<String, Long> PRODUCT_CATALOG = Map.of(
            "Book", 1000L,
            "Pen", 500L,
            "Notebook", 750L
    );

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
        Map<String, Long> products = new LinkedHashMap<>(PRODUCT_CATALOG);
        model.addAttribute("products", products);
        return "index";
    }


    @PostMapping("/checkout")
    public String checkout(@RequestParam String product, Model model) throws Exception {
        Long productPrice = PRODUCT_CATALOG.get(product);
        if (productPrice == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown product: " + product);
        }

        // 以前端金額為參考，伺服器仍以商品目錄定價為準，避免金額遭竄改。
        long finalAmount = productPrice;

        PaymentIntent intent = paymentService.createPayment(finalAmount, "usd", product);
        String orderId = UUID.randomUUID().toString();
        Order order = new Order();
        order.setId(orderId);
        order.setProduct(product);
        order.setAmount(finalAmount);
        order.setCurrency("USD");
        order.setStatus("pending");
        orderDB.put(orderId, order);

        model.addAttribute("clientSecret", intent.getClientSecret());
        model.addAttribute("orderId", orderId);
        model.addAttribute("product", product);
        model.addAttribute("amount", finalAmount);
        return "checkout";
    }

    @PostMapping("/orders/{orderId}/complete")
    @ResponseBody
    public String completeOrder(@PathVariable String orderId) {
        Order order = orderDB.get(orderId);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + orderId);
        }
        order.setStatus("succeeded");
        return "OK";
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
