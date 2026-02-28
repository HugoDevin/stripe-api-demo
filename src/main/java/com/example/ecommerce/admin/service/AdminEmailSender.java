package com.example.ecommerce.admin.service;

public interface AdminEmailSender {
  void sendVerification(String toEmail, String verifyUrl);
}
