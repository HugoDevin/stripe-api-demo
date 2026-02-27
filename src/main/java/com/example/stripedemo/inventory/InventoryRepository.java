package com.example.stripedemo.inventory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, String> {
    @Modifying
    @Query("""
            update Inventory i set i.availableQty = i.availableQty - :qty,
            i.reservedQty = i.reservedQty + :qty,
            i.version = i.version + 1,
            i.updatedAt = CURRENT_TIMESTAMP
            where i.sku = :sku and i.availableQty >= :qty
            """)
    int reserveAtomic(@Param("sku") String sku, @Param("qty") long qty);
}
