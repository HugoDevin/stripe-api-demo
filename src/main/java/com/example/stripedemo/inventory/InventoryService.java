package com.example.stripedemo.inventory;

import com.example.stripedemo.order.OrderService;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryReservationRepository reservationRepository;
    private final OrderService orderService;

    public InventoryService(InventoryRepository inventoryRepository, InventoryReservationRepository reservationRepository, OrderService orderService) {
        this.inventoryRepository = inventoryRepository;
        this.reservationRepository = reservationRepository;
        this.orderService = orderService;
    }

    @Transactional
    public void commit(String orderId) {
        for (InventoryReservation r : reservationRepository.findByOrderId(orderId)) {
            if (r.getStatus() != InventoryReservationStatus.RESERVED) continue;
            Inventory i = inventoryRepository.findById(r.getSku()).orElseThrow();
            i.setReservedQty(i.getReservedQty() - r.getQty());
            i.setUpdatedAt(OffsetDateTime.now());
            inventoryRepository.save(i);
            r.setStatus(InventoryReservationStatus.COMMITTED); r.setUpdatedAt(OffsetDateTime.now());
            reservationRepository.save(r);
        }
    }

    @Transactional
    public void release(String orderId, boolean expired) {
        for (InventoryReservation r : reservationRepository.findByOrderId(orderId)) {
            if (r.getStatus() != InventoryReservationStatus.RESERVED) continue;
            Inventory i = inventoryRepository.findById(r.getSku()).orElseThrow();
            i.setAvailableQty(i.getAvailableQty() + r.getQty());
            i.setReservedQty(i.getReservedQty() - r.getQty());
            i.setUpdatedAt(OffsetDateTime.now()); inventoryRepository.save(i);
            r.setStatus(expired ? InventoryReservationStatus.EXPIRED : InventoryReservationStatus.RELEASED);
            r.setUpdatedAt(OffsetDateTime.now()); reservationRepository.save(r);
        }
        orderService.cancelOrder(orderId);
    }

    @Scheduled(fixedDelay = 60000)
    public void expireReservations() {
        reservationRepository.findByStatusAndExpiresAtBefore(InventoryReservationStatus.RESERVED, OffsetDateTime.now())
                .stream().map(InventoryReservation::getOrderId).distinct().forEach(orderId -> release(orderId, true));
    }
}
