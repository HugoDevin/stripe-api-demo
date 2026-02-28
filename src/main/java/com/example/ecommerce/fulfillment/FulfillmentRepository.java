package com.example.ecommerce.fulfillment;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FulfillmentRepository extends JpaRepository<Fulfillment, UUID> {
  boolean existsByOrderId(UUID orderId);
}
