package com.expiryalert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point of the application.
 * @SpringBootApplication switches on auto-configuration and scans this package
 * (and all sub-packages) for controllers, services and repositories.
 */
@SpringBootApplication
public class ExpiryAlertApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExpiryAlertApplication.class, args);
    }
}
