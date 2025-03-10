package com.project.service;

import com.project.ReviewAndRatingModuleApplication;
import com.project.dto.ReviewDTO;
import com.project.dto.UserDTO;
import com.project.enums.Role;
import com.project.exception.ReviewNotFoundException;
import com.project.models.Review;
import com.project.repositories.ReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {ReviewAndRatingModuleApplication.class})
@ActiveProfiles("test")
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ModelMapper mapper;
    @Mock
    private UserService userService;
    @InjectMocks
    private ReviewServiceImpl reviewService;

    private static final Review review = new Review(1, 4.5f, "Great Book", 2,"ISBN-GRT2BK");
    private static final ReviewDTO reviewDTO = new ReviewDTO(1, 4.5f, "Great Book", 2,"ISBN-GRT2BK");
    private static final UserDTO userDTO = new UserDTO(12L, "name", "mail", "pass", Role.CUSTOMER);
    private static final float RATING = 4.5f;
    private static final String COMMENT = "Great Book";
    private static final long USER_ID = 2;
    private static final String BOOK_ID = "ISBN-GRT2BK";

    AutoCloseable autoCloseable;
    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception { autoCloseable.close(); }

    @Test
    @DisplayName("AddReview-Positive")
    void test_addReview_positive() {
        when(reviewRepository.save(any())).thenReturn(review);
        boolean actual = reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID);
        verify(reviewRepository).save(any());
        assertTrue(actual);
    }

    @Test
    @DisplayName("AddReview-Negative")
    void test_addReview_negative() {
        when(reviewRepository.save(any())).thenThrow(new RuntimeException("Error"));
        boolean actual = reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID);
        verify(reviewRepository).save(any());
        assertFalse(reviewRepository.findById(review.getReviewId()).isPresent());
        assertFalse(actual);
    }


    @Test
    @DisplayName("UpdateReview-Positive-SameUser")
    void test_updateReview_positive_sameUser() {
        when(userService.getUserById(2)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(mapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(reviewRepository.save(any())).thenReturn(review);
        when(mapper.map(reviewDTO, Review.class)).thenReturn(review);
        ReviewDTO actual = null;
        try {
            actual = reviewService.updateReview(2, reviewDTO);
        } catch (Exception e) {
            fail("Error thrown");
        }
        verify(userService).getUserById(2);
        verify(mapper).map(reviewDTO, Review.class);
        verify(reviewRepository).save(any());
        verify(mapper).map(review, ReviewDTO.class);
        assertEquals(reviewDTO, actual);
    }


    @Test
    @DisplayName("UpdateReview-Positive-Admin")
    void test_updateReview_positive_admin() {
        userDTO.setRole(Role.ADMIN);
        when(userService.getUserById(4)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(mapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(reviewRepository.save(any())).thenReturn(review);
        when(mapper.map(reviewDTO, Review.class)).thenReturn(review);
        ReviewDTO actual = null;
        try {
            actual = reviewService.updateReview(4, reviewDTO);
        } catch (Exception e) {
            fail("Error thrown");
        }
        verify(userService).getUserById(4);
        verify(mapper).map(reviewDTO, Review.class);
        verify(reviewRepository).save(any());
        verify(mapper).map(review, ReviewDTO.class);
        assertEquals(reviewDTO, actual);
    }

    @Test
    @DisplayName("UpdateReview-Negative")
    void test_updateReview_negative() {
        userDTO.setRole(Role.CUSTOMER);
        when(userService.getUserById(4)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(mapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(reviewRepository.save(any())).thenReturn(review);
        when(mapper.map(reviewDTO, Review.class)).thenReturn(review);

        try {
            reviewService.updateReview(4, reviewDTO);
            fail("Error not thrown in UpdateReview");
        } catch (Exception e) {
            assertTrue(true);
            verify(userService).getUserById(4);
        }
    }

    @Test
    @DisplayName("DeleteReview-Positive")
    void test_deleteReview_positive() {
        when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.of(review));
        when(mapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(userService.getUserById(2L)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        boolean actual = false;
        try {
            actual = reviewService.deleteReview(2, review.getReviewId());
        } catch (Exception | ReviewNotFoundException e) {
            fail("Error thrown in Delete: " + e.getMessage());
        }
        verify(reviewRepository).deleteById(review.getReviewId());
        verify(userService).getUserById(2);
        verify(mapper).map(review, ReviewDTO.class);
        assertTrue(actual);
    }

    @Test
    @DisplayName("DeleteReview-Negative")
    void test_deleteReview_negative() {
        when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.empty());

        assertThrows(
                ReviewNotFoundException.class,
                () -> reviewService.deleteReview(2, review.getReviewId()),
                "Error not thrown in DeleteReview");
//        try {
//            reviewService.deleteReview(2, review.getReviewId());
//            fail("Error not thrown in DeleteReview");
//        } catch (Exception | ReviewNotFoundException e) {
//            assertTrue(true);
//        }
        verify(reviewRepository).findById(review.getReviewId());
    }

    @Test
    @DisplayName("GetAllReview-Positive-WithOneEntity")
    void test_getAllReview_positive_withOneEntity() {
        when(reviewRepository.findAll()).thenAnswer((invocation -> List.of(review)));
        when(mapper.map(review, ReviewDTO.class)).thenAnswer((invocation -> reviewDTO));
        List<ReviewDTO> actual = null;
        try {
            actual = reviewService.getAllReviews();
        } catch (Exception | ReviewNotFoundException e) {
            fail("Error thrown in GetAll " + e.getMessage());
        }
        verify(reviewRepository).findAll();
        verify(mapper).map(review, ReviewDTO.class);
        assertFalse(actual.isEmpty());
    }

    @Test
    @DisplayName("GetAllReview-Negative")
    void test_getAllReview_negative() {
        when(reviewRepository.findAll()).thenReturn(null);
//        assertThrows(
//                ReviewNotFoundException.class,
//                () -> reviewService.getAllReviews(),
//                "Error not thrown in getAll");
        try {
            reviewService.getAllReviews();
            fail("Error not thrown in getAll");
        } catch (Exception | ReviewNotFoundException e) {
            assertTrue(true);
        }
        verify(reviewRepository).findAll();
    }

    @Test
    @DisplayName("GetReviewById-Positive")
    void test_getReviewById_positive() {
        when(reviewRepository.findById(any())).thenReturn(Optional.of(review));
        when(mapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        ReviewDTO actual = null;
        try {
            actual = reviewService.getReviewById(review.getReviewId());
        } catch (Exception | ReviewNotFoundException e) {
            fail("Error thrown in GetById " + e.getMessage());
        }
        verify(reviewRepository).findById(any());
        verify(mapper).map(review, ReviewDTO.class);
        assertEquals(reviewDTO, actual);
    }

    @Test
    @DisplayName("GetReviewById-Negative")
    void test_getReviewById_negative() {
        when(reviewRepository.findById(any())).thenReturn(null);
//        assertThrows(
//                ReviewNotFoundException.class,
//                () -> reviewService.getReviewById(-1L),
//                "Error not thrown in getById");
        try {
            reviewService.getReviewById(-1L);
            fail("Error not thrown in getById");
        } catch (Exception | ReviewNotFoundException e) {
            assertTrue(true);
        }
        verify(reviewRepository).findById(any());
    }
}