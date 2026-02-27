package com.example.ecommerce.inventory;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, UUID> {
  Optional<InventoryReservation> findByOrderId(UUID orderId);
  List<InventoryReservation> findByStatusAndExpiresAtBefore(ReservationStatus status, Instant now);
}
