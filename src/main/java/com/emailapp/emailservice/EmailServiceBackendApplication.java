package com.emailapp.emailservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.net.URI;

@SpringBootApplication
public class EmailServiceBackendApplication {

    public static void main(String[] args) {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl != null && databaseUrl.startsWith("postgresql://")) {
            try {
                // Parse the DATABASE_URL
                URI dbUri = new URI(databaseUrl);
                String username = dbUri.getUserInfo().split(":")[0];
                String password = dbUri.getUserInfo().split(":")[1];
                String host = dbUri.getHost();
                int port = dbUri.getPort();
                String database = dbUri.getPath().substring(1); // Remove leading "/"

                // Build JDBC URL without credentials
                String jdbcUrl = String.format("jdbc:postgresql://%s:%d/%s", host, port, database);

                // Set system properties
                System.setProperty("spring.datasource.url", jdbcUrl);
                System.setProperty("spring.datasource.username", username);
                System.setProperty("spring.datasource.password", password);

                System.out.println("Database configured:");
                System.out.println("  URL: " + jdbcUrl);
                System.out.println("  Username: " + username);
                System.out.println("  Password: " + (password != null ? "***" : "null"));
            } catch (Exception e) {
                System.err.println("Error parsing DATABASE_URL: " + e.getMessage());
            }
        }

        System.out.println("DATABASE_URL: " + databaseUrl);
        System.out.println("JWT_SECRET: " + System.getenv("JWT_SECRET"));
        System.out.println("JWT_EXPIRATION: " + System.getenv("JWT_EXPIRATION"));

        SpringApplication.run(EmailServiceBackendApplication.class, args);
    }
}