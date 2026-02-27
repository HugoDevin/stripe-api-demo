package com.example.ecommerce;

import static org.junit.jupiter.api.Assertions.*;

import com.example.ecommerce.catalog.*;
import com.example.ecommerce.inventory.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
class InventoryAtomicUpdateTest {
  @Autowired InventoryRepository inventoryRepository;
  @Autowired ProductRepository productRepository;

  @Test @Transactional
  void reserveSuccessAndFail(){
    Product p=new Product(); p.sku="S1"; p.name="n"; p.price=new java.math.BigDecimal("1.00"); p.currency="USD"; productRepository.save(p);
    Inventory i=new Inventory(); i.sku="S1"; i.availableQty=10; i.reservedQty=0; inventoryRepository.save(i);
    assertEquals(1, inventoryRepository.reserveAtomic("S1", 6));
    assertEquals(0, inventoryRepository.reserveAtomic("S1", 6));
    Inventory now = inventoryRepository.findById("S1").orElseThrow();
    assertEquals(4, now.availableQty);
    assertEquals(6, now.reservedQty);
  }
}
