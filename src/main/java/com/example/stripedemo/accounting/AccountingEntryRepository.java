package com.example.stripedemo.accounting;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountingEntryRepository extends JpaRepository<AccountingEntry, String> {
    boolean existsByOrderId(String orderId);
}
