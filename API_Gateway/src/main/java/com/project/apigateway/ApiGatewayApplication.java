package com.project.apigateway;

import com.project.filter.AuthenticationFilter;
import com.project.filter.AuthorizationFilter;
import com.project.util.JwtUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import org.springframework.context.annotation.Bean;


@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@Bean
	public JwtUtil jwtUtil() {
		return new JwtUtil();
	}

	@Bean
	public AuthenticationFilter authenticationFilter() {
		return new AuthenticationFilter();
	}

	@Bean
	public AuthorizationFilter authorizationFilter() {
		return new AuthorizationFilter();
	}

}
