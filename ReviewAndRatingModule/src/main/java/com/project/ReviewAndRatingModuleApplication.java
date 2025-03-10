package com.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableFeignClients
//@EnableDiscoveryClient(autoRegister = true)
public class ReviewAndRatingModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReviewAndRatingModuleApplication.class, args);
    }

}
