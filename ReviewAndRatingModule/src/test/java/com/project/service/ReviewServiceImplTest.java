package com.project.service;

import com.project.dto.BookDTO;
import com.project.dto.ReviewDTO;
import com.project.dto.UserDTO;
import com.project.enums.Role;
import com.project.exception.*;
import com.project.feign.BookClient;
import com.project.feign.UserClient;
import com.project.models.Review;
import com.project.repositories.ReviewRepository;
import feign.FeignException;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("preview")
@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ModelMapper mapper;
    @Mock
    private UserClient userClient;
    @Mock
    private BookClient bookClient;
    @InjectMocks
    private ReviewServiceImpl reviewService;

    private Review review;
    private ReviewDTO reviewDTO;
    private UserDTO userDTO;
    private BookDTO bookDTO;
    private static final float RATING = 4.5f;
    private static final String COMMENT = "Great Book";
    private static final long USER_ID = 2;
    private static final String BOOK_ID = "ISBN-GRT2BK";

    @BeforeEach
    void setup() {
        review = new Review(1L, RATING, COMMENT, USER_ID, BOOK_ID);
        reviewDTO = new ReviewDTO(1L, RATING, COMMENT, USER_ID, BOOK_ID);
        userDTO = new UserDTO(12L, "name", "mail", "pass", Role.CUSTOMER);
        bookDTO = new BookDTO("ISBN-1212", "title", 2000, 9L, 8, 7);
    }

    @Test
    @DisplayName("AddReview-Positive")
    void test_addReview_positive() {
        when(reviewRepository.save(any())).thenReturn(review);
        when(mapper.map(any(), any())).thenReturn(reviewDTO);
        when(userClient.getUserById(USER_ID)).thenReturn(ResponseEntity.ok(userDTO));
        when(bookClient.getBookById(BOOK_ID)).thenAnswer(invocation -> ResponseEntity.ok(bookDTO));
        ReviewDTO reviewDTOActual = null;
        try {
            reviewDTOActual = reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID);
        } catch (UserNotFoundException | BookNotFoundException e) {
            fail(STR."Error thrown:  \{e.toString()}");
        }
        verify(reviewRepository).save(any());
        verify(mapper).map(any(), any());
        verify(userClient).getUserById(USER_ID);
        verify(bookClient).getBookById(BOOK_ID);
        assertEquals(reviewDTO, reviewDTOActual);
    }

    @Test
    @DisplayName("AddReview-Negative")
    void test_addReview_negative() {
        when(reviewRepository.save(any())).thenReturn(new IllegalArgumentException());
        when(userClient.getUserById(USER_ID)).thenReturn(ResponseEntity.ok(userDTO));
        when(bookClient.getBookById(BOOK_ID)).thenAnswer(invocation -> ResponseEntity.ok(bookDTO));
        assertThrows(Exception.class,
                () -> reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID),
                "Error not thrown in addReview");
        verify(reviewRepository).save(any());
        verify(userClient).getUserById(USER_ID);
        verify(bookClient).getBookById(BOOK_ID);
        assertFalse(reviewRepository.findById(review.getReviewId()).isPresent());
    }

    @Test
    @DisplayName("AddReview-Negative-UserNotFound")
    void test_addReview_negative_userNotFound() {
        when(userClient.getUserById(USER_ID)).thenReturn(ResponseEntity.ok(null));
        assertThrows(UserNotFoundException.class,
                () -> reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID),
                "Error not thrown in addReview");
        verify(userClient).getUserById(USER_ID);
        assertFalse(reviewRepository.findById(review.getReviewId()).isPresent());
    }

    @Test
    @DisplayName("AddReview-Negative-UserNotFound-EmptyObject")
    void test_addReview_negative_userNotFound_emptyObject() {
        when(userClient.getUserById(USER_ID)).thenReturn(ResponseEntity.ok(new UserDTO()));
        assertThrows(UserNotFoundException.class,
                () -> reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID),
                "Error not thrown in addReview");
        verify(userClient).getUserById(USER_ID);
        assertFalse(reviewRepository.findById(review.getReviewId()).isPresent());
    }

    @Test
    @DisplayName("AddReview-Negative-BookNotFound")
    void test_addReview_negative_bookNotFound() {
        when(userClient.getUserById(USER_ID)).thenReturn(ResponseEntity.ok(userDTO));
        when(bookClient.getBookById(BOOK_ID)).thenThrow(FeignException.class);
        assertThrows(BookNotFoundException.class,
                () -> reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID),
                "Error not thrown in addReview");
        verify(userClient).getUserById(USER_ID);
        verify(bookClient).getBookById(BOOK_ID);
        assertFalse(reviewRepository.findById(review.getReviewId()).isPresent());
    }


    @Test
    @DisplayName("UpdateReview-Positive-SameUser")
    void test_updateReview_positive_sameUser() {
        when(userClient.getUserById(USER_ID)).thenReturn(ResponseEntity.ok(userDTO));
        when(bookClient.getBookById(any())).thenAnswer(invocation -> ResponseEntity.ok(bookDTO));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(review));
        when(mapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(reviewRepository.save(any())).thenReturn(review);
        when(mapper.map(reviewDTO, Review.class)).thenReturn(review);
        ReviewDTO actual = null;
        try {
            actual = reviewService.updateReview(USER_ID, reviewDTO);
        } catch (UserNotFoundException | UserNotAuthorizedException | IDMismatchException | BookNotFoundException e) {
             fail(STR."Error thrown in updateReview: \{e}");
        }
        verify(userClient).getUserById(USER_ID);
        verify(bookClient).getBookById(any());
        verify(reviewRepository).findById(any());
        verify(mapper).map(reviewDTO, Review.class);
        verify(reviewRepository).save(any());
        verify(mapper).map(review, ReviewDTO.class);
        assertEquals(reviewDTO, actual);
    }


    @Test
    @DisplayName("UpdateReview-Positive-Admin")
    void test_updateReview_positive_admin() {
        userDTO.setRole(Role.ADMIN);
        when(userClient.getUserById(4L)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(bookClient.getBookById(any())).thenAnswer(invocation -> ResponseEntity.ok(bookDTO));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(review));
        when(mapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(reviewRepository.save(any())).thenReturn(review);
        when(mapper.map(reviewDTO, Review.class)).thenReturn(review);
        ReviewDTO actual = null;
        try {
            actual = reviewService.updateReview(4L, reviewDTO);
        } catch (UserNotFoundException | UserNotAuthorizedException | IDMismatchException | BookNotFoundException e) {
            fail(STR."Error thrown in updateReview: \{e}");
        }
        verify(userClient).getUserById(4L);
        verify(bookClient).getBookById(any());
        verify(reviewRepository).findById(any());
        verify(mapper).map(reviewDTO, Review.class);
        verify(reviewRepository).save(any());
        verify(mapper).map(review, ReviewDTO.class);
        assertEquals(reviewDTO, actual);
    }

    @Test
    @DisplayName("UpdateReview-Negative-Unauthorized")
    void test_updateReview_negative_unauthorized() {
        when(userClient.getUserById(4L)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(bookClient.getBookById(any())).thenAnswer(invocation -> ResponseEntity.ok(bookDTO));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(review));
        assertThrows(
                UserNotAuthorizedException.class,
                () -> reviewService.updateReview(4L, reviewDTO),
                "Error not thrown in UpdateReview for User Unauthorized");
        verify(reviewRepository).findById(any());
        verify(userClient).getUserById(4L);
        verify(bookClient).getBookById(any());
    }

    @Test
    @DisplayName("UpdateReview-Negative-ReviewIDMismatch")
    void test_updateReview_negative_reviewIDMismatch() {
        when(userClient.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(bookClient.getBookById(any())).thenAnswer(invocation -> ResponseEntity.ok(bookDTO));
        when(reviewRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(
                IDMismatchException.class,
                () -> reviewService.updateReview(USER_ID, reviewDTO),
                "Error not thrown in UpdateReview for Review ID Mismatch");
        verify(userClient).getUserById(USER_ID);
        verify(bookClient).getBookById(any());
        verify(reviewRepository).findById(any());
    }

    @Test
    @DisplayName("UpdateReview-Negative-UserIDMismatch")
    void test_updateReview_negative_userIDMismatch() {
        when(userClient.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(bookClient.getBookById(any())).thenAnswer(invocation -> ResponseEntity.ok(bookDTO));
        review.setUserId(4L);
        when(reviewRepository.findById(any())).thenReturn(Optional.of(review));
        assertThrows(
                IDMismatchException.class,
                () -> reviewService.updateReview(USER_ID, reviewDTO),
                "Error not thrown in UpdateReview for User ID Mismatch");
        verify(userClient).getUserById(USER_ID);
        verify(bookClient).getBookById(any());
        verify(reviewRepository).findById(any());
    }

    @Test
    @DisplayName("UpdateReview-Negative-BookIDMismatch")
    void test_updateReview_negative_bookIDMismatch() {
        when(userClient.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(bookClient.getBookById(any())).thenAnswer(invocation -> ResponseEntity.ok(bookDTO));
        review.setBookId("ISBN-NTFND");
        when(reviewRepository.findById(any())).thenReturn(Optional.of(review));
        assertThrows(
                IDMismatchException.class,
                () -> reviewService.updateReview(USER_ID, reviewDTO),
                "Error not thrown in UpdateReview for Book ID Mismatch");
        verify(userClient).getUserById(USER_ID);
        verify(bookClient).getBookById(any());
        verify(reviewRepository).findById(any());
    }


    @Test
    @DisplayName("UpdateReview-Negative-BookNotFound")
    void test_updateReview_negative_bookNotFound() {
        when(userClient.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(bookClient.getBookById(any())).thenThrow(FeignException.class);
        assertThrows(
                BookNotFoundException.class,
                () -> reviewService.updateReview(USER_ID, reviewDTO),
                "Error not thrown in UpdateReview for Book Not Found");
        verify(userClient).getUserById(USER_ID);
        verify(bookClient).getBookById(any());
    }

    @Test
    @DisplayName("UpdateReview-Negative-UserNotFound")
    void test_updateReview_negative_userNotFound() {
        when(userClient.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        assertThrows(
                UserNotFoundException.class,
                () -> reviewService.updateReview(USER_ID, reviewDTO),
                "Error not thrown in UpdateReview for User Not Found");
        verify(userClient).getUserById(USER_ID);
    }

    @Test
    @DisplayName("UpdateReview-Negative-UserNotFound-EmptyObject")
    void test_updateReview_negative_userNotFound_emptyObject() {
        when(userClient.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(new UserDTO(), HttpStatus.OK));
        assertThrows(
                UserNotFoundException.class,
                () -> reviewService.updateReview(USER_ID, reviewDTO),
                "Error not thrown in UpdateReview for User Not Found");
        verify(userClient).getUserById(USER_ID);
    }

    @Test
    @DisplayName("DeleteReview-Positive-SameUser")
    void test_deleteReview_positive_sameUser() {
        userDTO.setUserId(USER_ID);
        when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.of(review));
        when(mapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(userClient.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        boolean actual = false;
        try {
            actual = reviewService.deleteReview(USER_ID, review.getReviewId());
        } catch (UserNotFoundException | UserNotAuthorizedException | ReviewNotFoundException e) {
            fail(STR."Error thrown in Delete: \{e}");
        }
        verify(reviewRepository).deleteById(review.getReviewId());
        verify(userClient).getUserById(USER_ID);
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
        when(userClient.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        boolean actual = false;
        try {
            actual = reviewService.deleteReview(USER_ID, review.getReviewId());
        } catch (UserNotFoundException | UserNotAuthorizedException | ReviewNotFoundException e) {
            fail(STR."Error thrown in Delete: \{e.getMessage()}");
        }
        verify(reviewRepository).deleteById(review.getReviewId());
        verify(userClient).getUserById(USER_ID);
        verify(mapper).map(any(), any());
        assertTrue(actual);
    }

    @Test
    @DisplayName("DeleteReview-Negative-UserNotAuthorized")
    void test_deleteReview_negative_userNotAuthorized() {
        reviewDTO.setUserId(3L);
        when(reviewRepository.findById(any())).thenReturn(Optional.of(review));
        when(mapper.map(any(), any())).thenReturn(reviewDTO);
        when(userClient.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        assertThrows(
                UserNotAuthorizedException.class,
                () -> reviewService.deleteReview(USER_ID, review.getReviewId()),
                "Error not thrown in DeleteReview");
        verify(reviewRepository).findById(review.getReviewId());
        verify(userClient).getUserById(USER_ID);
        verify(mapper).map(any(), any());
    }

    @Test
    @DisplayName("DeleteReview-Negative-UserNotFound")
    void test_deleteReview_negative_userNotFound() {
        when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.of(review));
        when(userClient.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        assertThrows(
                UserNotFoundException.class,
                () -> reviewService.deleteReview(USER_ID, review.getReviewId()),
                "Error not thrown in DeleteReview");
        verify(reviewRepository).findById(review.getReviewId());
        verify(userClient).getUserById(USER_ID);
    }

    @Test
    @DisplayName("DeleteReview-Negative-UserNotFound-EmptyObject")
    void test_deleteReview_negative_userNotFound_emptyObject() {
        when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.of(review));
        when(userClient.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(new UserDTO(), HttpStatus.OK));
        assertThrows(
                UserNotFoundException.class,
                () -> reviewService.deleteReview(USER_ID, review.getReviewId()),
                "Error not thrown in DeleteReview");
        verify(reviewRepository).findById(review.getReviewId());
        verify(userClient).getUserById(USER_ID);
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

    @Test
    @DisplayName("RetrieveAllReviews-Positive-WithOneReview")
    void test_retrieveAllReviews_positive_withOneReview() {
        when(reviewRepository.findAll()).thenAnswer((invocation -> List.of(review)));
        when(mapper.map(any(), any())).thenAnswer((invocation -> reviewDTO));
        try {
            List<ReviewDTO> actual = reviewService.retrieveAllReviews();
            verify(reviewRepository).findAll();
            verify(mapper).map(review, ReviewDTO.class);
            assertEquals(1, actual.size());
        } catch (ReviewNotFoundException e) {
            fail(STR."Error thrown in RetrieveAll \{e.getMessage()}");
        }
    }

    @Test
    @DisplayName("RetrieveAllReviews-Positive-WithMultipleReviews")
    void test_retrieveAllReviews_positive_withMultipleReviews() {
        Review review1 = new Review(0.1f, "Worst Book", 4, "ISBN-NTGD");
        ReviewDTO reviewDTO1 = new ReviewDTO(0L, 0.1f, "Worst Book", 4L, "ISBN-NTGD");
        when(reviewRepository.findAll()).thenAnswer((invocation -> List.of(review, review1)));
        when(mapper.map(review, ReviewDTO.class)).thenAnswer((invocation -> reviewDTO));
        when(mapper.map(review1, ReviewDTO.class)).thenAnswer((invocation -> reviewDTO1));
        List<ReviewDTO> actual = null, expected = List.of(reviewDTO, reviewDTO1);
        try {
            actual = reviewService.retrieveAllReviews();
        } catch (ReviewNotFoundException e) {
            fail(STR."Error thrown in RetrieveAll \{e.getMessage()}");
        }
        verify(reviewRepository).findAll();
        verify(mapper).map(review, ReviewDTO.class);
        assertEquals(expected, actual);
    }

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

    @Test
    @DisplayName("RetrieveAllReviewsByBookId-Positive")
    void test_retrieveAllReviewsByBookId_positive() {
        Review review2 = new Review(3f, "Good Content", 2L, "ISBN-3080");
        ReviewDTO reviewDTO2 = new ReviewDTO(review2.getReviewId(), 3f, "Good Content", 2L, "ISBN-3080");
        when(reviewRepository.findByBookId(BOOK_ID)).thenReturn(List.of(review, review2));
        when(mapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(mapper.map(review2, ReviewDTO.class)).thenReturn(reviewDTO2);
        List<ReviewDTO> actual = null;
        List<ReviewDTO> expected = List.of(reviewDTO, reviewDTO2);
        try {
            actual = reviewService.retrieveAllReviewsByBookId(review.getBookId());
        } catch (ReviewNotFoundException e) {
            fail(STR."Error thrown in RetrieveById \{e.getMessage()}");
        }
        verify(reviewRepository).findByBookId(BOOK_ID);
        verify(mapper, times(2)).map(any(), any());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("RetrieveAllReviewsByBookId-Negative-ReviewNotFound")
    void test_retrieveAllReviewsByBookId_negative_reviewNotFound() {
        when(reviewRepository.findByBookId(BOOK_ID)).thenReturn(List.of());
        assertThrows(ReviewNotFoundException.class,
                () -> reviewService.retrieveAllReviewsByBookId(BOOK_ID),
                "Error not thrown in RetrieveAllReviewsByBookId");
        verify(reviewRepository).findByBookId(BOOK_ID);
    }
}
