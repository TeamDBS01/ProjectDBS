package com.project.service;

import com.project.dto.ReviewDTO;
import com.project.exception.*;
import com.project.feign.BookClient;
import com.project.feign.UserClient;
import com.project.models.Review;
import com.project.repositories.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("preview")
@SpringBootTest
@ActiveProfiles("spytest")
@ExtendWith(MockitoExtension.class)
class ReviewServiceImplSpyTest {

    private static final float RATING = 4.5f;
    private static final String COMMENT = "Great Book";
    private static final long USER_ID = 24L;
    private static final String BOOK_ID = "ISBN-1212";
    private static final String USER_NAME = "daya";
    private static final String BOOK_TITLE = "The Great Programmer's Guide";
    private static long SIZE;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private UserClient userClient;
    @Autowired
    private BookClient bookClient;
    private ReviewServiceImpl reviewService;
    private Review review;
    private ReviewDTO reviewDTO;

    @BeforeEach
    void setup() {
        reviewService = new ReviewServiceImpl(reviewRepository, userClient, bookClient, mapper);
        review = new Review(RATING, COMMENT, USER_ID, BOOK_ID);
        reviewDTO = new ReviewDTO(1L, RATING, COMMENT, USER_ID, BOOK_ID, USER_NAME, BOOK_TITLE);
        SIZE = reviewRepository.count();
    }

