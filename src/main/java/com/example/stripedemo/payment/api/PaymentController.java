package com.example.stripedemo.payment.api;

import com.example.stripedemo.payment.Payment;
import com.example.stripedemo.payment.PaymentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {
    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) { this.paymentService = paymentService; }

    @PostMapping("/create")
    public CreatePaymentResponse create(@RequestParam String orderId) throws Exception {
        Payment p = paymentService.createPayment(orderId);
        return new CreatePaymentResponse(p.getId(), p.getClientSecret());
    }
    @GetMapping("/{id}") public Payment get(@PathVariable String id) { return paymentService.getById(id); }

    public record CreatePaymentResponse(String paymentId, String clientSecret) {}
}
