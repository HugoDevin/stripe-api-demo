package com.example.stripedemo.reporting;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface SalesDailyFactRepository extends JpaRepository<SalesDailyFact, LocalDate> {
    List<SalesDailyFact> findByDateBetween(LocalDate from, LocalDate to);
}
