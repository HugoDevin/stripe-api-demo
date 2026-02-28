package com.example.ecommerce.common.error;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(AppException.class)
  ResponseEntity<?> handleApp(AppException ex){
    return ResponseEntity.status(ex.status()).body(Map.of("timestamp", Instant.now(), "message", ex.getMessage()));
  }
  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<?> handleValidation(MethodArgumentNotValidException ex){
    return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
  }
}
