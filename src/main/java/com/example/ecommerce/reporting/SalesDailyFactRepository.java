package com.example.ecommerce.reporting;

import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesDailyFactRepository extends JpaRepository<SalesDailyFact, LocalDate> {
  List<SalesDailyFact> findByDateBetweenOrderByDate(LocalDate from, LocalDate to);
}
