package com.healthtracker;

import com.healthtracker.init.DatabaseInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class HealthTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HealthTrackerApplication.class, args);
    }

    @PostConstruct
    public void init() {
        
        DatabaseInitializer.initialise();
    }
}