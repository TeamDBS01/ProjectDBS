package com.project;

import com.project.controllers.OrderController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class OrderManagementApplicationTests {

	@Autowired
	private OrderController controller;

	@Test
	void contextLoads() {
		assertNotNull(controller);
	}
//
//	@Test
//	void test_main() {
//		OrderManagementApplication.main(new String[] {});
//	}

}
