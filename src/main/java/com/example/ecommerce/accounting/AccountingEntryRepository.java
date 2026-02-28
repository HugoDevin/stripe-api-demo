package com.example.ecommerce.accounting;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountingEntryRepository extends JpaRepository<AccountingEntry, UUID> {
  boolean existsByOrderId(UUID orderId);
}
