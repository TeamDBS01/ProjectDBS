package com.project;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {ReviewAndRatingModuleApplication.class})
class ReviewAndRatingModuleApplicationTests {

	@Test
	void contextLoads() {
		// This method tests if the Spring Boot Application Test is initialized properly.
	}

}
