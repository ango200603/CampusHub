package com.campushub.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.mybatis.spring.annotation.MapperScan;


/**
 * Spring Boot entry point for the User service.
 */
@EnableDiscoveryClient
@MapperScan("com.campushub.user.mapper")

@SpringBootApplication(scanBasePackages = "com.campushub")
public class UserServiceApplication {
    /**
     * Starts the Spring Boot application.
     */
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