    @Test
    @DisplayName("AddReview-Positive")
    void test_addReview_positive() {
        ReviewDTO reviewDTOActual = null;
        try {
            reviewDTOActual = reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID);
        } catch (UserNotFoundException | BookNotFoundException | ServiceUnavailableException e) {
            fail(STR."Error thrown:  \{e.toString()}");
        }
        reviewDTO.setReviewId(reviewDTOActual.getReviewId());
        assertEquals(reviewDTO, reviewDTOActual);
        assertEquals(++SIZE, reviewRepository.count());
    }

    @Test
    @DisplayName("AddReview-Negative-UserNotFound")
    void test_addReview_negative_userNotFound() {
        assertThrows(UserNotFoundException.class,
                () -> reviewService.addReview(RATING, COMMENT, 1L, BOOK_ID),
                "Error not thrown in addReview");
        assertEquals(SIZE, reviewRepository.count());
    }

    @Test
    @DisplayName("AddReview-Negative-BookNotFound")
    void test_addReview_negative_bookNotFound() {
        assertThrows(BookNotFoundException.class,
                () -> reviewService.addReview(RATING, COMMENT, USER_ID, "BOOK_ID"),
                "Error not thrown in addReview");
        assertEquals(SIZE, reviewRepository.count());
    }


    @Test
    @DisplayName("UpdateReview-Positive-SameUser")
    void test_updateReview_positive_sameUser() {
        ReviewDTO actual = null;
        reviewDTO.setReviewId(reviewRepository.save(review).getReviewId());
        reviewDTO.setComment("Gud Book");
        try {
            actual = reviewService.updateReview(USER_ID, reviewDTO);
        } catch (UserNotFoundException | UserNotAuthorizedException | IDMismatchException | BookNotFoundException |
                 ServiceUnavailableException e) {
            fail(STR."Error thrown in updateReview: \{e}");
        }
        assertEquals(reviewDTO, actual);
    }


    @Test
    @DisplayName("UpdateReview-Positive-Admin")
    void test_updateReview_positive_admin() {
        reviewDTO.setReviewId(reviewRepository.save(review).getReviewId());
        reviewDTO.setComment("Gud Book");
        ReviewDTO actual = null;
        try {
            actual = reviewService.updateReview(11L, reviewDTO);
        } catch (UserNotFoundException | UserNotAuthorizedException | IDMismatchException | BookNotFoundException |
                 ServiceUnavailableException e) {
            fail(STR."Error thrown in updateReview: \{e}");
        }
        assertEquals(reviewDTO, actual);
    }

    @Test
    @DisplayName("UpdateReview-Negative-Unauthorized")
    void test_updateReview_negative_unauthorized() {
        reviewDTO.setReviewId(reviewRepository.save(review).getReviewId());
        reviewDTO.setComment("Gud Book");
        assertThrows(
                UserNotAuthorizedException.class,
                () -> reviewService.updateReview(10L, reviewDTO),
                "Error not thrown in UpdateReview for User Unauthorized");
    }

    @Test
    @DisplayName("UpdateReview-Negative-ReviewIDMismatch")
    void test_updateReview_negative_reviewIDMismatch() {
        reviewDTO.setReviewId(reviewRepository.save(review).getReviewId());
        reviewDTO.setReviewId(-1L);
        assertThrows(
                IDMismatchException.class,
                () -> reviewService.updateReview(USER_ID, reviewDTO),
                "Error not thrown in UpdateReview for Review ID Mismatch");
    }

    @Test
    @DisplayName("UpdateReview-Negative-UserIDMismatch")
    void test_updateReview_negative_userIDMismatch() {
        reviewDTO.setReviewId(reviewRepository.save(review).getReviewId());
        reviewDTO.setUserId(11L);
        assertThrows(
                IDMismatchException.class,
                () -> reviewService.updateReview(USER_ID, reviewDTO),
                "Error not thrown in UpdateReview for User ID Mismatch");
    }

    @Test
    @DisplayName("UpdateReview-Negative-BookIDMismatch")
    void test_updateReview_negative_bookIDMismatch() {
        reviewDTO.setReviewId(reviewRepository.save(review).getReviewId());
        reviewDTO.setBookId("B001");
        assertThrows(
                IDMismatchException.class,
                () -> reviewService.updateReview(USER_ID, reviewDTO),
                "Error not thrown in UpdateReview for Book ID Mismatch");
    }


    @Test
    @DisplayName("UpdateReview-Negative-BookNotFound")
    void test_updateReview_negative_bookNotFound() {
        reviewDTO.setReviewId(reviewRepository.save(review).getReviewId());
        reviewDTO.setBookId("Book-NTFND");
        assertThrows(
                BookNotFoundException.class,
                () -> reviewService.updateReview(USER_ID, reviewDTO),
                "Error not thrown in UpdateReview for Book Not Found");
    }

    @Test
    @DisplayName("UpdateReview-Negative-UserNotFound")
    void test_updateReview_negative_userNotFound() {
        assertThrows(
                UserNotFoundException.class,
                () -> reviewService.updateReview(-1L, reviewDTO),
                "Error not thrown in UpdateReview for User Not Found");
    }

    @Test
    @DisplayName("DeleteReview-Positive-SameUser")
    void test_deleteReview_positive_sameUser() {
        long reviewId = reviewRepository.save(review).getReviewId();
        boolean actual = false;
        try {
            actual = reviewService.deleteReview(USER_ID, reviewId);
        } catch (UserNotFoundException | UserNotAuthorizedException | ReviewNotFoundException |
                 ServiceUnavailableException e) {
            fail(STR."Error thrown in Delete: \{e}");
        }
        assertTrue(actual);
    }

    @Test
    @DisplayName("DeleteReview-Positive-Admin")
    void test_deleteReview_positive_admin() {
        reviewDTO.setUserId(12L);
        long reviewId = reviewRepository.save(review).getReviewId();
        boolean actual = false;
        try {
            actual = reviewService.deleteReview(11L, reviewId);
        } catch (UserNotFoundException | UserNotAuthorizedException | ReviewNotFoundException |
                 ServiceUnavailableException e) {
            fail(STR."Error thrown in Delete: \{e.getMessage()}");
        }
        assertTrue(actual);
    }

    @Test
    @DisplayName("DeleteReview-Negative-UserNotAuthorized")
    void test_deleteReview_negative_userNotAuthorized() {
        long reviewId = reviewRepository.save(review).getReviewId();
        assertThrows(
                UserNotAuthorizedException.class,
                () -> reviewService.deleteReview(10L, reviewId),
                "Error not thrown in DeleteReview");
    }

    @Test
    @DisplayName("DeleteReview-Negative-UserNotFound")
    void test_deleteReview_negative_userNotFound() {
        long reviewId = reviewRepository.save(review).getReviewId();
        assertThrows(
                UserNotFoundException.class,
                () -> reviewService.deleteReview(1L, reviewId),
                "Error not thrown in DeleteReview");
    }

    @Test
    @DisplayName("DeleteReview-Negative-ReviewNotFound")
    void test_deleteReview_negative_reviewNotFound() {
        assertThrows(
                ReviewNotFoundException.class,
                () -> reviewService.deleteReview(USER_ID, -1L),
                "Error not thrown in DeleteReview");
    }

    @Test
    @DisplayName("RetrieveAllReviews-Positive-WithOneReview")
    void test_retrieveAllReviews_positive_withOneReview() {
        reviewRepository.deleteAll();
        reviewRepository.save(review);
        try {
            List<ReviewDTO> actual = reviewService.retrieveAllReviews();
            assertEquals(1, actual.size());
        } catch (ReviewNotFoundException | ServiceUnavailableException e) {
            fail(STR."Error thrown in RetrieveAll \{e.getMessage()}");
        }
    }

    @Test
    @DisplayName("RetrieveAllReviews-Positive-WithMultipleReviews")
    void test_retrieveAllReviews_positive_withMultipleReviews() {
        reviewRepository.deleteAll();
        Review review1 = new Review(0.1f, "Worst Book", 4, "ISBN-NTGD");
        ReviewDTO reviewDTO1 = new ReviewDTO(0L, 0.1f, "Worst Book", 4L, "ISBN-NTGD", USER_NAME, BOOK_TITLE);
        reviewDTO.setReviewId(reviewRepository.save(review).getReviewId());
        reviewDTO1.setReviewId(reviewRepository.save(review1).getReviewId());
        List<ReviewDTO> actual = null, expected = List.of(reviewDTO, reviewDTO1);
        try {
            actual = reviewService.retrieveAllReviews();
        } catch (ReviewNotFoundException | ServiceUnavailableException e) {
            fail(STR."Error thrown in RetrieveAll \{e.getMessage()}");
        }
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("RetrieveAllReviews-Negative")
    void test_retrieveAllReviews_negative() {
        reviewRepository.deleteAll();
        assertThrows(
                ReviewNotFoundException.class,
                () -> reviewService.retrieveAllReviews(),
                "Error not thrown in retireveAll");
    }

    @Test
    @DisplayName("RetrieveReviewById-Positive")
    void test_retrieveReviewById_positive() {
        ReviewDTO actual = null;
        long reviewId = reviewRepository.save(review).getReviewId();
        reviewDTO.setReviewId(reviewId);
        try {
            actual = reviewService.retrieveReviewById(reviewId);
        } catch (ReviewNotFoundException | ServiceUnavailableException e) {
            fail(STR."Error thrown in RetrieveById \{e.getMessage()}");
        }
        assertEquals(reviewDTO, actual);
    }

    @Test
    @DisplayName("RetrieveReviewById-Negative")
    void test_retrieveReviewById_negative() {
        reviewRepository.deleteAll();
        assertThrows(
                ReviewNotFoundException.class,
                () -> reviewService.retrieveReviewById(-1L),
                "Error not thrown in getById");
    }

    @Test
    @DisplayName("RetrieveAllReviewsByUserId-Positive")
    void test_retrieveAllReviewsByUserId_positive() {
        reviewRepository.deleteAll();
        Review review2 = new Review(3f, "Good Content", USER_ID, "ISBN-3080");
        ReviewDTO reviewDTO2 = new ReviewDTO(review2.getReviewId(), 3f, "Good Content", USER_ID, "ISBN-3080", USER_NAME, BOOK_TITLE);
        List<ReviewDTO> actual = null;
        List<ReviewDTO> expected = List.of(reviewDTO, reviewDTO2);
        reviewDTO.setReviewId(reviewRepository.save(review).getReviewId());
        reviewDTO2.setReviewId(reviewRepository.save(review2).getReviewId());
        try {
            actual = reviewService.retrieveAllReviewsByUserId(review.getUserId());
        } catch (ReviewNotFoundException | ServiceUnavailableException e) {
            fail(STR."Error thrown in RetrieveById \{e.getMessage()}");
        }
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("RetrieveAllReviewsByUserId-Negative-ReviewNotFound")
    void test_retrieveAllReviewsByUserId_negative_reviewNotFound() {
        reviewRepository.deleteAll();
        assertThrows(ReviewNotFoundException.class,
                () -> reviewService.retrieveAllReviewsByUserId(USER_ID),
                "Error not thrown in RetrieveAllReviewsByUserId");
    }

    @Test
    @DisplayName("RetrieveAllReviewsByBookId-Positive")
    void test_retrieveAllReviewsByBookId_positive() {
        reviewRepository.deleteAll();
        Review review2 = new Review(3f, "Good Content", 11L, BOOK_ID);
        ReviewDTO reviewDTO2 = new ReviewDTO(review2.getReviewId(), 3f, "Good Content", 11L, BOOK_ID, USER_NAME, BOOK_TITLE);
        List<ReviewDTO> actual = null;
        List<ReviewDTO> expected = List.of(reviewDTO, reviewDTO2);
        reviewDTO.setReviewId(reviewRepository.save(review).getReviewId());
        reviewDTO2.setReviewId(reviewRepository.save(review2).getReviewId());
        try {
            actual = reviewService.retrieveAllReviewsByBookId(review.getBookId());
        } catch (ReviewNotFoundException | ServiceUnavailableException e) {
            fail(STR."Error thrown in RetrieveById \{e.getMessage()}");
        }
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("RetrieveAllReviewsByBookId-Negative-ReviewNotFound")
    void test_retrieveAllReviewsByBookId_negative_reviewNotFound() {
        reviewRepository.deleteAll();
        assertThrows(ReviewNotFoundException.class,
                () -> reviewService.retrieveAllReviewsByBookId(BOOK_ID),
                "Error not thrown in RetrieveAllReviewsByBookId");
    }

    @Test
    @DisplayName("RetrieveAverageRating-Positive")
    void test_retrieveAverageRating_positive() {
        reviewRepository.deleteAll();
        reviewRepository.save(review);
        List<Float> expected = new ArrayList<>(List.of(review.getRating(), 1f));
        List<Float> actual = reviewService.retrieveAverageRating(BOOK_ID);
        assertEquals(expected, actual);
    }
}