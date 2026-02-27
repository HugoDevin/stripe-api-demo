package com.example.stripedemo.order;

import com.example.stripedemo.catalog.Product;
import com.example.stripedemo.catalog.ProductRepository;
import com.example.stripedemo.common.error.ApiException;
import com.example.stripedemo.inventory.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository reservationRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, InventoryRepository inventoryRepository, InventoryReservationRepository reservationRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.inventoryRepository = inventoryRepository;
        this.reservationRepository = reservationRepository;
    }

    @Transactional
    public Order createOrder(CreateOrderRequest req) {
        if (req.items().isEmpty()) throw new ApiException(400, "items required");
        Order order = new Order();
        order.setCustomerEmail(req.customerEmail());
        order.setStatus(OrderStatus.PAYMENT_PENDING);
        order.setCreatedAt(OffsetDateTime.now()); order.setUpdatedAt(OffsetDateTime.now());

        long total = 0; String currency = null;
        for (CreateOrderItem itemReq: req.items()) {
            Product p = productRepository.findById(itemReq.sku()).orElseThrow(() -> new ApiException(404, "sku not found: " + itemReq.sku()));
            if (!p.isActive()) throw new ApiException(409, "inactive sku");
            if (currency == null) currency = p.getCurrency();
            if (!currency.equalsIgnoreCase(p.getCurrency())) throw new ApiException(409, "mixed currency unsupported");
            int updated = inventoryRepository.reserveAtomic(itemReq.sku(), itemReq.qty());
            if (updated == 0) throw new ApiException(409, "insufficient stock: " + itemReq.sku());

            OrderItem oi = new OrderItem();
            oi.setOrder(order); oi.setSku(p.getSku()); oi.setName(p.getName()); oi.setUnitPrice(p.getPrice()); oi.setQty(itemReq.qty());
            oi.setLineTotal(p.getPrice() * itemReq.qty());
            order.getItems().add(oi);
            total += oi.getLineTotal();
        }
        order.setTotalAmount(total); order.setCurrency(currency);
        Order saved = orderRepository.save(order);

        for (OrderItem oi : saved.getItems()) {
            InventoryReservation r = new InventoryReservation();
            r.setOrderId(saved.getId()); r.setSku(oi.getSku()); r.setQty(oi.getQty());
            r.setStatus(InventoryReservationStatus.RESERVED); r.setExpiresAt(OffsetDateTime.now().plusMinutes(15));
            r.setCreatedAt(OffsetDateTime.now()); r.setUpdatedAt(OffsetDateTime.now());
            reservationRepository.save(r);
        }
        return saved;
    }

    public Order getOrder(String id) { return orderRepository.findById(id).orElseThrow(() -> new ApiException(404, "order not found")); }

    @Transactional
    public void markPaid(String orderId) {
        Order o = getOrder(orderId);
        o.setStatus(OrderStatus.PAID); o.setUpdatedAt(OffsetDateTime.now());
        orderRepository.save(o);
    }

    @Transactional
    public void cancelOrder(String orderId) {
        Order o = getOrder(orderId); o.setStatus(OrderStatus.CANCELED); o.setUpdatedAt(OffsetDateTime.now()); orderRepository.save(o);
    }

    public record CreateOrderRequest(String customerEmail, List<CreateOrderItem> items) {}
    public record CreateOrderItem(String sku, Long qty) {}
}
