package com.example.stripedemo.invoicing;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReceiptRepository extends JpaRepository<Receipt, String> {
    boolean existsByOrderId(String orderId);
}
