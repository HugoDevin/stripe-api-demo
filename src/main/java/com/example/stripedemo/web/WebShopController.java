package com.example.stripedemo.web;

import com.example.stripedemo.catalog.Product;
import com.example.stripedemo.catalog.ProductRepository;
import com.example.stripedemo.common.error.ApiException;
import com.example.stripedemo.order.Order;
import com.example.stripedemo.order.OrderService;
import com.example.stripedemo.order.OrderRepository;
import com.example.stripedemo.payment.Payment;
import com.example.stripedemo.payment.PaymentRepository;
import com.example.stripedemo.payment.PaymentService;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/web")
@Validated
public class WebShopController {

    private final ProductRepository productRepository;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final String stripePublishableKey;
    private final String activeProfile;

    public WebShopController(
            ProductRepository productRepository,
            OrderService orderService,
            PaymentService paymentService,
            PaymentRepository paymentRepository,
            OrderRepository orderRepository,
            @Value("${stripe.publishable-key:}") String stripePublishableKey,
            @Value("${spring.profiles.active:default}") String activeProfile
    ) {
        this.productRepository = productRepository;
        this.orderService = orderService;
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        this.stripePublishableKey = stripePublishableKey;
        this.activeProfile = activeProfile;
    }

    @GetMapping
    public String index(Model model) {
        List<Product> products = productRepository.findAll().stream().filter(Product::isActive).toList();
        model.addAttribute("products", products);
        return "shop";
    }

    @PostMapping("/orders")
    public String createOrder(
            @RequestParam @Email @NotBlank String customerEmail,
            @RequestParam @NotBlank String sku,
            @RequestParam(defaultValue = "1") @Min(1) Long qty
    ) {
        Order order = orderService.createOrder(new OrderService.CreateOrderRequest(customerEmail, List.of(new OrderService.CreateOrderItem(sku, qty))));
        return "redirect:/web/orders/" + order.getId();
    }

    @GetMapping("/orders/{orderId}")
    public String orderDetail(@PathVariable String orderId, Model model) {
        Order order = orderService.getOrder(orderId);
        model.addAttribute("order", order);
        model.addAttribute("payment", paymentRepository.findByOrderId(orderId).orElse(null));
        model.addAttribute("devOffline", activeProfile.contains("dev-offline"));
        return "order-detail";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderRepository.findAll());
        return "orders";
    }

    @PostMapping("/payments/create")
    public String createPayment(@RequestParam String orderId) throws Exception {
        Payment payment = paymentService.createPayment(orderId);
        return "redirect:/web/payments/" + payment.getId();
    }

    @GetMapping("/payments/{paymentId}")
    public String paymentPage(@PathVariable String paymentId, Model model) {
        Payment payment = paymentService.getById(paymentId);
        Order order = orderService.getOrder(payment.getOrderId());
        model.addAttribute("payment", payment);
        model.addAttribute("order", order);
        model.addAttribute("publishableKey", stripePublishableKey);
        model.addAttribute("devOffline", activeProfile.contains("dev-offline"));
        return "payment";
    }

    @PostMapping("/payments/{orderId}/simulate-success")
    public String simulateSuccess(@PathVariable String orderId, RedirectAttributes redirectAttributes) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new ApiException(404, "payment not found"));
        paymentService.onPaymentSucceeded("web-dev-" + UUID.randomUUID(), payment.getProviderIntentId());
        redirectAttributes.addFlashAttribute("message", "Simulated payment success");
        return "redirect:/web/orders/" + orderId;
    }

    @PostMapping("/payments/{orderId}/simulate-fail")
    public String simulateFail(@PathVariable String orderId, RedirectAttributes redirectAttributes) {
        Payment payment = paymentRepository.findByOrderId(orderId).orElseThrow(() -> new ApiException(404, "payment not found"));
        paymentService.onPaymentFailed("web-dev-" + UUID.randomUUID(), payment.getProviderIntentId());
        redirectAttributes.addFlashAttribute("message", "Simulated payment failure");
        return "redirect:/web/orders/" + orderId;
    }
}
