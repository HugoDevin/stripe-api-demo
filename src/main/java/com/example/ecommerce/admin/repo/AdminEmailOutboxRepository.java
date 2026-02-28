package com.example.ecommerce.admin.repo;

import com.example.ecommerce.admin.domain.AdminEmailOutbox;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminEmailOutboxRepository extends JpaRepository<AdminEmailOutbox, UUID> {
  List<AdminEmailOutbox> findTop100ByOrderByCreatedAtDesc();
}
