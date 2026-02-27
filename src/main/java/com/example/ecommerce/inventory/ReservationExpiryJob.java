package com.example.ecommerce.inventory;

import com.example.ecommerce.order.*;
import java.time.Instant;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ReservationExpiryJob {
  private final InventoryReservationRepository reservations; private final InventoryRepository inventory; private final OrderRepository orders;
  public ReservationExpiryJob(InventoryReservationRepository r, InventoryRepository i, OrderRepository o){reservations=r;inventory=i;orders=o;}
  @Scheduled(fixedDelay = 60000)
  @Transactional
  public void expire(){
    for (InventoryReservation r: reservations.findByStatusAndExpiresAtBefore(ReservationStatus.RESERVED, Instant.now())){
      inventory.release(r.sku, r.qty); r.status=ReservationStatus.EXPIRED; reservations.save(r);
      orders.findById(r.orderId).ifPresent(o->{if(o.status==OrderStatus.PAYMENT_PENDING){o.status=OrderStatus.CANCELED; orders.save(o);}});
    }
  }
}
