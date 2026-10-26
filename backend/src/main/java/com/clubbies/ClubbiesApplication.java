package com.clubbies;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ClubbiesApplication {
    public static void main(String[] args) {
        SpringApplication.run(ClubbiesApplication.class, args);
    }
}
