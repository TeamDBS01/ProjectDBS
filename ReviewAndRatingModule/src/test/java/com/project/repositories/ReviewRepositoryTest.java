package com.project.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;

import com.project.ReviewAndRatingModuleApplication;
import com.project.models.Review;

import jakarta.transaction.Transactional;

@DataJpaTest
@ContextConfiguration(classes = {ReviewAndRatingModuleApplication.class})
class ReviewRepositoryTest {

	@Autowired
	private ReviewRepository reviewRepository;


	@Test
	@DisplayName("FindAll-Positive")
	void test_findAll_positive() {
		Review review = new Review(3.8f, "Good Content", 12, "ISBN-0001");
		reviewRepository.save(review);
		
		List<Review> list = reviewRepository.findAll();
		assertFalse(list.isEmpty());
		assertEquals(review, list.get(0));
	}

	@Test
	@DisplayName("FindAll-Negative")
	void test_findAll_negative() {
		List<Review> list = reviewRepository.findAll();
		assertTrue(list.isEmpty());
	}	

	@Test
	@DisplayName("FindById-Positive")
	void test_findById_positive() {
		Review review = new Review(1f, "Bad Content", 10, "ISBN-0002");
		reviewRepository.save(review);
		
		Optional<Review> optionalReview = reviewRepository.findById(review.getReviewId());
		assertTrue(optionalReview.isPresent());
		assertEquals(review, optionalReview.get());
	}

	@Test
	@DisplayName("FindById-Negative")
	void test_findById_negative() {
		Optional<Review> optionalReview = reviewRepository.findById(-1l);
		assertTrue(optionalReview.isEmpty());
	}

	@Test
	@DisplayName("Update-Positive")
	void test_update_positive() {
		Review review = new Review(1f, "Bad Content", 10, "ISBN-0002");
		reviewRepository.save(review);
		review.setComment("Worst Book Ever!");
		
		Optional<Review> optionalReview = reviewRepository.findById(review.getReviewId());
		assertTrue(optionalReview.isPresent());
		assertEquals(review, optionalReview.get());
	}


	@Test
	@DisplayName("DeleteById-Positive")
	void test_deleteById_positive() {
		Review review = new Review(1f, "Bad Content", 10, "ISBN-0002");
		reviewRepository.save(review);
		
		reviewRepository.deleteById(review.getReviewId());
		Optional<Review> optionalReview = reviewRepository.findById(review.getReviewId());
		assertFalse(optionalReview.isPresent());
	}

	@Test
	@DisplayName("DeleteById-Negative")
	void test_deleteById_negative() {
		reviewRepository.deleteById(-1l);
	}
	
	
	@Test
	@DisplayName("Delete-Positive")
	void test_delete_positive() {
		Review review = new Review(1f, "Bad Content", 10, "ISBN-0002");
		reviewRepository.save(review);
		
		reviewRepository.delete(review);
		Optional<Review> optionalReview = reviewRepository.findById(review.getReviewId());
		assertFalse(optionalReview.isPresent());
	}

	@Test
	@DisplayName("Delete-Negative")
	void test_delete_negative() {
		reviewRepository.delete(new Review());
	}
	
}
