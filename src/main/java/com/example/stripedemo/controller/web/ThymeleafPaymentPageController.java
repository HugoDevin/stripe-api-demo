package com.example.stripedemo.controller.web;

import com.example.stripedemo.application.CheckoutApplicationService;
import com.example.stripedemo.domain.order.OrderService;
import com.example.stripedemo.service.ProductCatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/web")
public class ThymeleafPaymentPageController {

    private final ProductCatalogService productCatalogService;
    private final CheckoutApplicationService checkoutApplicationService;
    private final OrderService orderService;

    public ThymeleafPaymentPageController(
            ProductCatalogService productCatalogService,
            CheckoutApplicationService checkoutApplicationService,
            OrderService orderService
    ) {
        this.productCatalogService = productCatalogService;
        this.checkoutApplicationService = checkoutApplicationService;
        this.orderService = orderService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("products", productCatalogService.getAllProducts());
        return "index";
    }

    @PostMapping("/checkout")
    public String checkout(@RequestParam String product, Model model) throws Exception {
        CheckoutApplicationService.CheckoutResult result = checkoutApplicationService.createCheckout(product);

        model.addAttribute("clientSecret", result.clientSecret());
        model.addAttribute("orderId", result.orderId());
        model.addAttribute("product", result.product());
        model.addAttribute("amount", result.amount());
        return "checkout";
    }

    @PostMapping("/orders/{orderId}/complete")
    @ResponseBody
    public String completeOrder(@PathVariable String orderId) {
        orderService.completeOrder(orderId);
        return "OK";
    }

    @GetMapping("/orders")
    public String orders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "orders";
    }
}
