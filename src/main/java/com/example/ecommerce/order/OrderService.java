package com.example.ecommerce.order;

import com.example.ecommerce.catalog.ProductService;
import com.example.ecommerce.common.error.AppException;
import com.example.ecommerce.inventory.*;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class OrderService {
  private final OrderRepository orderRepository; private final ProductService productService;
  private final InventoryRepository inventoryRepository; private final InventoryReservationRepository reservationRepository;
  public OrderService(OrderRepository o, ProductService p, InventoryRepository i, InventoryReservationRepository r){orderRepository=o;productService=p;inventoryRepository=i;reservationRepository=r;}

  @Transactional
  public OrderEntity create(String customerEmail, List<ItemRequest> items){
    OrderEntity order = new OrderEntity(); order.customerEmail=customerEmail; order.currency="USD"; order.status=OrderStatus.PAYMENT_PENDING;
    BigDecimal total = BigDecimal.ZERO;
    for (ItemRequest req : items){
      var p = productService.get(req.sku());
      if (inventoryRepository.reserveAtomic(req.sku(), req.qty()) == 0) throw new AppException(HttpStatus.CONFLICT, "insufficient stock: "+req.sku());
      InventoryReservation res = new InventoryReservation(); res.orderId=order.id; res.sku=req.sku(); res.qty=req.qty(); res.status=ReservationStatus.RESERVED; res.expiresAt=Instant.now().plusSeconds(900); reservationRepository.save(res);
      OrderItem oi = new OrderItem(); oi.order=order; oi.sku=p.sku; oi.name=p.name; oi.qty=req.qty(); oi.unitPrice=p.price; oi.lineTotal=p.price.multiply(BigDecimal.valueOf(req.qty()));
      order.items.add(oi); total = total.add(oi.lineTotal);
    }
    order.totalAmount = OrderPricing.total(order.items);
    return orderRepository.save(order);
  }

  public OrderEntity get(java.util.UUID id){return orderRepository.findById(id).orElseThrow(() -> new AppException(HttpStatus.NOT_FOUND,"order not found"));}
  public record ItemRequest(String sku, int qty) {}
}
