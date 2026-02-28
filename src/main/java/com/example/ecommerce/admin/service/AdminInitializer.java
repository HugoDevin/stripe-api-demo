package com.example.ecommerce.admin.service;

import com.example.ecommerce.admin.domain.*;
import com.example.ecommerce.admin.repo.AdminUserRepository;
import com.example.ecommerce.catalog.Product;
import com.example.ecommerce.catalog.ProductRepository;
import com.example.ecommerce.inventory.Inventory;
import com.example.ecommerce.inventory.InventoryRepository;
import java.math.BigDecimal;
import java.time.Instant;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("dev-offline")
public class AdminInitializer implements ApplicationRunner {
  private final AdminUserRepository users; private final PasswordEncoder encoder;
  private final ProductRepository products;
  private final InventoryRepository inventory;
  public AdminInitializer(AdminUserRepository users, PasswordEncoder encoder, ProductRepository products, InventoryRepository inventory){
    this.users=users;this.encoder=encoder;this.products=products;this.inventory=inventory;
  }
  @Override public void run(ApplicationArguments args) {
    seed("admin@example.com", "Admin123!", "Super Admin", AdminRole.ADMIN_SUPER, true);
    seed("staff@example.com", "Staff123!", "Staff Admin", AdminRole.ADMIN, true);
    seedProduct("SKU-1", "Demo T-Shirt", new BigDecimal("1299"), "USD", 100);
    seedProduct("SKU-2", "Demo Mug", new BigDecimal("599"), "USD", 100);
  }
  private void seed(String email, String raw, String name, AdminRole role, boolean enabled){
    if (users.findByEmail(email).isPresent()) return;
    AdminUser u = new AdminUser(); u.email=email; u.passwordHash=encoder.encode(raw); u.displayName=name; u.role=role; u.emailVerified=true; u.enabled=enabled; u.createdAt=Instant.now(); u.updatedAt=Instant.now();
    users.save(u);
  }

  private void seedProduct(String sku, String name, BigDecimal price, String currency, int initialQty){
    Product p = products.findById(sku).orElseGet(Product::new);
    p.sku = sku;
    p.name = name;
    p.price = price;
    p.currency = currency;
    p.active = true;
    p.updatedAt = Instant.now();
    products.save(p);

    Inventory i = inventory.findById(sku).orElseGet(Inventory::new);
    i.sku = sku;
    if (i.availableQty == 0 && i.reservedQty == 0) i.availableQty = initialQty;
    i.updatedAt = Instant.now();
    inventory.save(i);
  }
}
