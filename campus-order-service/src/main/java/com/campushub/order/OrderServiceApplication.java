    package com.campushub.order;

    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
    import org.springframework.cloud.openfeign.EnableFeignClients;
import org.mybatis.spring.annotation.MapperScan;


    /**
     * Spring Boot entry point for the Order service.
     */
    @EnableDiscoveryClient
    @EnableFeignClients(basePackages = "com.campushub.order.client")
@MapperScan("com.campushub.order.mapper")

    @SpringBootApplication(scanBasePackages = "com.campushub")
    public class OrderServiceApplication {
        /**
         * Starts the Spring Boot application.
         */
        public static void main(String[] args) {
            SpringApplication.run(OrderServiceApplication.class, args);
        }
    }
