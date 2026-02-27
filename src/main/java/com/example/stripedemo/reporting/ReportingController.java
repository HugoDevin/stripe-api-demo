package com.example.stripedemo.reporting;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/reporting")
public class ReportingController {
    private final SalesDailyFactRepository repository;
    public ReportingController(SalesDailyFactRepository repository) { this.repository = repository; }

    @GetMapping("/sales-daily")
    public List<SalesDailyFact> sales(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
                                      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return repository.findByDateBetween(from, to);
    }
}
