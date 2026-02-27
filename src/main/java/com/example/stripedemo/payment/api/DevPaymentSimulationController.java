package com.example.stripedemo.payment.api;

import com.example.stripedemo.common.error.ApiException;
import com.example.stripedemo.payment.Payment;
import com.example.stripedemo.payment.PaymentRepository;
import com.example.stripedemo.payment.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/internal/payments")
@Profile("dev-offline")
public class DevPaymentSimulationController {
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final String token;

    public DevPaymentSimulationController(PaymentRepository paymentRepository, PaymentService paymentService, @Value("${dev.internal-token}") String token) {
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
        this.token = token;
    }

    @PostMapping("/{orderId}/simulate-success")
    public String success(@PathVariable String orderId, @RequestHeader("X-DEV-TOKEN") String headerToken) {
        guard(headerToken);
        Payment p = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new ApiException(404, "payment not found"));
        paymentService.onPaymentSucceeded("dev-" + UUID.randomUUID(), p.getProviderIntentId());
        return "OK";
    }

    @PostMapping("/{orderId}/simulate-fail")
    public String fail(@PathVariable String orderId, @RequestHeader("X-DEV-TOKEN") String headerToken) {
        guard(headerToken);
        Payment p = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new ApiException(404, "payment not found"));
        paymentService.onPaymentFailed("dev-" + UUID.randomUUID(), p.getProviderIntentId());
        return "OK";
    }

    private void guard(String headerToken) { if (!token.equals(headerToken)) throw new ApiException(401, "unauthorized"); }
}
