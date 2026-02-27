package com.example.stripedemo.common.correlation;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class CorrelationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String cid = request.getHeader("X-Correlation-Id");
        if (cid == null || cid.isBlank()) cid = UUID.randomUUID().toString();
        MDC.put("correlationId", cid);
        response.setHeader("X-Correlation-Id", cid);
        try { filterChain.doFilter(request, response); } finally { MDC.clear(); }
    }
}
