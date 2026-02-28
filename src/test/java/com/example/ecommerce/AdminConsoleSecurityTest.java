package com.example.ecommerce;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.example.ecommerce.admin.domain.*;
import com.example.ecommerce.admin.repo.*;
import java.time.Instant;
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
@TestPropertySource(properties = {"admin.allowed-cidrs=127.0.0.1/32"})
class AdminConsoleSecurityTest {
  @Autowired MockMvc mvc;
  @Autowired AdminUserRepository users;

  @Test
  void loginPageWorks() throws Exception {
    mvc.perform(get("/admin/login").with(r->{r.setRemoteAddr("127.0.0.1");return r;})).andExpect(status().isOk());
  }

  @Test
  void notEnabledCannotLogin() throws Exception {
    var u = new AdminUser(); u.email="pending@test.com"; u.passwordHash="$2a$10$7fQfdqM4M3d4J8fWr6Sraei4W5Tvx4m7WAfS74hQ7JPKV0f8hohI2"; u.role=AdminRole.ADMIN; u.emailVerified=true; u.enabled=false; u.createdAt=Instant.now(); u.updatedAt=Instant.now(); users.save(u);
    mvc.perform(post("/admin/login").with(csrf()).param("username", "pending@test.com").param("password","Admin123!").with(r->{r.setRemoteAddr("127.0.0.1");return r;}))
        .andExpect(status().is3xxRedirection());
  }

  @Test
  void allowlistBlock() throws Exception {
    mvc.perform(get("/admin/login").with(r->{r.setRemoteAddr("8.8.8.8");return r;})).andExpect(status().isForbidden());
  }
}
