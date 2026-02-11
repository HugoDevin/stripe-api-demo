package com.example.stripedemo.controller.api;

import com.example.stripedemo.application.CheckoutApplicationService;
import com.example.stripedemo.controller.api.dto.CheckoutRequest;
import com.example.stripedemo.controller.api.dto.CheckoutResponse;
import com.example.stripedemo.controller.api.dto.ConfigResponse;
import com.example.stripedemo.controller.api.dto.OrderStatusResponse;
import com.example.stripedemo.controller.api.dto.ProductResponse;
import com.example.stripedemo.domain.order.OrderService;
import com.example.stripedemo.model.Order;
import com.example.stripedemo.service.ProductCatalogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "${app.frontend-origin:http://localhost:5173}")
public class PaymentApiController {

    private final ProductCatalogService productCatalogService;
    private final CheckoutApplicationService checkoutApplicationService;
    private final OrderService orderService;
    private final String stripePublishableKey;

    public PaymentApiController(
            ProductCatalogService productCatalogService,
            CheckoutApplicationService checkoutApplicationService,
            OrderService orderService,
            @Value("${stripe.publishable-key}") String stripePublishableKey
    ) {
        this.productCatalogService = productCatalogService;
        this.checkoutApplicationService = checkoutApplicationService;
        this.orderService = orderService;
        this.stripePublishableKey = stripePublishableKey;
    }

    @GetMapping("/products")
    public List<ProductResponse> products() {
        return productCatalogService.getAllProducts()
                .entrySet()
                .stream()
                .map(entry -> new ProductResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    @GetMapping("/config")
    public ConfigResponse config() {
        return new ConfigResponse(stripePublishableKey);
    }

    @PostMapping("/checkout")
    public CheckoutResponse checkout(@RequestBody CheckoutRequest request) throws Exception {
        CheckoutApplicationService.CheckoutResult result = checkoutApplicationService.createCheckout(request.product());
        return new CheckoutResponse(
                result.clientSecret(),
                result.orderId(),
                result.product(),
                result.amount(),
                result.currency()
        );
    }

    @PostMapping("/orders/{orderId}/complete")
    public OrderStatusResponse completeOrder(@PathVariable String orderId) {
        Order order = orderService.completeOrder(orderId);
        return new OrderStatusResponse(order.getStatus());
    }

    @GetMapping("/orders")
    public List<Order> orders() {
        return orderService.getAllOrders();
    }

    @PostMapping("/webhook")
    public String webhook(@RequestBody Map<String, Object> payload) {
        return "OK";
    }
}
