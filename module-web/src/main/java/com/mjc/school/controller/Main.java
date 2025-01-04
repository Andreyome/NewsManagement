package com.mjc.school.controller;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication(scanBasePackages ={ "com.mjc.school","com.mjc.school.repository.impl"})
@EntityScan(basePackages = {"com.mjc.school"})
@EnableWebMvc
@EnableJpaRepositories(basePackages = "com.mjc.school.repository.impl")
@EnableJpaAuditing
@EnableTransactionManagement
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);


    }
}
