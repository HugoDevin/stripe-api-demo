package com.example.ecommerce.admin.service;

import com.example.ecommerce.admin.domain.AdminEmailOutbox;
import com.example.ecommerce.admin.repo.AdminEmailOutboxRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Primary
@Profile("dev-offline")
public class FakeAdminEmailSender implements AdminEmailSender {
  private final AdminEmailOutboxRepository repo;
  public FakeAdminEmailSender(AdminEmailOutboxRepository repo) {this.repo = repo;}
  @Override public void sendVerification(String toEmail, String verifyUrl) {
    AdminEmailOutbox o = new AdminEmailOutbox();
    o.toEmail = toEmail; o.subject = "Verify admin account"; o.verifyUrl = verifyUrl;
    o.body = "Click to verify: " + verifyUrl;
    repo.save(o);
  }
}
