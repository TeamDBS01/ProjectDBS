package com.project;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ProjectDbsBookCatalogApplicationTests {

	@Test
	void main() {
		ProjectDbsBookCatalogApplication.main(new String[] {});
		assertTrue(true);
	}
	@Test
	void contextLoads() {
		assertTrue(true);
	}

}
