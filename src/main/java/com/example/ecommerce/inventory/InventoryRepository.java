package com.example.ecommerce.inventory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface InventoryRepository extends JpaRepository<Inventory, String> {
  @Modifying
  @Query(value = "UPDATE inventory SET available_qty=available_qty-:qty,reserved_qty=reserved_qty+:qty,version=version+1,updated_at=now() WHERE sku=:sku AND available_qty>=:qty", nativeQuery = true)
  int reserveAtomic(@Param("sku") String sku, @Param("qty") int qty);

  @Modifying
  @Query(value = "UPDATE inventory SET available_qty=available_qty+:qty,reserved_qty=reserved_qty-:qty,version=version+1,updated_at=now() WHERE sku=:sku", nativeQuery = true)
  int release(@Param("sku") String sku, @Param("qty") int qty);

  @Modifying
  @Query(value = "UPDATE inventory SET reserved_qty=reserved_qty-:qty,version=version+1,updated_at=now() WHERE sku=:sku", nativeQuery = true)
  int commit(@Param("sku") String sku, @Param("qty") int qty);
}
