package com.example.oops;

import jakarta.persistence.Enumerated;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class OopsApplication {

    public static void main(String[] args) {
        SpringApplication.run(OopsApplication.class, args);
    }

}
