package com.project;

import com.project.controller.ReviewController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("test")
@ContextConfiguration(classes = {ReviewAndRatingModuleApplication.class})
class ReviewAndRatingModuleApplicationTest {

	@Autowired
	private ReviewController reviewController;

	@Test
	void test_main() {
		ReviewAndRatingModuleApplication.main(new String[] {});
		assertTrue(true);
	}
	@Test
	void contextLoads() {
		// This method tests if the Spring Boot Application Test is initialized properly.
        assertNotNull(reviewController);
	}
}
