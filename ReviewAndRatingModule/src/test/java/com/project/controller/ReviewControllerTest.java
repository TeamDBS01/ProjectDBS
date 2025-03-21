package com.project.controller;

import com.jayway.jsonpath.JsonPath;
import com.project.dto.ReviewDTO;
import com.project.exception.*;
import com.project.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("preview")
@ExtendWith(MockitoExtension.class)
class ReviewControllerTest {
    @Mock
    private ReviewService reviewService;
    @InjectMocks
    private ReviewControllerImpl reviewController;

    private ReviewDTO reviewDTO;
    private static final long REVIEW_ID = 2L;
    private static final float RATING = 5f;
    private static final String COMMENT = "Best book!";
    private static final long USER_ID = 12L;
    private static final String BOOK_ID = "ISBN-4002";

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        reviewDTO = new ReviewDTO(REVIEW_ID, RATING, COMMENT, USER_ID, BOOK_ID);
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
    }

    @Test
    @DisplayName("GetReviewById-Positive")
    void test_getReviewById_positive() throws ReviewNotFoundException {
        when(reviewService.retrieveReviewById(REVIEW_ID)).thenReturn(reviewDTO);
        ResponseEntity<ReviewDTO> response = reviewController.getReviewById(REVIEW_ID);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(reviewDTO, response.getBody());
        verify(reviewService).retrieveReviewById(REVIEW_ID);
    }

    @Test
    @DisplayName("GetReviewById-Negative")
    void test_getReviewById_negative() throws ReviewNotFoundException {
        when(reviewService.retrieveReviewById(REVIEW_ID)).thenThrow(new ReviewNotFoundException("Review not found"));
        ResponseEntity<ReviewDTO> response = reviewController.getReviewById(REVIEW_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).retrieveReviewById(REVIEW_ID);
    }

    @Test
    @DisplayName("GetAllReviews-Positive")
    void test_getAllReviews_positive() throws ReviewNotFoundException {
        List<ReviewDTO> reviewList = List.of(reviewDTO);
        when(reviewService.retrieveAllReviews()).thenReturn(reviewList);
        ResponseEntity<List<ReviewDTO>> response = reviewController.getAllReviews();
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(reviewList, response.getBody());
        verify(reviewService).retrieveAllReviews();
    }

    @Test
    @DisplayName("GetAllReviews-Negative")
    void test_getAllReviews_negative() throws ReviewNotFoundException {
        when(reviewService.retrieveAllReviews()).thenThrow(new ReviewNotFoundException("No reviews found"));
        ResponseEntity<List<ReviewDTO>> response = reviewController.getAllReviews();
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).retrieveAllReviews();
    }

    @Test
    @DisplayName("GetAllReviews-Negative-RuntimeException")
    void test_getAllReviews_negative_runtimeException() throws ReviewNotFoundException {
        when(reviewService.retrieveAllReviews()).thenThrow(new RuntimeException());
        ResponseEntity<List<ReviewDTO>> response = reviewController.getAllReviews();
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).retrieveAllReviews();
    }

    @Test
    @DisplayName("GetAllReviewsByUserId-Positive")
    void test_getAllReviewsByUserId_positive() throws ReviewNotFoundException {
        List<ReviewDTO> reviewList = List.of(reviewDTO);
        when(reviewService.retrieveAllReviewsByUserId(USER_ID)).thenReturn(reviewList);
        ResponseEntity<List<ReviewDTO>> response = reviewController.getAllReviewsByUserId(USER_ID);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(reviewList, response.getBody());
        verify(reviewService).retrieveAllReviewsByUserId(USER_ID);
    }

    @Test
    @DisplayName("GetAllReviewsByUserId-Negative")
    void test_getAllReviewsByUserId_negative() throws ReviewNotFoundException {
        when(reviewService.retrieveAllReviewsByUserId(USER_ID)).thenThrow(new ReviewNotFoundException("No reviews found for user"));
        ResponseEntity<List<ReviewDTO>> response = reviewController.getAllReviewsByUserId(USER_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).retrieveAllReviewsByUserId(USER_ID);
    }

    @Test
    @DisplayName("GetAllReviewsByUserId-Negative-RuntimeException")
    void test_getAllReviewsByUserId_negative_runtimeException() throws ReviewNotFoundException {
        when(reviewService.retrieveAllReviewsByUserId(USER_ID)).thenThrow(new RuntimeException());
        ResponseEntity<List<ReviewDTO>> response = reviewController.getAllReviewsByUserId(USER_ID);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).retrieveAllReviewsByUserId(USER_ID);
    }

    @Test
    @DisplayName("GetAllReviewsByBookId-Positive")
    void test_getAllReviewsByBookId_positive() throws ReviewNotFoundException {
        List<ReviewDTO> reviewList = List.of(reviewDTO);
        when(reviewService.retrieveAllReviewsByBookId(BOOK_ID)).thenReturn(reviewList);
        ResponseEntity<List<ReviewDTO>> response = reviewController.getAllReviewsByBookId(BOOK_ID);
        assertEquals(HttpStatus.FOUND, response.getStatusCode());
        assertEquals(reviewList, response.getBody());
        verify(reviewService).retrieveAllReviewsByBookId(BOOK_ID);
    }

    @Test
    @DisplayName("GetAllReviewsByBookId-Negative")
    void test_getAllReviewsByBookId_negative() throws ReviewNotFoundException {
        when(reviewService.retrieveAllReviewsByBookId(BOOK_ID)).thenThrow(new ReviewNotFoundException("No reviews found for user"));
        ResponseEntity<List<ReviewDTO>> response = reviewController.getAllReviewsByBookId(BOOK_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).retrieveAllReviewsByBookId(BOOK_ID);
    }

    @Test
    @DisplayName("GetAllReviewsByBookId-Negative-RuntimeException")
    void test_getAllReviewsByBookId_negative_runtimeException() throws ReviewNotFoundException {
        when(reviewService.retrieveAllReviewsByBookId(BOOK_ID)).thenThrow(new RuntimeException());
        ResponseEntity<List<ReviewDTO>> response = reviewController.getAllReviewsByBookId(BOOK_ID);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).retrieveAllReviewsByBookId(BOOK_ID);
    }

    @Test
    @DisplayName("AddReviewWithParameters-Positive")
    void test_addReviewWithParameters_positive() throws UserNotFoundException, BookNotFoundException {
        when(reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID)).thenReturn(reviewDTO);
        ResponseEntity<ReviewDTO> response = reviewController.addReview(RATING, COMMENT, USER_ID, BOOK_ID);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(reviewDTO, response.getBody());
        verify(reviewService).addReview(RATING, COMMENT, USER_ID, BOOK_ID);
    }

    @Test
    @DisplayName("AddReviewWithParameters-Negative-BadGateway")
    void test_addReviewWithParameters_negative_badGateway() throws UserNotFoundException, BookNotFoundException {
        when(reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID)).thenThrow(new RuntimeException("Unable to add review"));
        ResponseEntity<ReviewDTO> response = reviewController.addReview(RATING, COMMENT, USER_ID, BOOK_ID);
        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertTrue(response.getBody() == null || response.getBody().getBookId() == null);
        verify(reviewService).addReview(RATING, COMMENT, USER_ID, BOOK_ID);
    }

    @Test
    @DisplayName("AddReviewWithParameters-Negative-UserNotFound")
    void test_addReviewWithParameters_negative_userNotFound() throws UserNotFoundException, BookNotFoundException {
        when(reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID)).thenThrow(new UserNotFoundException("User/Book Not Found - Review Not added"));
        ResponseEntity<ReviewDTO> response = reviewController.addReview(RATING, COMMENT, USER_ID, BOOK_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() == null || response.getBody().getBookId() == null);
        verify(reviewService).addReview(RATING, COMMENT, USER_ID, BOOK_ID);
    }

    @Test
    @DisplayName("AddReviewWithParameters-Negative-BookNotFound")
    void test_addReviewWithParameters_negative_bookNotFound() throws UserNotFoundException, BookNotFoundException {
        when(reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID)).thenThrow(new BookNotFoundException("User/Book Not Found - Review Not added"));
        ResponseEntity<ReviewDTO> response = reviewController.addReview(RATING, COMMENT, USER_ID, BOOK_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() == null || response.getBody().getBookId() == null);
        verify(reviewService).addReview(RATING, COMMENT, USER_ID, BOOK_ID);
    }


    @Test
    @DisplayName("AddReviewWithReviewDTO-Positive")
    void test_addReviewWithReviewDTO_positive() throws UserNotFoundException, BookNotFoundException {
        when(reviewService.addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId())).thenReturn(reviewDTO);
        ResponseEntity<ReviewDTO> response = reviewController.addReview(reviewDTO);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(reviewDTO, response.getBody());
        verify(reviewService).addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId());
    }

    @Test
    @DisplayName("AddReviewWithReviewDTO-Negative-BadGateway")
    void test_addReviewWithReviewDTO_negative_badGateway() throws UserNotFoundException, BookNotFoundException {
        when(reviewService.addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId())).thenThrow(new RuntimeException("Unable to add review"));
        ResponseEntity<ReviewDTO> response = reviewController.addReview(reviewDTO);
        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId());
    }

    @Test
    @DisplayName("AddReviewWithReviewDTO-Negative-UserNotFound")
    void test_addReviewWithReviewDTO_negative_userNotFound() throws UserNotFoundException, BookNotFoundException {
        when(reviewService.addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId())).thenThrow(new UserNotFoundException("User/Book Not Found - Review Not Added"));
        ResponseEntity<ReviewDTO> response = reviewController.addReview(reviewDTO);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId());
    }

    @Test
    @DisplayName("AddReviewWithReviewDTO-Negative-BookNotFound")
    void test_addReviewWithReviewDTO_negative_bookNotFound() throws UserNotFoundException, BookNotFoundException {
        when(reviewService.addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId())).thenThrow(new BookNotFoundException("User/Book Not Found - Review Not Added"));
        ResponseEntity<ReviewDTO> response = reviewController.addReview(reviewDTO);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId());
    }

    @Test
    @DisplayName("UpdateReview-Positive")
    void test_updateReview_positive() throws UserNotFoundException, UserNotAuthorizedException, IDMismatchException, BookNotFoundException {
        when(reviewService.updateReview(USER_ID, reviewDTO)).thenReturn(reviewDTO);
        ResponseEntity<ReviewDTO> response = reviewController.updateReview(USER_ID, reviewDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reviewDTO, response.getBody());
        verify(reviewService).updateReview(USER_ID, reviewDTO);
    }

    @Test
    @DisplayName("UpdateReview-Negative-Unauthorized")
    void test_updateReview_negative_unauthorized() throws UserNotFoundException, UserNotAuthorizedException, IDMismatchException, BookNotFoundException {
        when(reviewService.updateReview(USER_ID, reviewDTO)).thenThrow(new UserNotAuthorizedException("User not authorized"));
        ResponseEntity<ReviewDTO> response = reviewController.updateReview(USER_ID, reviewDTO);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).updateReview(USER_ID, reviewDTO);
    }

    @Test
    @DisplayName("UpdateReview-Negative-UserNotFound")
    void test_updateReview_negative_userNotFound() throws UserNotFoundException, UserNotAuthorizedException, IDMismatchException, BookNotFoundException {
        when(reviewService.updateReview(USER_ID, reviewDTO)).thenThrow(new UserNotFoundException("User not found"));
        ResponseEntity<ReviewDTO> response = reviewController.updateReview(USER_ID, reviewDTO);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).updateReview(USER_ID, reviewDTO);
    }

    @Test
    @DisplayName("UpdateReview-Negative-BookNotFound")
    void test_updateReview_negative_bookNotFound() throws UserNotFoundException, UserNotAuthorizedException, IDMismatchException, BookNotFoundException {
        when(reviewService.updateReview(USER_ID, reviewDTO)).thenThrow(new BookNotFoundException("Book not found"));
        ResponseEntity<ReviewDTO> response = reviewController.updateReview(USER_ID, reviewDTO);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).updateReview(USER_ID, reviewDTO);
    }

    @Test
    @DisplayName("UpdateReview-Negative-IDMismatch")
    void test_updateReview_negative_idMismatch() throws UserNotFoundException, UserNotAuthorizedException, IDMismatchException, BookNotFoundException {
        when(reviewService.updateReview(USER_ID, reviewDTO)).thenThrow(new IDMismatchException("ID should not change"));
        ResponseEntity<ReviewDTO> response = reviewController.updateReview(USER_ID, reviewDTO);
        assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).updateReview(USER_ID, reviewDTO);
    }

    @Test
    @DisplayName("UpdateReview-Negative-RuntimeException")
    void test_updateReview_negative_runtimeException() throws UserNotFoundException, UserNotAuthorizedException, IDMismatchException, BookNotFoundException {
        when(reviewService.updateReview(USER_ID, reviewDTO)).thenThrow(new RuntimeException());
        ResponseEntity<ReviewDTO> response = reviewController.updateReview(USER_ID, reviewDTO);
        assertEquals(HttpStatus.NOT_MODIFIED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).updateReview(USER_ID, reviewDTO);
    }

    @Test
    @DisplayName("UpdateReview-Negative-IlleagalArgumentException")
    void test_updateReview_negative_illeagalArgumentException() throws UserNotFoundException, UserNotAuthorizedException, IDMismatchException, BookNotFoundException {
        when(reviewService.updateReview(USER_ID, reviewDTO)).thenThrow(new IllegalArgumentException());
        ResponseEntity<ReviewDTO> response = reviewController.updateReview(USER_ID, reviewDTO);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).updateReview(USER_ID, reviewDTO);
    }

    @Test
    @DisplayName("DeleteReview-Positive")
    void test_deleteReview_positive() throws UserNotFoundException, ReviewNotFoundException, UserNotAuthorizedException {
        when(reviewService.deleteReview(USER_ID, REVIEW_ID)).thenReturn(true);
        ResponseEntity<Boolean> response = reviewController.deleteReview(USER_ID, REVIEW_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());
        verify(reviewService).deleteReview(USER_ID, REVIEW_ID);
    }

    @Test
    @DisplayName("DeleteReview-Negative-NotModified")
    void test_deleteReview_negative_notModified() throws UserNotFoundException, ReviewNotFoundException, UserNotAuthorizedException {
        when(reviewService.deleteReview(USER_ID, REVIEW_ID)).thenReturn(false);
        ResponseEntity<Boolean> response = reviewController.deleteReview(USER_ID, REVIEW_ID);
        assertEquals(HttpStatus.NOT_MODIFIED, response.getStatusCode());
        assertNotEquals(Boolean.TRUE, response.getBody());
        verify(reviewService).deleteReview(USER_ID, REVIEW_ID);
    }

    @Test
    @DisplayName("DeleteReview-Negative-RuntimeException")
    void test_deleteReview_negative_runtimeException() throws UserNotFoundException, ReviewNotFoundException, UserNotAuthorizedException {
        when(reviewService.deleteReview(USER_ID, REVIEW_ID)).thenThrow(new RuntimeException());
        ResponseEntity<Boolean> response = reviewController.deleteReview(USER_ID, REVIEW_ID);
        assertEquals(HttpStatus.NOT_MODIFIED, response.getStatusCode());
        assertNotEquals(Boolean.TRUE, response.getBody());
        verify(reviewService).deleteReview(USER_ID, REVIEW_ID);
    }

    @Test
    @DisplayName("DeleteReview-Negative-IlleagalArgumentException")
    void test_deleteReview_negative_illeagalArgumentException() throws UserNotFoundException, ReviewNotFoundException, UserNotAuthorizedException {
        when(reviewService.deleteReview(USER_ID, REVIEW_ID)).thenThrow(new IllegalArgumentException());
        ResponseEntity<Boolean> response = reviewController.deleteReview(USER_ID, REVIEW_ID);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotEquals(Boolean.TRUE, response.getBody());
        verify(reviewService).deleteReview(USER_ID, REVIEW_ID);
    }

    @Test
    @DisplayName("DeleteReview-Negative-Unauthorized")
    void test_deleteReview_negative_unauthorized() throws UserNotFoundException, ReviewNotFoundException, UserNotAuthorizedException {
        when(reviewService.deleteReview(USER_ID, REVIEW_ID)).thenThrow(new UserNotAuthorizedException("User not authorized"));
        ResponseEntity<Boolean> response = reviewController.deleteReview(USER_ID, REVIEW_ID);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(Boolean.FALSE, response.getBody());
        verify(reviewService).deleteReview(USER_ID, REVIEW_ID);
    }

    @Test
    @DisplayName("DeleteReview-Negative-ReviewNotFound")
    void test_deleteReview_negative_reviewNotFound() throws UserNotFoundException, ReviewNotFoundException, UserNotAuthorizedException {
        when(reviewService.deleteReview(USER_ID, REVIEW_ID)).thenThrow(new ReviewNotFoundException("Review not found"));
        ResponseEntity<Boolean> response = reviewController.deleteReview(USER_ID, REVIEW_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotEquals(Boolean.TRUE, response.getBody());
        verify(reviewService).deleteReview(USER_ID, REVIEW_ID);
    }

    @Test
    @DisplayName("DeleteReview-Negative-UserNotFound")
    void test_deleteReview_negative_userNotFound() throws UserNotFoundException, ReviewNotFoundException, UserNotAuthorizedException {
        when(reviewService.deleteReview(USER_ID, REVIEW_ID)).thenThrow(new UserNotFoundException("User not found"));
        ResponseEntity<Boolean> response = reviewController.deleteReview(USER_ID, REVIEW_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotEquals(Boolean.TRUE, response.getBody());
        verify(reviewService).deleteReview(USER_ID, REVIEW_ID);
    }

    @Test
    @DisplayName("GetOk")
    void test_getOk() {
        ResponseEntity<Boolean> response= reviewController.getOk();
        assertEquals(Boolean.TRUE, response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @DisplayName("GetReviewById-Uri-Positive")
    void test_getReviewById_uri_positive() {
        try {
            when(reviewService.retrieveReviewById(REVIEW_ID)).thenReturn(reviewDTO);
            mockMvc.perform(get("/dbs/review/{reviewId}", REVIEW_ID))
                    .andExpect(status().isFound())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Exception thrown \{e.toString()}");
        }
    }

    @Test
    @DisplayName("GetReviewById-Uri-Negative-ReviewNotFound")
    void test_getReviewById_uri_negative_reviewNotFound() {
        try {
            when(reviewService.retrieveReviewById(REVIEW_ID)).thenThrow(new ReviewNotFoundException("Review not found"));
            mockMvc.perform(get("/dbs/review/{reviewId}", REVIEW_ID))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Exception thrown \{e.toString()}");
        }
    }

    @Test
    @DisplayName("GetReviewById-Json-Positive")
    void test_getReviewById_json_positive() {
        try {
            when(reviewService.retrieveReviewById(REVIEW_ID)).thenReturn(reviewDTO);
            MvcResult mvcResult = mockMvc.perform(get("/dbs/review/{reviewId}", REVIEW_ID))
                    .andExpect(status().isFound())
                    .andReturn();

            String jsonData = mvcResult.getResponse().getContentAsString();
            String actual = JsonPath.parse(jsonData).read("comment");
            assertEquals("Best book!", actual);
        } catch (Exception e) {
            fail(STR."Exception thrown \{e.toString()}");
        }
    }

    @Test
    @DisplayName("GetAllReviews-Uri-Positive")
    void test_getAllReviews_uri_positive() {
        try {
            List<ReviewDTO> reviewList = List.of(reviewDTO);
            when(reviewService.retrieveAllReviews()).thenReturn(reviewList);
            mockMvc.perform(get("/dbs/review/all"))
                    .andExpect(status().isFound())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Exception thrown \{e.toString()}");
        }
    }

    @Test
    @DisplayName("GetAllReviews-Uri-Negative-ReviewNotFound")
    void test_getAllReviews_uri_negative_reviewNotFound() {
        try {
            when(reviewService.retrieveAllReviews()).thenThrow(new ReviewNotFoundException("No reviews found"));
            mockMvc.perform(get("/dbs/review/all"))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Exception thrown \{e.toString()}");
        }
    }

    @Test
    @DisplayName("GetAllReviews-Json-Positive")
    void test_getAllReviews_json_positive() {
        try {
            List<ReviewDTO> reviewList = List.of(reviewDTO);
            when(reviewService.retrieveAllReviews()).thenReturn(reviewList);
            MvcResult mvcResult = mockMvc.perform(get("/dbs/review/all"))
                    .andExpect(status().isFound())
                    .andReturn();

            String jsonData = mvcResult.getResponse().getContentAsString();
            String actual = JsonPath.parse(jsonData).read("$[0].comment");
            assertEquals("Best book!", actual);
        } catch (Exception e) {
            fail(STR."Exception thrown \{e.toString()}");
        }
    }

    @Test
    @DisplayName("GetAllReviewsByUserId-Uri-Positive")
    void test_getAllReviewsByUserId_uri_positive() {
        try {
            List<ReviewDTO> reviewList = List.of(reviewDTO);
            when(reviewService.retrieveAllReviewsByUserId(USER_ID)).thenReturn(reviewList);
            mockMvc.perform(get("/dbs/review/user/{userId}", USER_ID))
                    .andExpect(status().isFound())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Exception thrown \{e.toString()}");
        }
    }

    @Test
    @DisplayName("GetAllReviewsByUserId-Uri-Negative-ReviewNotFound")
    void test_getAllReviewsByUserId_uri_negative_reviewNotFound() {
        try {
            when(reviewService.retrieveAllReviewsByUserId(USER_ID)).thenThrow(new ReviewNotFoundException("No reviews found for user"));
            mockMvc.perform(get("/dbs/review/user/{userId}", USER_ID))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Exception thrown \{e.toString()}");
        }
    }

    @Test
    @DisplayName("GetAllReviewsByUserId-Json-Positive")
    void test_getAllReviewsByUserId_json_positive() {
        try {
            List<ReviewDTO> reviewList = List.of(reviewDTO);
            when(reviewService.retrieveAllReviewsByUserId(USER_ID)).thenReturn(reviewList);
            MvcResult mvcResult = mockMvc.perform(get("/dbs/review/user/{userId}", USER_ID))
                    .andExpect(status().isFound())
                    .andReturn();

            String jsonData = mvcResult.getResponse().getContentAsString();
            String actual = JsonPath.parse(jsonData).read("$[0].comment");
            assertEquals("Best book!", actual);
        } catch (Exception e) {
            fail(STR."Exception thrown \{e.toString()}");
        }
    }

    @Test
    @DisplayName("AddReviewWithParameters-Uri-Positive")
    void test_addReviewWithParameters_uri_positive() {
        try {
            when(reviewService.addReview(5.0f, "Best book!", USER_ID, "ISBN-4002")).thenReturn(reviewDTO);
            mockMvc.perform(post("/dbs/review/add/values")
                    .param("comment", "Best book!")
                    .param("rating", String.valueOf(5.0f))
                    .param("userId", String.valueOf(USER_ID))
                    .param("bookId", "ISBN-4002"))
                    .andExpect(status().isCreated())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }

    @Test
    @DisplayName("AddReviewWithParameters-Uri-Negative-UserNotFound")
    void test_addReviewWithParameters_uri_negative_userNotFound() {
        try {
            when(reviewService.addReview(5.0f, "Best book!", USER_ID, "ISBN-4002")).thenThrow(new UserNotFoundException("User not found"));
            mockMvc.perform(post("/dbs/review/add/values")
                    .param("rating", String.valueOf(5.0f))
                    .param("comment", "Best book!")
                    .param("bookId", "ISBN-4002")
                    .param("userId", String.valueOf(USER_ID)))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }

    @Test
    @DisplayName("AddReviewWithParameters-Uri-Negative-BadGateway")
    void test_addReviewWithParameters_uri_negative_badGateway() {
        try {
            when(reviewService.addReview(5.0f, "Best book!", USER_ID, "ISBN-4002")).thenThrow(new RuntimeException("Unable to add review"));
            mockMvc.perform(post("/dbs/review/add/values")
                    .param("rating", String.valueOf(5.0f))
                    .param("comment", "Best book!")
                    .param("userId", String.valueOf(USER_ID))
                    .param("bookId", "ISBN-4002"))
                    .andExpect(status().isBadGateway())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }

    @Test
    @DisplayName("AddReviewWithReviewDTO-Uri-Positive")
    void test_addReviewWithReviewDTO_uri_positive() {
        try {
            when(reviewService.addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId())).thenReturn(reviewDTO);
            mockMvc.perform(post("/dbs/review/add")
                            .contentType("application/json")
                            .content("{\"rating\":5.0,\"comment\":\"Best book!\",\"userId\":12,\"bookId\":\"ISBN-4002\"}"))
                    .andExpect(status().isCreated())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }

    @Test
    @DisplayName("AddReviewWithReviewDTO-Uri-Negative-UserNotFound")
    void test_addReviewWithReviewDTO_uri_negative_userNotFound() {
        try {
            when(reviewService.addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId())).thenThrow(new UserNotFoundException("User not found"));
            mockMvc.perform(post("/dbs/review/add")
                            .contentType("application/json")
                            .content("{\"rating\":5.0,\"comment\":\"Best book!\",\"userId\":12,\"bookId\":\"ISBN-4002\"}"))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }

    @Test
    @DisplayName("AddReviewWithReviewDTO-Uri-Negative-BadGateway")
    void test_addReviewWithReviewDTO_uri_negative_badGateway() {
        try {
            when(reviewService.addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId())).thenThrow(new RuntimeException("Unable to add review"));
            mockMvc.perform(post("/dbs/review/add")
                            .contentType("application/json")
                            .content("{\"rating\":5.0,\"comment\":\"Best book!\",\"userId\":12,\"bookId\":\"ISBN-4002\"}"))
                    .andExpect(status().isBadGateway())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }

    @Test
    @DisplayName("UpdateReview-Uri-Positive")
    void test_updateReview_uri_positive() throws UserNotAuthorizedException {
        try {
            when(reviewService.updateReview(USER_ID, reviewDTO)).thenReturn(reviewDTO);
            mockMvc.perform(patch("/dbs/review/update/{userId}", USER_ID)
                            .contentType("application/json")
                            .content("{\"reviewId\":2,\"rating\":5.0,\"comment\":\"Best book!\",\"userId\":12,\"bookId\":\"ISBN-4002\"}"))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }

    @Test
    @DisplayName("UpdateReview-Uri-Negative-Unauthorized")
    void test_updateReview_uri_negative_unauthorized() throws UserNotAuthorizedException {
        try {
            when(reviewService.updateReview(USER_ID, reviewDTO)).thenThrow(new UserNotAuthorizedException("User not authorized"));
            mockMvc.perform(patch("/dbs/review/update/{userId}", USER_ID)
                            .contentType("application/json")
                            .content("{\"reviewId\":2,\"rating\":5.0,\"comment\":\"Best book!\",\"userId\":12,\"bookId\":\"ISBN-4002\"}"))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }

    @Test
    @DisplayName("UpdateReview-Uri-Negative-UserNotFound")
    void test_updateReview_uri_negative_userNotFound() throws UserNotAuthorizedException {
        try {
            when(reviewService.updateReview(USER_ID, reviewDTO)).thenThrow(new UserNotFoundException("User not found"));
            mockMvc.perform(patch("/dbs/review/update/{userId}", USER_ID)
                            .contentType("application/json")
                            .content("{\"reviewId\":2,\"rating\":5.0,\"comment\":\"Best book!\",\"userId\":12,\"bookId\":\"ISBN-4002\"}"))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }

    @Test
    @DisplayName("DeleteReview-Uri-Positive")
    void test_deleteReview_uri_positive() throws UserNotAuthorizedException {
        try {
            when(reviewService.deleteReview(USER_ID, REVIEW_ID)).thenReturn(true);
            mockMvc.perform(delete("/dbs/review/delete/{userId}/{reviewId}", USER_ID, REVIEW_ID))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }

    @Test
    @DisplayName("DeleteReview-Uri-Negative-NotModified")
    void test_deleteReview_uri_negative_notModified() throws UserNotAuthorizedException {
        try {
            when(reviewService.deleteReview(USER_ID, REVIEW_ID)).thenReturn(false);
            mockMvc.perform(delete("/dbs/review/delete/{userId}/{reviewId}", USER_ID, REVIEW_ID))
                    .andExpect(status().isNotModified())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }

    @Test
    @DisplayName("DeleteReview-Uri-Negative-Unauthorized")
    void test_deleteReview_uri_negative_unauthorized() throws UserNotAuthorizedException {
        try {
            when(reviewService.deleteReview(USER_ID, REVIEW_ID)).thenThrow(new UserNotAuthorizedException("User not authorized"));
            mockMvc.perform(delete("/dbs/review/delete/{userId}/{reviewId}", USER_ID, REVIEW_ID))
                    .andExpect(status().isUnauthorized())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }

    @Test
    @DisplayName("DeleteReview-Uri-Negative-ReviewNotFound")
    void test_deleteReview_uri_negative_reviewNotFound() throws UserNotAuthorizedException {
        try {
            when(reviewService.deleteReview(USER_ID, REVIEW_ID)).thenThrow(new ReviewNotFoundException("Review not found"));
            mockMvc.perform(delete("/dbs/review/delete/{userId}/{reviewId}", USER_ID, REVIEW_ID))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }
}