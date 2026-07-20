package com.eventmanagement.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class BackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("  Event Management Backend is RUNNING!");
        System.out.println("  Server: http://localhost:8080");
        System.out.println("========================================\n");
    }
}
