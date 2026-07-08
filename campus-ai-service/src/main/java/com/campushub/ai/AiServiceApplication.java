package com.campushub.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.mybatis.spring.annotation.MapperScan;


/**
 * Spring Boot entry point for the Ai service.
 */
@EnableDiscoveryClient
@MapperScan("com.campushub.ai.mapper")

@SpringBootApplication(scanBasePackages = "com.campushub")
public class AiServiceApplication {
    /**
     * Starts the Spring Boot application.
     */
    public static void main(String[] args) {
        SpringApplication.run(AiServiceApplication.class, args);
    }
}
