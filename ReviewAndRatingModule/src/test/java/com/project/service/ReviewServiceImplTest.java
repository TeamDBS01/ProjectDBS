package com.project.service;


import com.project.ReviewAndRatingModuleApplication;
import com.project.dto.ReviewDTO;
import com.project.dto.UserDTO;
import com.project.enums.Role;
import com.project.exception.ReviewNotFoundException;
import com.project.exception.UserNotAuthorizedException;
import com.project.exception.UserNotFoundException;
import com.project.models.Review;
import com.project.repositories.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("preview")
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {ReviewAndRatingModuleApplication.class})
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ModelMapper mapper;
    @Mock
    private UserService userService;
    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review review;
    private ReviewDTO reviewDTO;
    private UserDTO userDTO;
    private static final float RATING = 4.5f;
    private static final String COMMENT = "Great Book";
    private static final long USER_ID = 2;
    private static final String BOOK_ID = "ISBN-GRT2BK";

    @BeforeEach
    void setup() {
        review = new Review(1, 4.5f, "Great Book", 2, "ISBN-GRT2BK");
        reviewDTO = new ReviewDTO(1, 4.5f, "Great Book", 2, "ISBN-GRT2BK");
        userDTO = new UserDTO(12L, "name", "mail", "pass", Role.CUSTOMER);
    }

    @Test
    @DisplayName("AddReview-Positive")
    void test_addReview_positive() {
        when(reviewRepository.save(any())).thenReturn(review);
        when(mapper.map(any(), any())).thenReturn(reviewDTO);
        ReviewDTO reviewDTOActual = reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID);
        verify(reviewRepository).save(any());
        verify(mapper).map(any(), any());
        assertEquals(reviewDTO, reviewDTOActual);
    }

    @Test
    @DisplayName("AddReview-Negative")
    void test_addReview_negative() {
        when(reviewRepository.save(any())).thenReturn(new IllegalArgumentException());
        assertThrows(ClassCastException.class,
                () -> reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID),
                "Error not thrown in addReview");
        verify(reviewRepository).save(any());
        assertFalse(reviewRepository.findById(review.getReviewId()).isPresent());
    }


    @Test
    @DisplayName("UpdateReview-Positive-SameUser")
    void test_updateReview_positive_sameUser() {
        when(userService.getUserById(USER_ID)).thenReturn(ResponseEntity.ok(userDTO));
        when(mapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(reviewRepository.save(any())).thenReturn(review);
        when(mapper.map(reviewDTO, Review.class)).thenReturn(review);
        ReviewDTO actual = null;
        try {
            actual = reviewService.updateReview(USER_ID, reviewDTO);
        } catch (UserNotFoundException | UserNotAuthorizedException e) {
            fail(STR."Error thrown in updateReview: \{e}");
        }
        verify(userService).getUserById(USER_ID);
        verify(mapper).map(reviewDTO, Review.class);
        verify(reviewRepository).save(any());
        verify(mapper).map(review, ReviewDTO.class);
        assertEquals(reviewDTO, actual);
    }


    @Test
    @DisplayName("UpdateReview-Positive-Admin")
    void test_updateReview_positive_admin() {
        userDTO.setRole(Role.ADMIN);
        when(userService.getUserById(4L)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(mapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(reviewRepository.save(any())).thenReturn(review);
        when(mapper.map(reviewDTO, Review.class)).thenReturn(review);
        ReviewDTO actual = null;
        try {
            actual = reviewService.updateReview(4L, reviewDTO);
        } catch (UserNotFoundException | UserNotAuthorizedException e) {
            fail(STR."Error thrown in updateReview: \{e}");
        }
        verify(userService).getUserById(4L);
        verify(mapper).map(reviewDTO, Review.class);
        verify(reviewRepository).save(any());
        verify(mapper).map(review, ReviewDTO.class);
        assertEquals(reviewDTO, actual);
    }

    @Test
    @DisplayName("UpdateReview-Negative-Unauthorized")
    void test_updateReview_negative_unauthorized() {
        when(userService.getUserById(4L)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        assertThrows(
                UserNotAuthorizedException.class,
                () -> reviewService.updateReview(4L, reviewDTO),
                "Error not thrown in UpdateReview for User Unauthorized");
        verify(userService).getUserById(4L);
    }

    @Test
    @DisplayName("UpdateReview-Negative-UserNotFound")
    void test_updateReview_negative_userNotFound() {
        when(userService.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        assertThrows(
                UserNotFoundException.class,
                () -> reviewService.updateReview(USER_ID, reviewDTO),
                "Error not thrown in UpdateReview for User Not Found");
        verify(userService).getUserById(USER_ID);
    }

    @Test
    @DisplayName("DeleteReview-Positive-SameUser")
    void test_deleteReview_positive_sameUser() {
        userDTO.setUserId(USER_ID);
        when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.of(review));
        when(mapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(userService.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        boolean actual = false;
        try {
            actual = reviewService.deleteReview(USER_ID, review.getReviewId());
        } catch (UserNotFoundException | UserNotAuthorizedException | ReviewNotFoundException e) {
            fail(STR."Error thrown in Delete: \{e}");
        }
        verify(reviewRepository).deleteById(review.getReviewId());
        verify(userService).getUserById(USER_ID);
        verify(mapper).map(review, ReviewDTO.class);
        assertTrue(actual);
    }

    @Test
    @DisplayName("DeleteReview-Positive-Admin")
    void test_deleteReview_positive_admin() {
        userDTO.setRole(Role.ADMIN);
        userDTO.setUserId(USER_ID);
        reviewDTO.setUserId(4L);
        when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.of(review));
        when(mapper.map(any(), any())).thenReturn(reviewDTO);
        when(userService.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        boolean actual = false;
        try {
            actual = reviewService.deleteReview(USER_ID, review.getReviewId());
        } catch (UserNotFoundException | UserNotAuthorizedException | ReviewNotFoundException e) {
            fail(STR."Error thrown in Delete: \{e.getMessage()}");
        }
        verify(reviewRepository).deleteById(review.getReviewId());
        verify(userService).getUserById(USER_ID);
        verify(mapper).map(any(), any());
        assertTrue(actual);
    }

    @Test
    @DisplayName("DeleteReview-Negative-UserNotAuthorized")
    void test_deleteReview_negative_userNotAuthorized() {
        reviewDTO.setUserId(3L);
        when(reviewRepository.findById(any())).thenReturn(Optional.of(review));
        when(mapper.map(any(), any())).thenReturn(reviewDTO);
        when(userService.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        assertThrows(
                UserNotAuthorizedException.class,
                () -> reviewService.deleteReview(USER_ID, review.getReviewId()),
                "Error not thrown in DeleteReview");
        verify(reviewRepository).findById(review.getReviewId());
        verify(userService).getUserById(USER_ID);
        verify(mapper).map(any(), any());
    }

    @Test
    @DisplayName("DeleteReview-Negative-UserNotFound")
    void test_deleteReview_negative_userNotFound() {
        when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.of(review));
        when(userService.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        assertThrows(
                UserNotFoundException.class,
                () -> reviewService.deleteReview(USER_ID, review.getReviewId()),
                "Error not thrown in DeleteReview");
        verify(reviewRepository).findById(review.getReviewId());
        verify(userService).getUserById(USER_ID);
    }

    @Test
    @DisplayName("DeleteReview-Negative-ReviewNotFound")
    void test_deleteReview_negative_reviewNotFound() {
        when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.empty());
        assertThrows(
                ReviewNotFoundException.class,
                () -> reviewService.deleteReview(2, review.getReviewId()),
                "Error not thrown in DeleteReview");
        verify(reviewRepository).findById(review.getReviewId());
    }

//    @Test
//    @DisplayName("RetrieveAllReviews-Positive-WithOneReview")
//    void test_retrieveAllReviews_positive_withOneReview() {
//        when(reviewRepository.findAll()).thenAnswer((_ -> List.of(review)));
//        when(mapper.map(any(), any())).thenAnswer((_ -> reviewDTO));
//        try {
//            List<ReviewDTO> actual = reviewService.retrieveAllReviews();
//            verify(reviewRepository).findAll();
//            verify(mapper).map(review, ReviewDTO.class);
//            assertEquals(1, actual.size());
//        } catch (Exception | ReviewNotFoundException e) {
//            fail(STR."Error thrown in RetrieveAll \{e.getMessage()}");
//        }
//    }

//    @Test
//    @DisplayName("RetrieveAllReviews-Positive-WithMultipleReviews")
//    void test_retrieveAllReviews_positive_withMultipleReviews() {
//        Review review1 = new Review(0.1f, "Worst Book", 4, "ISBN-NTGD");
//        ReviewDTO reviewDTO1 = new ReviewDTO(0, 0.1f, "Worst Book", 4, "ISBN-NTGD");
//        when(reviewRepository.findAll()).thenAnswer((_ -> List.of(review, review1)));
//        when(mapper.map(review, ReviewDTO.class)).thenAnswer((_ -> reviewDTO));
//        when(mapper.map(review1, ReviewDTO.class)).thenAnswer((_ -> reviewDTO1));
//        List<ReviewDTO> actual = null, expected = List.of(reviewDTO, reviewDTO1);
//        try {
//            actual = reviewService.retrieveAllReviews();
//        } catch (Exception | ReviewNotFoundException e) {
//            fail(STR."Error thrown in RetrieveAll \{e.getMessage()}");
//        }
//        verify(reviewRepository).findAll();
//        verify(mapper).map(review, ReviewDTO.class);
//        assertEquals(expected, actual);
//    }
//
    @Test
    @DisplayName("RetrieveAllReviews-Negative")
    void test_retrieveAllReviews_negative() {
        when(reviewRepository.findAll()).thenReturn(List.of());
        assertThrows(
                ReviewNotFoundException.class,
                () -> reviewService.retrieveAllReviews(),
                "Error not thrown in retireveAll");
        verify(reviewRepository).findAll();
    }

    @Test
    @DisplayName("RetrieveReviewById-Positive")
    void test_retrieveReviewById_positive() {
        when(reviewRepository.findById(any())).thenReturn(Optional.of(review));
        when(mapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        ReviewDTO actual = null;
        try {
            actual = reviewService.retrieveReviewById(review.getReviewId());
        } catch (ReviewNotFoundException e) {
            fail(STR."Error thrown in RetrieveById \{e.getMessage()}");
        }
        verify(reviewRepository).findById(any());
        verify(mapper).map(review, ReviewDTO.class);
        assertEquals(reviewDTO, actual);
    }

    @Test
    @DisplayName("RetrieveReviewById-Negative")
    void test_retrieveReviewById_negative() {
        when(reviewRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(
                ReviewNotFoundException.class,
                () -> reviewService.retrieveReviewById(-1L),
                "Error not thrown in getById");
        verify(reviewRepository).findById(any());
    }

    @Test
    @DisplayName("RetrieveAllReviewsByUserId-Positive")
    void test_retrieveAllReviewsByUserId_positive() {
        Review review2 = new Review(3f, "Good Content", 2L, "ISBN-3080");
        ReviewDTO reviewDTO2 = new ReviewDTO(review2.getReviewId(), 3f, "Good Content", 2L, "ISBN-3080");
        when(reviewRepository.findByUserId(USER_ID)).thenReturn(List.of(review, review2));
        when(mapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(mapper.map(review2, ReviewDTO.class)).thenReturn(reviewDTO2);
        List<ReviewDTO> actual = null;
        List<ReviewDTO> expected = List.of(reviewDTO, reviewDTO2);
        try {
            actual = reviewService.retrieveAllReviewsByUserId(review.getUserId());
        } catch (ReviewNotFoundException e) {
            fail(STR."Error thrown in RetrieveById \{e.getMessage()}");
        }
        verify(reviewRepository).findByUserId(USER_ID);
        verify(mapper, times(2)).map(any(), any());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("RetrieveAllReviewsByUserId-Negative-ReviewNotFound")
    void test_retrieveAllReviewsByUserId_negative_reviewNotFound() {
        when(reviewRepository.findByUserId(USER_ID)).thenReturn(List.of());
        assertThrows(ReviewNotFoundException.class,
                () -> reviewService.retrieveAllReviewsByUserId(USER_ID),
                "Error not thrown in RetrieveAllReviewsByUserId");
        verify(reviewRepository).findByUserId(USER_ID);
    }
}
