package com.example.stripedemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class StripeDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(StripeDemoApplication.class, args);
    }
}
