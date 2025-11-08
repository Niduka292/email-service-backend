package com.emailapp.emailservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EmailServiceBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmailServiceBackendApplication.class, args);
        System.out.println("Application running on port: 8080");
    }

}
