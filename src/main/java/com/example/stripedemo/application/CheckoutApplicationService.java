package com.example.stripedemo.application;

import com.example.stripedemo.domain.order.OrderService;
import com.example.stripedemo.model.Order;
import com.example.stripedemo.service.PaymentService;
import com.example.stripedemo.service.ProductCatalogService;
import com.stripe.model.PaymentIntent;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CheckoutApplicationService {

    private final PaymentService paymentService;
    private final ProductCatalogService productCatalogService;
    private final OrderService orderService;

    public CheckoutApplicationService(
            PaymentService paymentService,
            ProductCatalogService productCatalogService,
            OrderService orderService
    ) {
        this.paymentService = paymentService;
        this.productCatalogService = productCatalogService;
        this.orderService = orderService;
    }

    public CheckoutResult createCheckout(String product) throws Exception {
        Long productPrice = productCatalogService.getProductPrice(product);
        if (productPrice == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown product: " + product);
        }

        long finalAmount = productPrice;
        PaymentIntent intent = paymentService.createPayment(finalAmount, "usd", product);
        Order order = orderService.createPendingOrder(product, finalAmount, "USD");

        return new CheckoutResult(intent.getClientSecret(), order.getId(), product, finalAmount, "USD");
    }

    public record CheckoutResult(String clientSecret, String orderId, String product, Long amount, String currency) {}
}
