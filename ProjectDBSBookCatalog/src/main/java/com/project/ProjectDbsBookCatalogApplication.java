package com.project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class ProjectDbsBookCatalogApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectDbsBookCatalogApplication.class, args);
	}

}
