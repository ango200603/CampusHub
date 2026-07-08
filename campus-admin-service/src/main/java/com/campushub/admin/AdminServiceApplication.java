package com.campushub.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.mybatis.spring.annotation.MapperScan;


/**
 * Spring Boot entry point for the Admin service.
 */
@EnableDiscoveryClient
@MapperScan("com.campushub.admin.mapper")

@SpringBootApplication(scanBasePackages = "com.campushub")
public class AdminServiceApplication {
    /**
     * Starts the Spring Boot application.
     */
    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
    }
}
