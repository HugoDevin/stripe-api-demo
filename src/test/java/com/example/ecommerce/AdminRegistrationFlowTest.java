package com.example.ecommerce;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.ecommerce.admin.repo.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles({"test","dev-offline"})
@TestPropertySource(properties = {"admin.allowed-cidrs=127.0.0.1/32","app.base-url=http://localhost:8080"})
class AdminRegistrationFlowTest {
  @Autowired MockMvc mvc;
  @Autowired AdminUserRepository users;
  @Autowired AdminEmailOutboxRepository outbox;
  @Autowired AdminEmailVerificationTokenRepository tokens;

  @Test
  void registerThenVerify() throws Exception {
    mvc.perform(post("/admin/register").with(csrf()).param("email","newadmin@test.com").param("password","Admin123!").param("name","new")
            .with(r->{r.setRemoteAddr("127.0.0.1");return r;}))
        .andExpect(status().isOk());
    assertThat(outbox.findTop100ByOrderByCreatedAtDesc()).isNotEmpty();
    String verifyUrl = outbox.findTop100ByOrderByCreatedAtDesc().get(0).verifyUrl;
    String token = verifyUrl.substring(verifyUrl.indexOf("token=")+6);
    mvc.perform(get("/admin/verify-email").param("token", token).with(r->{r.setRemoteAddr("127.0.0.1");return r;}))
        .andExpect(status().isOk())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("Email verified")));
    var u = users.findByEmail("newadmin@test.com").orElseThrow();
    assertThat(u.emailVerified).isTrue();
    assertThat(u.enabled).isFalse();
  }
}
