package com.campushub.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.mybatis.spring.annotation.MapperScan;

/**
 * Spring Boot entry point for the Admin service.
 */
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.campushub.admin.client")
@MapperScan("com.campushub.admin.mapper")
@SpringBootApplication(scanBasePackages = "com.campushub")
@SuppressWarnings("unused")
public class AdminServiceApplication {
    /**
     * Starts the Spring Boot application.
     */
    public static void main(String[] args) {
        SpringApplication.run(AdminServiceApplication.class, args);
    }
}
