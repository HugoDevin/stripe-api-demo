package com.example.ecommerce.common.correlation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class CorrelationFilter extends OncePerRequestFilter {
  @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
    String cid = req.getHeader("X-Correlation-Id");
    if (cid == null || cid.isBlank()) cid = UUID.randomUUID().toString();
    MDC.put("correlationId", cid);
    res.setHeader("X-Correlation-Id", cid);
    try { chain.doFilter(req, res); } finally { MDC.remove("correlationId"); }
  }
}
