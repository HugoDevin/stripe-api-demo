package com.example.stripedemo.fulfillment;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FulfillmentRepository extends JpaRepository<Fulfillment, String> {
    boolean existsByOrderId(String orderId);
}
