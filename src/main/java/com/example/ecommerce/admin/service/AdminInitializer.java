package com.example.ecommerce.admin.service;

import com.example.ecommerce.admin.domain.*;
import com.example.ecommerce.admin.repo.AdminUserRepository;
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
  public AdminInitializer(AdminUserRepository users, PasswordEncoder encoder){this.users=users;this.encoder=encoder;}
  @Override public void run(ApplicationArguments args) {
    seed("admin@example.com", "Admin123!", "Super Admin", AdminRole.ADMIN_SUPER, true);
    seed("staff@example.com", "Staff123!", "Staff Admin", AdminRole.ADMIN, true);
  }
  private void seed(String email, String raw, String name, AdminRole role, boolean enabled){
    if (users.findByEmail(email).isPresent()) return;
    AdminUser u = new AdminUser(); u.email=email; u.passwordHash=encoder.encode(raw); u.displayName=name; u.role=role; u.emailVerified=true; u.enabled=enabled; u.createdAt=Instant.now(); u.updatedAt=Instant.now();
    users.save(u);
  }
}
