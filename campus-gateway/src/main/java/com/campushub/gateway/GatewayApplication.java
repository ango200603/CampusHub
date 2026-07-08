package com.campushub.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * CampusHub gateway application entry point.
 */
@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = "com.campushub")
public class GatewayApplication {
    /**
     * Starts the gateway service.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
}
