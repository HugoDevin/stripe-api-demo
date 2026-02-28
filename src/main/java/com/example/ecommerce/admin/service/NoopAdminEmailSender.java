package com.example.ecommerce.admin.service;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"staging","prod","test"})
public class NoopAdminEmailSender implements AdminEmailSender {
  @Override public void sendVerification(String toEmail, String verifyUrl) {}
}
