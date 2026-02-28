package com.example.ecommerce.admin.service;

import org.springframework.stereotype.Component;

@Component
public class NoopAdminEmailSender implements AdminEmailSender {
  @Override public void sendVerification(String toEmail, String verifyUrl) {}
}
