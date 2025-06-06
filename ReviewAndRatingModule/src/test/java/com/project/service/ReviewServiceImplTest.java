package com.project.service;

import com.project.dto.BookDTO;
import com.project.dto.ReviewDTO;
import com.project.dto.UserDTO;
import com.project.enums.Role;
import com.project.exception.*;
import com.project.feign.BookClient;
import com.project.feign.UserClient;
import com.project.models.Review;
import com.project.models.ReviewDelete;
import com.project.repositories.ReviewDeleteRepository;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("preview")
@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    private static final float RATING = 4.5f;
    private static final String COMMENT = "Great Book";
    private static final long USER_ID = 11L;
    private static final String BOOK_ID = "ISBN-1212";
    private static final String USER_NAME = "Sabarish";
    private static final String BOOK_TITLE = "Guide to Java";
    private static final String REASON = "Spam";
    @Mock
    private ReviewRepository reviewRepository;
    @Mock
    private ReviewDeleteRepository reviewDeleteRepository;
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

    @BeforeEach
    void setup() {
        review = new Review(1L, RATING, COMMENT, USER_ID, BOOK_ID);
        reviewDTO = new ReviewDTO(1L, RATING, COMMENT, USER_ID, BOOK_ID, USER_NAME, BOOK_TITLE);
        userDTO = new UserDTO(12L, "name", "mail", "pass", Role.CUSTOMER);
        bookDTO = new BookDTO("ISBN-1212", "title", 2000, 9L, 8, 7);
    }

    @Test
    @DisplayName("AddReview-Positive")
    void test_addReview_positive() throws ServiceUnavailableException {
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
    void test_addReview_negative() throws ServiceUnavailableException {
        when(reviewRepository.save(any())).thenReturn(new IllegalArgumentException());
        when(userClient.getUserById(USER_ID)).thenReturn(ResponseEntity.ok(userDTO));
        when(bookClient.getBookById(BOOK_ID)).thenAnswer(invocation -> ResponseEntity.ok(bookDTO));
        assertThrows(Exception.class, () -> reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID), "Error not thrown in addReview");
        verify(reviewRepository).save(any());
        verify(userClient).getUserById(USER_ID);
        verify(bookClient).getBookById(BOOK_ID);
        assertFalse(reviewRepository.findById(review.getReviewId()).isPresent());
    }

    @Test
    @DisplayName("AddReview-Negative-UserNotFound")
    void test_addReview_negative_userNotFound() throws ServiceUnavailableException {
        when(userClient.getUserById(USER_ID)).thenReturn(ResponseEntity.ok(null));
        assertThrows(UserNotFoundException.class, () -> reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID), "Error not thrown in addReview");
        verify(userClient).getUserById(USER_ID);
        assertFalse(reviewRepository.findById(review.getReviewId()).isPresent());
    }

    @Test
    @DisplayName("AddReview-Negative-UserNotFound-EmptyObject")
    void test_addReview_negative_userNotFound_emptyObject() throws ServiceUnavailableException {
        when(userClient.getUserById(USER_ID)).thenReturn(ResponseEntity.ok(new UserDTO()));
        assertThrows(UserNotFoundException.class, () -> reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID), "Error not thrown in addReview");
        verify(userClient).getUserById(USER_ID);
        assertFalse(reviewRepository.findById(review.getReviewId()).isPresent());
    }

    @Test
    @DisplayName("AddReview-Negative-BookNotFound")
    void test_addReview_negative_bookNotFound() throws ServiceUnavailableException {
        when(userClient.getUserById(USER_ID)).thenReturn(ResponseEntity.ok(userDTO));
//        when(bookClient.getBookById(BOOK_ID)).thenThrow(FeignException.class);
        when(bookClient.getBookById(any())).thenAnswer(invocation -> new ResponseEntity<>(new BookDTO(), HttpStatus.NO_CONTENT));
        assertThrows(BookNotFoundException.class, () -> reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID), "Error not thrown in addReview");
        verify(userClient).getUserById(USER_ID);
        verify(bookClient).getBookById(BOOK_ID);
        assertFalse(reviewRepository.findById(review.getReviewId()).isPresent());
    }


    @Test
    @DisplayName("UpdateReview-Positive-SameUser")
    void test_updateReview_positive_sameUser() throws ServiceUnavailableException {
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
    void test_updateReview_positive_admin() throws ServiceUnavailableException {
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
    void test_updateReview_negative_unauthorized() throws ServiceUnavailableException {
        when(userClient.getUserById(4L)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(bookClient.getBookById(any())).thenAnswer(invocation -> ResponseEntity.ok(bookDTO));
        when(reviewRepository.findById(any())).thenReturn(Optional.of(review));
        assertThrows(UserNotAuthorizedException.class, () -> reviewService.updateReview(4L, reviewDTO), "Error not thrown in UpdateReview for User Unauthorized");
        verify(reviewRepository).findById(any());
        verify(userClient).getUserById(4L);
        verify(bookClient).getBookById(any());
    }

    @Test
    @DisplayName("UpdateReview-Negative-ReviewIDMismatch")
    void test_updateReview_negative_reviewIDMismatch() throws ServiceUnavailableException {
        when(userClient.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(bookClient.getBookById(any())).thenAnswer(invocation -> ResponseEntity.ok(bookDTO));
        when(reviewRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(IDMismatchException.class, () -> reviewService.updateReview(USER_ID, reviewDTO), "Error not thrown in UpdateReview for Review ID Mismatch");
        verify(userClient).getUserById(USER_ID);
        verify(bookClient).getBookById(any());
        verify(reviewRepository).findById(any());
    }

    @Test
    @DisplayName("UpdateReview-Negative-UserIDMismatch")
    void test_updateReview_negative_userIDMismatch() throws ServiceUnavailableException {
        when(userClient.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(bookClient.getBookById(any())).thenAnswer(invocation -> ResponseEntity.ok(bookDTO));
        review.setUserId(4L);
        when(reviewRepository.findById(any())).thenReturn(Optional.of(review));
        assertThrows(IDMismatchException.class, () -> reviewService.updateReview(USER_ID, reviewDTO), "Error not thrown in UpdateReview for User ID Mismatch");
        verify(userClient).getUserById(USER_ID);
        verify(bookClient).getBookById(any());
        verify(reviewRepository).findById(any());
    }

    @Test
    @DisplayName("UpdateReview-Negative-BookIDMismatch")
    void test_updateReview_negative_bookIDMismatch() throws ServiceUnavailableException {
        when(userClient.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(bookClient.getBookById(any())).thenAnswer(invocation -> ResponseEntity.ok(bookDTO));
        review.setBookId("ISBN-NTFND");
        when(reviewRepository.findById(any())).thenReturn(Optional.of(review));
        assertThrows(IDMismatchException.class, () -> reviewService.updateReview(USER_ID, reviewDTO), "Error not thrown in UpdateReview for Book ID Mismatch");
        verify(userClient).getUserById(USER_ID);
        verify(bookClient).getBookById(any());
        verify(reviewRepository).findById(any());
    }


    @Test
    @DisplayName("UpdateReview-Negative-BookNotFound")
    void test_updateReview_negative_bookNotFound() throws ServiceUnavailableException {
        when(userClient.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        when(bookClient.getBookById(any())).thenAnswer(invocation -> new ResponseEntity<>(new BookDTO(), HttpStatus.NO_CONTENT));
        assertThrows(BookNotFoundException.class, () -> reviewService.updateReview(USER_ID, reviewDTO), "Error not thrown in UpdateReview for Book Not Found");
        verify(userClient).getUserById(USER_ID);
        verify(bookClient).getBookById(any());
    }

    @Test
    @DisplayName("UpdateReview-Negative-UserNotFound")
    void test_updateReview_negative_userNotFound() throws ServiceUnavailableException {
        when(userClient.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        assertThrows(UserNotFoundException.class, () -> reviewService.updateReview(USER_ID, reviewDTO), "Error not thrown in UpdateReview for User Not Found");
        verify(userClient).getUserById(USER_ID);
    }

    @Test
    @DisplayName("UpdateReview-Negative-UserNotFound-EmptyObject")
    void test_updateReview_negative_userNotFound_emptyObject() throws ServiceUnavailableException {
        when(userClient.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(new UserDTO(), HttpStatus.OK));
        assertThrows(UserNotFoundException.class, () -> reviewService.updateReview(USER_ID, reviewDTO), "Error not thrown in UpdateReview for User Not Found");
        verify(userClient).getUserById(USER_ID);
    }

    @Test
    @DisplayName("DeleteReview-Positive-SameUser")
    void test_deleteReview_positive_sameUser() throws ServiceUnavailableException {
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
    void test_deleteReview_positive_admin() throws ServiceUnavailableException {
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
    void test_deleteReview_negative_userNotAuthorized() throws ServiceUnavailableException {
        reviewDTO.setUserId(3L);
        when(reviewRepository.findById(any())).thenReturn(Optional.of(review));
        when(mapper.map(any(), any())).thenReturn(reviewDTO);
        when(userClient.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
        assertThrows(UserNotAuthorizedException.class, () -> reviewService.deleteReview(USER_ID, review.getReviewId()), "Error not thrown in DeleteReview");
        verify(reviewRepository).findById(review.getReviewId());
        verify(userClient).getUserById(USER_ID);
        verify(mapper).map(any(), any());
    }

    @Test
    @DisplayName("DeleteReview-Negative-UserNotFound")
    void test_deleteReview_negative_userNotFound() throws ServiceUnavailableException {
        when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.of(review));
        when(userClient.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        assertThrows(UserNotFoundException.class, () -> reviewService.deleteReview(USER_ID, review.getReviewId()), "Error not thrown in DeleteReview");
        verify(reviewRepository).findById(review.getReviewId());
        verify(userClient).getUserById(USER_ID);
    }

    @Test
    @DisplayName("DeleteReview-Negative-UserNotFound-EmptyObject")
    void test_deleteReview_negative_userNotFound_emptyObject() throws ServiceUnavailableException {
        when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.of(review));
        when(userClient.getUserById(USER_ID)).thenReturn(new ResponseEntity<>(new UserDTO(), HttpStatus.OK));
        assertThrows(UserNotFoundException.class, () -> reviewService.deleteReview(USER_ID, review.getReviewId()), "Error not thrown in DeleteReview");
        verify(reviewRepository).findById(review.getReviewId());
        verify(userClient).getUserById(USER_ID);
    }

    @Test
    @DisplayName("DeleteReview-Negative-ReviewNotFound")
    void test_deleteReview_negative_reviewNotFound() {
        when(reviewRepository.findById(review.getReviewId())).thenReturn(Optional.empty());
        assertThrows(ReviewNotFoundException.class, () -> reviewService.deleteReview(2, review.getReviewId()), "Error not thrown in DeleteReview");
        verify(reviewRepository).findById(review.getReviewId());
    }


    @Test
    @DisplayName("DeleteReview-Positive-SameUser")
    void test_addToReview_positive() {
        when(reviewDeleteRepository.save(any())).thenReturn(new ReviewDelete());
        boolean actual = true;
        reviewService.addToReviewDelete(review.getReviewId(), COMMENT);
        verify(reviewDeleteRepository).save(any());
        assertTrue(actual);
    }


    @Test
    @DisplayName("RetrieveAllReviews-Positive-WithOneReview")
    void test_retrieveAllReviews_positive_withOneReview() throws ServiceUnavailableException {
        when(reviewRepository.findAll()).thenAnswer((invocation -> List.of(review)));
        when(mapper.map(any(), any())).thenAnswer((invocation -> reviewDTO));
        when(userClient.getUserById(any())).thenReturn(ResponseEntity.ok(userDTO));
        when(bookClient.getBookById(any())).thenReturn(ResponseEntity.ok(bookDTO));
        when(reviewDeleteRepository.findAllReviewIds()).thenReturn(List.of());
        try {
            List<ReviewDTO> actual = reviewService.retrieveAllReviews();
            verify(reviewRepository).findAll();
            verify(mapper).map(review, ReviewDTO.class);
            verify(userClient).getUserById(any());
            verify(bookClient).getBookById(any());
            verify(reviewDeleteRepository).findAllReviewIds();
            assertEquals(1, actual.size());
        } catch (ReviewNotFoundException | ServiceUnavailableException e) {
            fail(STR."Error thrown in RetrieveAll \{e.getMessage()}");
        }
    }

    @Test
    @DisplayName("RetrieveAllReviews-Positive-WithMultipleReviews")
    void test_retrieveAllReviews_positive_withMultipleReviews() throws ServiceUnavailableException {
        Review review1 = new Review(0.1f, "Worst Book", 4, "ISBN-NTGD");
        ReviewDTO reviewDTO1 = new ReviewDTO(0L, 0.1f, "Worst Book", 4L, "ISBN-NTGD", USER_NAME, BOOK_TITLE);
        when(reviewRepository.findAll()).thenAnswer((invocation -> List.of(review, review1)));
        when(mapper.map(review, ReviewDTO.class)).thenAnswer((invocation -> reviewDTO));
        when(mapper.map(review1, ReviewDTO.class)).thenAnswer((invocation -> reviewDTO1));
        when(userClient.getUserById(any())).thenReturn(ResponseEntity.ok(userDTO));
        when(bookClient.getBookById(any())).thenReturn(ResponseEntity.ok(bookDTO));
        when(reviewDeleteRepository.findAllReviewIds()).thenReturn(List.of());
        List<ReviewDTO> actual = null, expected = List.of(reviewDTO, reviewDTO1);
        try {
            actual = reviewService.retrieveAllReviews();
        } catch (ReviewNotFoundException | ServiceUnavailableException e) {
            fail(STR."Error thrown in RetrieveAll \{e.getMessage()}");
        }
        verify(reviewRepository).findAll();
        verify(mapper).map(review, ReviewDTO.class);
        verify(userClient, times(2)).getUserById(any());
        verify(bookClient, times(2)).getBookById(any());
        verify(reviewDeleteRepository).findAllReviewIds();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("RetrieveAllReviews-Negative")
    void test_retrieveAllReviews_negative() {
        when(reviewRepository.findAll()).thenReturn(List.of());
        assertThrows(ReviewNotFoundException.class, () -> reviewService.retrieveAllReviews(), "Error not thrown in retrieveAll");
        verify(reviewRepository).findAll();
    }

    @Test
    @DisplayName("RetrieveReviewById-Positive")
    void test_retrieveReviewById_positive() throws ServiceUnavailableException {
        when(reviewRepository.findById(any())).thenReturn(Optional.of(review));
        when(mapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(userClient.getUserById(any())).thenReturn(ResponseEntity.ok(userDTO));
        when(bookClient.getBookById(any())).thenReturn(ResponseEntity.ok(bookDTO));
        ReviewDTO actual = null;
        try {
            actual = reviewService.retrieveReviewById(review.getReviewId());
        } catch (ReviewNotFoundException e) {
            fail(STR."Error thrown in RetrieveById \{e.getMessage()}");
        }
        verify(reviewRepository).findById(any());
        verify(mapper).map(review, ReviewDTO.class);
        verify(userClient).getUserById(any());
        verify(bookClient).getBookById(any());
        assertEquals(reviewDTO, actual);
    }

    @Test
    @DisplayName("RetrieveReviewById-Negative")
    void test_retrieveReviewById_negative() {
        when(reviewRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(ReviewNotFoundException.class, () -> reviewService.retrieveReviewById(-1L), "Error not thrown in getById");
        verify(reviewRepository).findById(any());
    }

    @Test
    @DisplayName("RetrieveAllReviewsByUserId-Positive")
    void test_retrieveAllReviewsByUserId_positive() throws ServiceUnavailableException {
        Review review2 = new Review(3f, "Good Content", USER_ID, BOOK_ID);
        ReviewDTO reviewDTO2 = new ReviewDTO(review2.getReviewId(), 3f, "Good Content", USER_ID, BOOK_ID, USER_NAME, BOOK_TITLE);
        when(reviewRepository.findByUserId(USER_ID)).thenReturn(List.of(review, review2));
        when(mapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(mapper.map(review2, ReviewDTO.class)).thenReturn(reviewDTO2);
        when(userClient.getUserById(any())).thenReturn(ResponseEntity.ok(userDTO));
        when(bookClient.getBookById(any())).thenReturn(ResponseEntity.ok(bookDTO));
        when(reviewDeleteRepository.findAllReviewIds()).thenReturn(List.of());
        List<ReviewDTO> actual = null;
        List<ReviewDTO> expected = List.of(reviewDTO, reviewDTO2);
        try {
            actual = reviewService.retrieveAllReviewsByUserId(review.getUserId());
        } catch (ReviewNotFoundException | ServiceUnavailableException e) {
            fail(STR."Error thrown in RetrieveById \{e.getMessage()}");
        }
        verify(reviewRepository).findByUserId(USER_ID);
        verify(mapper, times(2)).map(any(), any());
        verify(userClient, times(2)).getUserById(any());
        verify(reviewDeleteRepository).findAllReviewIds();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("RetrieveAllReviewsByUserId-Negative-ReviewNotFound")
    void test_retrieveAllReviewsByUserId_negative_reviewNotFound() {
        when(reviewRepository.findByUserId(USER_ID)).thenReturn(List.of());
        assertThrows(ReviewNotFoundException.class, () -> reviewService.retrieveAllReviewsByUserId(USER_ID), "Error not thrown in RetrieveAllReviewsByUserId");
        verify(reviewRepository).findByUserId(USER_ID);
    }

    @Test
    @DisplayName("RetrieveAllReviewsByBookId-Positive")
    void test_retrieveAllReviewsByBookId_positive() throws ServiceUnavailableException {
        Review review2 = new Review(3f, "Good Content", USER_ID, BOOK_ID);
        ReviewDTO reviewDTO2 = new ReviewDTO(review2.getReviewId(), 3f, "Good Content", USER_ID, BOOK_ID, USER_NAME, BOOK_TITLE);
        when(reviewRepository.findByBookId(BOOK_ID)).thenReturn(List.of(review, review2));
        when(userClient.getUserById(any())).thenReturn(ResponseEntity.ok(userDTO));
        when(bookClient.getBookById(any())).thenReturn(ResponseEntity.ok(bookDTO));
        when(mapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(mapper.map(review2, ReviewDTO.class)).thenReturn(reviewDTO2);
        when(reviewDeleteRepository.findAllReviewIds()).thenReturn(List.of());
        List<ReviewDTO> actual = null;
        List<ReviewDTO> expected = List.of(reviewDTO, reviewDTO2);
        try {
            actual = reviewService.retrieveAllReviewsByBookId(review.getBookId());
        } catch (ReviewNotFoundException | ServiceUnavailableException e) {
            fail(STR."Error thrown in RetrieveById \{e.getMessage()}");
        }
        verify(reviewRepository).findByBookId(BOOK_ID);
        verify(mapper, times(2)).map(any(), any());
        verify(userClient, times(2)).getUserById(USER_ID);
        verify(reviewDeleteRepository).findAllReviewIds();
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("RetrieveAllReviewsByBookId-Negative-ReviewNotFound")
    void test_retrieveAllReviewsByBookId_negative_reviewNotFound() {
        when(reviewRepository.findByBookId(BOOK_ID)).thenReturn(List.of());
        assertThrows(ReviewNotFoundException.class, () -> reviewService.retrieveAllReviewsByBookId(BOOK_ID), "Error not thrown in RetrieveAllReviewsByBookId");
        verify(reviewRepository).findByBookId(BOOK_ID);
    }

    @Test
    @DisplayName("RetrieveAverageRating-Positive")
    void test_retrieveAverageRating_postive() {
        when(reviewRepository.findByBookId(any())).thenReturn(List.of(review));
        when(reviewDeleteRepository.findAllReviewIds()).thenReturn(List.of());
        List<Float> expected = new ArrayList<>(List.of(review.getRating(), 1f));
        List<Float> actual = reviewService.retrieveAverageRating(BOOK_ID);
        assertEquals(expected, actual);
        verify(reviewRepository).findByBookId(any());
        verify(reviewDeleteRepository).findAllReviewIds();
    }

    @Test
    @DisplayName("AddToReviewDelete-Positive")
    void test_addToReviewDelete_positive() {
        when(reviewDeleteRepository.save(any(ReviewDelete.class))).thenReturn(new ReviewDelete());

        boolean result = reviewService.addToReviewDelete(1L, "Reason");

        verify(reviewDeleteRepository).save(any(ReviewDelete.class));
        assertTrue(result);
    }

    @Test
    @DisplayName("RetrieveAllReviewDeletes-Positive")
    void test_retrieveAllReviewDeletes_positive() throws ServiceUnavailableException {
        ReviewDelete reviewDelete = new ReviewDelete(1L, REASON);
        when(reviewDeleteRepository.findAll()).thenReturn(List.of(reviewDelete));

        when(reviewRepository.findById(any())).thenReturn(Optional.of(review));
        when(mapper.map(review, ReviewDTO.class)).thenReturn(reviewDTO);
        when(userClient.getUserById(any())).thenReturn(ResponseEntity.ok(userDTO));
        when(bookClient.getBookById(any())).thenReturn(ResponseEntity.ok(bookDTO));

        List<ReviewDTO> result = reviewService.retrieveAllReviewDeletes();

        verify(reviewDeleteRepository).findAll();
        assertEquals(1, result.size());
        assertEquals(reviewDTO, result.getFirst());
    }

    @Test
    @DisplayName("RetrieveAllReviewDeletes-Negative")
    void test_retrieveAllReviewDeletes_negative() throws ServiceUnavailableException {
        when(reviewDeleteRepository.findAll()).thenReturn(List.of());

        List<ReviewDTO> result = reviewService.retrieveAllReviewDeletes();

        verify(reviewDeleteRepository).findAll();
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("DeleteReviewDelete-Positive")
    void test_deleteReviewDelete_positive() throws ServiceUnavailableException, UserNotFoundException, ReviewNotFoundException, UserNotAuthorizedException {
        when(reviewDeleteRepository.findByReviewId(anyLong())).thenReturn(Optional.of(new ReviewDelete()));
        when(userClient.getUserById(anyLong())).thenReturn(ResponseEntity.ok(new UserDTO(USER_ID, "Name", "mail", "pass", Role.ADMIN)));

        boolean result = reviewService.deleteReviewDelete(USER_ID, 1L);

        verify(reviewDeleteRepository).findByReviewId(anyLong());
        verify(userClient).getUserById(anyLong());
        verify(reviewDeleteRepository).deleteByReviewId(anyLong());
        assertTrue(result);
    }

    @Test
    @DisplayName("DeleteReviewDelete-Negative-ReviewNotFound")
    void test_deleteReviewDelete_negative_reviewNotFound() {
        when(reviewDeleteRepository.findByReviewId(anyLong())).thenReturn(Optional.empty());

        assertThrows(ReviewNotFoundException.class, () -> reviewService.deleteReviewDelete(1L, 1L));

        verify(reviewDeleteRepository).findByReviewId(anyLong());
    }

    @Test
    @DisplayName("DeleteReviewDelete-Negative-UserNotFound")
    void test_deleteReviewDelete_negative_userNotFound() throws ServiceUnavailableException {
        when(reviewDeleteRepository.findByReviewId(anyLong())).thenReturn(Optional.of(new ReviewDelete()));
        when(userClient.getUserById(anyLong())).thenReturn(ResponseEntity.ok(null));

        assertThrows(UserNotFoundException.class, () -> reviewService.deleteReviewDelete(1L, 1L));

        verify(reviewDeleteRepository).findByReviewId(anyLong());
        verify(userClient).getUserById(anyLong());
    }

    @Test
    @DisplayName("DeleteReviewDelete-Negative-UserNotAuthorized")
    void test_deleteReviewDelete_negative_userNotAuthorized() throws ServiceUnavailableException {
        when(reviewDeleteRepository.findByReviewId(anyLong())).thenReturn(Optional.of(new ReviewDelete()));
        when(userClient.getUserById(anyLong())).thenReturn(ResponseEntity.ok(new UserDTO(1L, "Name", "mail", "pass", Role.CUSTOMER)));

        assertThrows(UserNotAuthorizedException.class, () -> reviewService.deleteReviewDelete(1L, 1L));

        verify(reviewDeleteRepository).findByReviewId(anyLong());
        verify(userClient).getUserById(anyLong());
    }

}
