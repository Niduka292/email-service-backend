package com.emailapp.emailservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmailServiceBackendApplication {

    public static void main(String[] args) {
        // Debug: Print environment variables
        System.out.println("DATABASE_URL: " + System.getenv("DATABASE_URL"));
        System.out.println("JWT_SECRET: " + System.getenv("JWT_SECRET"));
        System.out.println("JWT_EXPIRATION: " + System.getenv("JWT_EXPIRATION"));

        SpringApplication.run(EmailServiceBackendApplication.class, args);
        System.out.println("Application running on port: 8080");
    }

}
