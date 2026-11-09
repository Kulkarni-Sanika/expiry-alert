package com.expiryalert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point of the Spring Boot application.
 * Enables auto-configuration and component scanning for the application package
 * and its sub-packages.
 */
@SpringBootApplication
public class ExpiryAlertApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpiryAlertApplication.class, args);
    }
}
