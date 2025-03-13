package com.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableFeignClients
@SpringBootApplication
@EnableAspectJAutoProxy
public class ReviewAndRatingModuleApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReviewAndRatingModuleApplication.class, args);
    }

}
