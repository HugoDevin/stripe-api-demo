package com.example.stripedemo.application;

import com.example.stripedemo.controller.api.dto.CardData;
import com.example.stripedemo.domain.order.OrderService;
import com.example.stripedemo.model.Order;
import com.example.stripedemo.service.PaymentService;
import com.example.stripedemo.service.ProductCatalogService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class CheckoutApplicationService {

    private final PaymentService paymentService;
    private final ProductCatalogService productCatalogService;
    private final OrderService orderService;
    private final String paymentCurrency;

    public CheckoutApplicationService(
            PaymentService paymentService,
            ProductCatalogService productCatalogService,
            OrderService orderService,
            @Value("${app.payment.currency:usd}") String paymentCurrency
    ) {
        this.paymentService = paymentService;
        this.productCatalogService = productCatalogService;
        this.orderService = orderService;
        this.paymentCurrency = paymentCurrency;
    }

    public CheckoutResult createCheckout(String product, String customerId) throws Exception {
        Long productPrice = productCatalogService.getProductPrice(product);
        if (productPrice == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unknown product: " + product);
        }

        long finalAmount = productPrice;
        String normalizedCurrency = paymentCurrency.toLowerCase(Locale.ROOT);
        PaymentService.PaymentSession intent = paymentService.createPayment(finalAmount, normalizedCurrency, product);
        String normalizedCustomerId = (customerId == null || customerId.isBlank()) ? "guest" : customerId;
        Order order = orderService.createPendingOrder(
                product,
                finalAmount,
                normalizedCurrency.toUpperCase(Locale.ROOT),
                intent.id(),
                normalizedCustomerId
        );

        return new CheckoutResult(intent.clientSecret(), order.getId(), product, finalAmount, order.getCurrency());
    }

    public Order payOrderWithCard(String orderId, CardData cardData) throws Exception {
        Order order = orderService.getOrder(orderId);
        PaymentService.PaymentSession paymentIntent = paymentService.payWithCard(order.getPaymentIntentId(), cardData);

        if (!"succeeded".equals(paymentIntent.status())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment failed with status: " + paymentIntent.status());
        }

        boolean amountMismatched = paymentIntent.amount() != null && !order.getAmount().equals(paymentIntent.amount());
        boolean currencyMismatched = paymentIntent.currency() != null
                && !order.getCurrency().equalsIgnoreCase(paymentIntent.currency());
        if (amountMismatched || currencyMismatched) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment amount/currency mismatch");
        }

        return orderService.completeOrder(orderId);
    }

    public record CheckoutResult(String clientSecret, String orderId, String product, Long amount, String currency) {}
}
