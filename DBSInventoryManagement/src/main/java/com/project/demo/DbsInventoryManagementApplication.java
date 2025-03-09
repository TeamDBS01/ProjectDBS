package com.project.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.demo.repositories")
@ComponentScan(basePackages = "com.demo")
public class DbsInventoryManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(DbsInventoryManagementApplication.class, args);
	}

}
