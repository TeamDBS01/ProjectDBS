package com.project.repositories;

import com.project.models.Review;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    @DisplayName("FindAll-Positive")
    void test_findAll_positive() {
        Review review = new Review(3.8f, "Good Content", 12, "ISBN-0001");
        testEntityManager.persist(review);

        List<Review> list = reviewRepository.findAll();
        assertFalse(list.isEmpty());
        assertEquals(review, list.getFirst());
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
        testEntityManager.persist(review);

        Optional<Review> optionalReview = reviewRepository.findById(review.getReviewId());
        assertTrue(optionalReview.isPresent());
        assertEquals(review, optionalReview.get());
    }

    @Test
    @DisplayName("FindById-Negative")
    void test_findById_negative() {
        Optional<Review> optionalReview = reviewRepository.findById(-1L);
        assertTrue(optionalReview.isEmpty());
    }

    @Test
    @DisplayName("Save-Positive")
    void test_save_positive() {
        Review review = new Review(3.8f, "Good Content", 12, "ISBN-0001");
        reviewRepository.save(review);

        List<Review> list = reviewRepository.findAll();
        assertFalse(list.isEmpty());
        assertEquals(review, list.getFirst());
    }

    @Test
    @DisplayName("Update-Positive")
    void test_update_positive() {
        Review review = new Review(1f, "Bad Content", 10, "ISBN-0002");
        testEntityManager.persist(review);
        review.setComment("Worst Book Ever!");

        Optional<Review> optionalReview = reviewRepository.findById(review.getReviewId());
        assertTrue(optionalReview.isPresent());
        assertEquals(review, optionalReview.get());
    }


    @Test
    @DisplayName("DeleteById-Positive")
    void test_deleteById_positive() {
        Review review = new Review(1f, "Bad Content", 10, "ISBN-0002");
        testEntityManager.persist(review);

        reviewRepository.deleteById(review.getReviewId());
        Optional<Review> optionalReview = reviewRepository.findById(review.getReviewId());
        assertFalse(optionalReview.isPresent());
    }

    @Test
    @DisplayName("DeleteById-Negative")
    void test_deleteById_negative() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> reviewRepository.deleteById(null));
    }


    @Test
    @DisplayName("Delete-Positive")
    void test_delete_positive() {
        Review review = new Review(1f, "Bad Content", 10, "ISBN-0002");
        testEntityManager.persist(review);

        reviewRepository.delete(review);
        Optional<Review> optionalReview = reviewRepository.findById(review.getReviewId());
        assertFalse(optionalReview.isPresent());
    }

    @Test
    @DisplayName("Delete-Negative")
    void test_delete_negative() {
        assertThrows(InvalidDataAccessApiUsageException.class, () -> reviewRepository.delete(null), "Erorr not thrown in delete");
    }


    @Test
    @DisplayName("FindByUserId-Positive")
    void test_findByUserId_positive() {
        Review review1 = new Review(1f, "Bad Content", 10, "ISBN-0002");
        Review review2 = new Review(5f, "Best Content", 10, "ISBN-0200");
        testEntityManager.persist(review1);
        testEntityManager.persist(review2);

        List<Review> actual = reviewRepository.findByUserId(review1.getUserId());
        List<Review> expected = List.of(review1, review2);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("FindByUserId-Negative")
    void test_findByUserId_negative() {
        List<Review> reviewList = reviewRepository.findByUserId(-1L);
        assertTrue(reviewList.isEmpty());
    }

    @Test
    @DisplayName("FindByBookId-Positive")
    void test_findByBookId_positive() {
        Review review1 = new Review(1f, "Bad Content", 11, "ISBN-1212");
        Review review2 = new Review(5f, "Best Content", 12, "ISBN-1212");
        testEntityManager.persist(review1);
        testEntityManager.persist(review2);

        List<Review> actual = reviewRepository.findByBookId(review1.getBookId());
        List<Review> expected = List.of(review1, review2);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("FindByBookId-Negative")
    void test_findByBookId_negative() {
        List<Review> reviewList = reviewRepository.findByBookId("COM");
        assertTrue(reviewList.isEmpty());
    }
}