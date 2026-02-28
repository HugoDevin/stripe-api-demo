package com.example.ecommerce.admin.config;

import com.example.ecommerce.admin.service.AdminEmailSender;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminEmailSenderConfig {
  @Bean
  @ConditionalOnMissingBean(AdminEmailSender.class)
  AdminEmailSender fallbackAdminEmailSender() {
    return (toEmail, verifyUrl) -> {};
  }
}
