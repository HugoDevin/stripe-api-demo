package com.example.ecommerce.admin.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class AdminIpAllowlistFilter extends OncePerRequestFilter {
  private final List<Cidr> cidrs;
  public AdminIpAllowlistFilter(@Value("${admin.allowed-cidrs:127.0.0.1/32,10.0.0.0/8,192.168.0.0/16,172.16.0.0/12}") String allowed) {
    this.cidrs = Arrays.stream(allowed.split(",")).map(String::trim).filter(s -> !s.isBlank()).map(Cidr::parse).toList();
  }

  @Override protected boolean shouldNotFilter(HttpServletRequest request) { return !request.getRequestURI().startsWith("/admin"); }

  @Override protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws ServletException, IOException {
    String ip = Optional.ofNullable(req.getHeader("X-Forwarded-For")).map(v -> v.split(",")[0].trim()).orElse(req.getRemoteAddr());
    try {
      InetAddress addr = InetAddress.getByName(ip);
      boolean ok = cidrs.stream().anyMatch(c -> c.contains(addr));
      if (!ok) { res.sendError(HttpStatus.FORBIDDEN.value(), "admin access denied by ip policy"); return; }
    } catch (Exception e) { res.sendError(HttpStatus.FORBIDDEN.value(), "admin access denied"); return; }
    chain.doFilter(req, res);
  }

  record Cidr(BigInteger network, BigInteger mask, int size){
    static Cidr parse(String s){
      try {
        var p=s.split("/"); InetAddress a=InetAddress.getByName(p[0]); int bits=Integer.parseInt(p[1]); int size=a.getAddress().length*8;
        BigInteger mask = bits==0?BigInteger.ZERO:BigInteger.ONE.shiftLeft(size).subtract(BigInteger.ONE).xor(BigInteger.ONE.shiftLeft(size-bits).subtract(BigInteger.ONE));
        BigInteger net = new BigInteger(1,a.getAddress()).and(mask);
        return new Cidr(net, mask, size);
      } catch (Exception e){ throw new IllegalArgumentException("bad cidr "+s); }
    }
    boolean contains(InetAddress a){ if(a.getAddress().length*8!=size) return false; return new BigInteger(1,a.getAddress()).and(mask).equals(network); }
  }
}
