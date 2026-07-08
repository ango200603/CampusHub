package com.campushub.pay;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.mybatis.spring.annotation.MapperScan;


/**
 * Spring Boot entry point for the Pay service.
 */
@EnableDiscoveryClient
@MapperScan("com.campushub.pay.mapper")

@SpringBootApplication(scanBasePackages = "com.campushub")
public class PayServiceApplication {
    /**
     * Starts the Spring Boot application.
     */
    public static void main(String[] args) {
        SpringApplication.run(PayServiceApplication.class, args);
    }
}
