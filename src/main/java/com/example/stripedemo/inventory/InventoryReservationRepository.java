package com.example.stripedemo.inventory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, String> {
    List<InventoryReservation> findByOrderId(String orderId);
    List<InventoryReservation> findByStatusAndExpiresAtBefore(InventoryReservationStatus status, OffsetDateTime now);
}
