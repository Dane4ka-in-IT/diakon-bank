package com.diakonbank.bankapi.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories("com.diakonbank.bankapi.service.repository")
@EntityScan("com.diakonbank.bankapi.service.entity")
public class BankApiServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankApiServiceApplication.class, args);
    }
}
