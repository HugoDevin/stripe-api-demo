package com.example.ecommerce.admin.repo;

import com.example.ecommerce.admin.domain.AdminUser;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserRepository extends JpaRepository<AdminUser, UUID> {
  Optional<AdminUser> findByEmail(String email);
}
