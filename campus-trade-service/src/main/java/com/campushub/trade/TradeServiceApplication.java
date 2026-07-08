package com.campushub.trade;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.mybatis.spring.annotation.MapperScan;


/**
 * Spring Boot entry point for the Trade service.
 */
@EnableDiscoveryClient
@MapperScan("com.campushub.trade.mapper")

@SpringBootApplication(scanBasePackages = "com.campushub")
public class TradeServiceApplication {
    /**
     * Starts the Spring Boot application.
     */
    public static void main(String[] args) {
        SpringApplication.run(TradeServiceApplication.class, args);
    }
}
