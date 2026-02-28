package com.example.ecommerce.admin.service;

import com.example.ecommerce.admin.domain.*;
import com.example.ecommerce.admin.repo.*;
import com.example.ecommerce.common.error.AppException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUserService {
  private final AdminUserRepository users;
  private final AdminEmailVerificationTokenRepository tokens;
  private final PasswordEncoder encoder;
  private final AdminEmailSender emailSender;
  @Value("${app.base-url:http://localhost:8080}") String baseUrl;

  public AdminUserService(AdminUserRepository users, AdminEmailVerificationTokenRepository tokens, PasswordEncoder encoder, ObjectProvider<AdminEmailSender> emailSenderProvider) {
    this.users = users; this.tokens = tokens; this.encoder = encoder;
    this.emailSender = emailSenderProvider.getIfAvailable(() -> (to, url) -> {});
  }

  @Transactional
  public void register(String email, String password, String name) {
    if (users.findByEmail(email).isPresent()) throw new AppException(HttpStatus.CONFLICT, "email already exists");
    AdminUser u = new AdminUser();
    u.email = email; u.passwordHash = encoder.encode(password); u.displayName = name; u.role = AdminRole.ADMIN;
    users.save(u);
    var t = new AdminEmailVerificationToken();
    t.userId = u.id; t.token = UUID.randomUUID().toString(); t.expiresAt = Instant.now().plusSeconds(86400);
    tokens.save(t);
    emailSender.sendVerification(email, baseUrl + "/admin/verify-email?token=" + t.token);
  }

  @Transactional
  public boolean verifyEmail(String token) {
    var t = tokens.findByToken(token).orElse(null);
    if (t == null || t.usedAt != null || t.expiresAt.isBefore(Instant.now())) return false;
    var u = users.findById(t.userId).orElseThrow();
    u.emailVerified = true; u.updatedAt = Instant.now(); users.save(u);
    t.usedAt = Instant.now(); tokens.save(t);
    return true;
  }

  public List<AdminUser> listUsers() { return users.findAll(); }
  @Transactional public void setEnabled(UUID id, boolean enabled){var u=users.findById(id).orElseThrow();u.enabled=enabled;u.updatedAt=Instant.now();users.save(u);}  
  public AdminUser get(UUID id){return users.findById(id).orElseThrow();}
}
