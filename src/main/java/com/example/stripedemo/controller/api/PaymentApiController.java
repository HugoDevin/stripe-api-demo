package com.example.stripedemo.controller.api;

import com.example.stripedemo.application.CheckoutApplicationService;
import com.example.stripedemo.controller.api.dto.*;
import com.example.stripedemo.domain.order.OrderService;
import com.example.stripedemo.model.Order;
import com.example.stripedemo.service.ProductCatalogService;
import com.example.stripedemo.service.RequestEncryptionService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final RequestEncryptionService requestEncryptionService;
    private final ObjectMapper objectMapper;
    private final String stripePublishableKey;

    public PaymentApiController(
            ProductCatalogService productCatalogService,
            CheckoutApplicationService checkoutApplicationService,
            OrderService orderService,
            RequestEncryptionService requestEncryptionService,
            ObjectMapper objectMapper,
            @Value("${stripe.publishable-key}") String stripePublishableKey
    ) {
        this.productCatalogService = productCatalogService;
        this.checkoutApplicationService = checkoutApplicationService;
        this.orderService = orderService;
        this.requestEncryptionService = requestEncryptionService;
        this.objectMapper = objectMapper;
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

    @GetMapping("/security/public-key")
    public PublicKeyResponse publicKey() {
        return new PublicKeyResponse("RSA-OAEP-256", requestEncryptionService.getPublicKeyBase64());
    }

    @PostMapping("/checkout")
    public CheckoutResponse checkout(@RequestBody CheckoutRequest request) throws Exception {
        CheckoutApplicationService.CheckoutResult result = checkoutApplicationService.createCheckout(request.product(), request.customerId());
        return new CheckoutResponse(
                result.clientSecret(),
                result.orderId(),
                result.product(),
                result.amount(),
                result.currency()
        );
    }

    @PostMapping("/orders/{orderId}/pay-encrypted")
    public OrderStatusResponse payEncrypted(@PathVariable String orderId, @RequestBody EncryptedCardRequest request) throws Exception {
        String decrypted = requestEncryptionService.decryptBase64(request.encryptedData());
        CardData cardData = objectMapper.readValue(decrypted, CardData.class);
        Order order = checkoutApplicationService.payOrderWithCard(orderId, cardData);
        return new OrderStatusResponse(order.getStatus());
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
