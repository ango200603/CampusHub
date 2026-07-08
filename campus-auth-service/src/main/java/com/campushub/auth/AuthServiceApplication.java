    package com.campushub.auth;

    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
    import org.springframework.cloud.openfeign.EnableFeignClients;
import org.mybatis.spring.annotation.MapperScan;


    /**
     * Spring Boot entry point for the Auth service.
     */
    @EnableDiscoveryClient
    @EnableFeignClients(basePackages = "com.campushub.auth.client")
@MapperScan("com.campushub.auth.mapper")

    @SpringBootApplication(scanBasePackages = "com.campushub")
    public class AuthServiceApplication {
        /**
         * Starts the Spring Boot application.
         */
        public static void main(String[] args) {
            SpringApplication.run(AuthServiceApplication.class, args);
        }
    }
