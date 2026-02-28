package com.example.ecommerce.admin.config;

import com.example.ecommerce.admin.service.AdminUserDetailsService;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.*;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
  @Value("${app.cors.allowed-origins:http://localhost:5173}")
  private String allowedOrigins;

  @Bean PasswordEncoder passwordEncoder(){ return new BCryptPasswordEncoder(); }

  @Bean
  @Order(1)
  SecurityFilterChain adminChain(HttpSecurity http, AdminIpAllowlistFilter ipFilter, AdminUserDetailsService uds) throws Exception {
    http.securityMatcher("/admin/**")
        .userDetailsService(uds)
        .addFilterBefore(ipFilter, UsernamePasswordAuthenticationFilter.class)
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/admin/login", "/admin/register", "/admin/verify-email", "/admin/css/**").permitAll()
            .requestMatchers("/admin/users/**", "/admin/dev/**").hasRole("ADMIN_SUPER")
            .anyRequest().authenticated())
        .formLogin(f -> f.loginPage("/admin/login").loginProcessingUrl("/admin/login").defaultSuccessUrl("/admin/dashboard", true).permitAll())
        .logout(l -> l.logoutUrl("/admin/logout").logoutSuccessUrl("/admin/login?logout").permitAll())
        .csrf(Customizer.withDefaults());
    return http.build();
  }

  @Bean
  @Order(2)
  SecurityFilterChain apiChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/**")
        .cors(Customizer.withDefaults())
        .authorizeHttpRequests(a -> a.anyRequest().permitAll())
        .csrf(csrf -> csrf.disable());
    return http.build();
  }
  @Bean
  CorsConfigurationSource corsConfigurationSource(){
    CorsConfiguration c = new CorsConfiguration();
    c.setAllowedOriginPatterns(Arrays.stream(allowedOrigins.split(",")).map(String::trim).filter(v -> !v.isEmpty()).toList());
    c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
    c.setAllowedHeaders(List.of("*"));
    c.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", c);
    return source;
  }

}
