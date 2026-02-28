package com.example.ecommerce.admin.service;

import com.example.ecommerce.admin.repo.AdminUserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class AdminUserDetailsService implements UserDetailsService {
  private final AdminUserRepository repo;
  public AdminUserDetailsService(AdminUserRepository repo) {this.repo = repo;}
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    var u = repo.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("not found"));
    return User.builder()
        .username(u.email)
        .password(u.passwordHash)
        .authorities(new SimpleGrantedAuthority("ROLE_" + u.role.name()))
        .accountLocked(u.locked)
        .disabled(!(u.enabled && u.emailVerified))
        .build();
  }
}
