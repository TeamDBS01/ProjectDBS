package com.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Main class for the DBS Inventory Module application.
 */
@SpringBootApplication
@EnableFeignClients
public class DbsInventoryModuleApplication {

	/**
	 * The main method to run the Spring Boot application.
	 * @param args the command line arguments.
	 */
	public static void main(String[] args) {
		SpringApplication.run(DbsInventoryModuleApplication.class, args);
	}

}
