package com.emailapp.emailservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmailServiceBackendApplication {

    public static void main(String[] args) {
        // Convert Railway's DATABASE_URL format to JDBC format
        String databaseUrl = System.getenv("DATABASE_URL");
        if (databaseUrl != null && databaseUrl.startsWith("postgresql://")) {
            String jdbcUrl = databaseUrl.replace("postgresql://", "jdbc:postgresql://");
            System.setProperty("spring.datasource.url", jdbcUrl);
            System.out.println("Converted DATABASE_URL to JDBC format: " + jdbcUrl);
        }

        // Debug output
        System.out.println("DATABASE_URL: " + databaseUrl);
        System.out.println("JWT_SECRET: " + System.getenv("JWT_SECRET"));
        System.out.println("JWT_EXPIRATION: " + System.getenv("JWT_EXPIRATION"));

        SpringApplication.run(EmailServiceBackendApplication.class, args);
    }
}