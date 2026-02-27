package com.example.stripedemo.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private final OrderService orderService;
    public OrderController(OrderService orderService) { this.orderService = orderService; }

    @PostMapping
    public OrderResponse create(@Valid @RequestBody CreateOrderApiRequest req) {
        Order o = orderService.createOrder(new OrderService.CreateOrderRequest(req.customerEmail(), req.items().stream().map(i -> new OrderService.CreateOrderItem(i.sku(), i.qty())).toList()));
        return new OrderResponse(o.getId(), o.getStatus().name(), o.getTotalAmount(), o.getCurrency());
    }

    @GetMapping("/{id}") public Order get(@PathVariable String id) { return orderService.getOrder(id); }

    public record CreateOrderApiRequest(@NotBlank @Email String customerEmail, @NotEmpty List<Item> items) {}
    public record Item(@NotBlank String sku, @Min(1) Long qty) {}
    public record OrderResponse(String orderId, String status, Long totalAmount, String currency) {}
}
