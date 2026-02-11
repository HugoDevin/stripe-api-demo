package com.example.stripedemo.controller;

import com.example.stripedemo.model.Order;
import com.example.stripedemo.service.PaymentService;
import com.example.stripedemo.service.ProductCatalogService;
import com.stripe.model.PaymentIntent;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.*;

@Controller
public class PaymentController {

    private final PaymentService paymentService;
    private final ProductCatalogService productCatalogService;
    private final Map<String, Order> orderDB = new HashMap<>();

    public PaymentController(PaymentService paymentService, ProductCatalogService productCatalogService) {
        this.paymentService = paymentService;
        this.productCatalogService = productCatalogService;
    }

    @GetMapping("/")
    public String index(Model model) {
        Map<String, Long> products = productCatalogService.getAllProducts();
        model.addAttribute("products", products);
        return "index";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam String product, Model model) throws Exception {
        Long productPrice = productCatalogService.getProductPrice(product);
        if (productPrice == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown product: " + product);
        }

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

    @PostMapping("/api/orders")
    @ResponseBody
    public ResponseEntity<CreateOrderResponse> createOrder(@RequestBody CreateOrderRequest request) throws Exception {
        if (request == null || request.product() == null || request.product().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "product is required");
        }

        String product = request.product().trim();
        Long productPrice = productCatalogService.getProductPrice(product);
        if (productPrice == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown product: " + product);
        }

        PaymentIntent intent = paymentService.createPayment(productPrice, "usd", product);
        String orderId = UUID.randomUUID().toString();

        Order order = new Order();
        order.setId(orderId);
        order.setProduct(product);
        order.setAmount(productPrice);
        order.setCurrency("USD");
        order.setStatus("pending");
        orderDB.put(orderId, order);

        URI location = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{orderId}")
                .buildAndExpand(orderId)
                .toUri();

        return ResponseEntity.created(location)
                .body(new CreateOrderResponse(order, intent.getClientSecret()));
    }

    @GetMapping("/api/orders")
    @ResponseBody
    public ResponseEntity<List<Order>> getOrders() {
        return ResponseEntity.ok(new ArrayList<>(orderDB.values()));
    }

    @GetMapping("/api/orders/{orderId}")
    @ResponseBody
    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {
        Order order = getOrderOrThrow(orderId);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/api/orders/{orderId}")
    @ResponseBody
    public ResponseEntity<Void> updateOrderStatus(@PathVariable String orderId, @RequestBody UpdateOrderStatusRequest request) {
        if (request == null || request.status() == null || request.status().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "status is required");
        }

        Order order = getOrderOrThrow(orderId);
        String normalizedStatus = request.status().trim().toLowerCase(Locale.ROOT);

        if (!Set.of("pending", "succeeded", "failed").contains(normalizedStatus)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported status: " + request.status());
        }

        order.setStatus(normalizedStatus);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/api/webhooks/stripe")
    @ResponseBody
    public ResponseEntity<Void> stripeWebhook(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.accepted().build();
    }

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseBody
    public ResponseEntity<ProblemDetail> handleResponseStatusException(ResponseStatusException ex, HttpServletRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(ex.getStatusCode(), ex.getReason());
        problemDetail.setType(URI.create("about:blank"));
        problemDetail.setTitle(ex.getStatusCode().toString());
        problemDetail.setProperty("path", request.getRequestURI());

        return ResponseEntity.status(ex.getStatusCode())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
                .body(problemDetail);
    }

    private Order getOrderOrThrow(String orderId) {
        Order order = orderDB.get(orderId);
        if (order == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + orderId);
        }
        return order;
    }

    public record CreateOrderRequest(String product) {}

    public record UpdateOrderStatusRequest(String status) {}

    public record CreateOrderResponse(Order order, String clientSecret) {}
}
