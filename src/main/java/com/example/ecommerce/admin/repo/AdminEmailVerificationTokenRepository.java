package com.example.ecommerce.admin.repo;

import com.example.ecommerce.admin.domain.AdminEmailVerificationToken;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminEmailVerificationTokenRepository extends JpaRepository<AdminEmailVerificationToken, UUID> {
  Optional<AdminEmailVerificationToken> findByToken(String token);
}
