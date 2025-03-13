package com.project.controller;

import com.jayway.jsonpath.JsonPath;
import com.project.ReviewAndRatingModuleApplication;
import com.project.dto.ReviewDTO;
import com.project.exception.ReviewNotFoundException;
import com.project.exception.UserNotAuthorizedException;
import com.project.exception.UserNotFoundException;
import com.project.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
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
//@WebMvcTest
//@AutoConfigureWebMvc
//@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = {ReviewAndRatingModuleApplication.class})
class ReviewControllerTest {
    @Mock
    private ReviewService reviewService;
    @InjectMocks
    private ReviewController reviewController;

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
    @DisplayName("GetReviewById-Negative-ReviewNotFound")
    void test_getReviewById_negative_reviewNotFound() throws ReviewNotFoundException {
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
    @DisplayName("GetAllReviews-Negative-ReviewNotFound")
    void test_getAllReviews_negative_reviewNotFound() throws ReviewNotFoundException {
        when(reviewService.retrieveAllReviews()).thenThrow(new ReviewNotFoundException("No reviews found"));
        ResponseEntity<List<ReviewDTO>> response = reviewController.getAllReviews();
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
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
    @DisplayName("GetAllReviewsByUserId-Negative-ReviewNotFound")
    void test_getAllReviewsByUserId_negative_reviewNotFound() throws ReviewNotFoundException {
        when(reviewService.retrieveAllReviewsByUserId(USER_ID)).thenThrow(new ReviewNotFoundException("No reviews found for user"));
        ResponseEntity<List<ReviewDTO>> response = reviewController.getAllReviewsByUserId(USER_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).retrieveAllReviewsByUserId(USER_ID);
    }

    @Test
    @DisplayName("AddReviewWithParameters-Positive")
    void test_addReviewWithParameters_positive() throws UserNotFoundException {
        when(reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID)).thenReturn(reviewDTO);
        ResponseEntity<ReviewDTO> response = reviewController.addReview(RATING, COMMENT, USER_ID, BOOK_ID);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(reviewDTO, response.getBody());
        verify(reviewService).addReview(RATING, COMMENT, USER_ID, BOOK_ID);
    }

    @Test
    @DisplayName("AddReviewWithParameters-Negative-BadGateway")
    void test_addReviewWithParameters_negative_badGateway() throws UserNotFoundException {
        when(reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID)).thenThrow(new RuntimeException("Unable to add review"));
        ResponseEntity<ReviewDTO> response = reviewController.addReview(RATING, COMMENT, USER_ID, BOOK_ID);
        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertTrue(response.getBody() == null || response.getBody().getBookId() == null);
        verify(reviewService).addReview(RATING, COMMENT, USER_ID, BOOK_ID);
    }

    @Test
    @DisplayName("AddReviewWithParameters-Negative-UserNotFound")
    void test_addReviewWithParameters_negative_userNotFound() throws UserNotFoundException {
        when(reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID)).thenThrow(new UserNotFoundException("User Not Found - Review Not added"));
        ResponseEntity<ReviewDTO> response = reviewController.addReview(RATING, COMMENT, USER_ID, BOOK_ID);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody() == null || response.getBody().getBookId() == null);
        verify(reviewService).addReview(RATING, COMMENT, USER_ID, BOOK_ID);
    }


    @Test
    @DisplayName("AddReviewWithReviewDTO-Positive")
    void test_addReviewWithReviewDTO_positive() throws UserNotFoundException {
        when(reviewService.addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId())).thenReturn(reviewDTO);
        ResponseEntity<ReviewDTO> response = reviewController.addReview(reviewDTO);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(reviewDTO, response.getBody());
        verify(reviewService).addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId());
    }

    @Test
    @DisplayName("AddReviewWithReviewDTO-Negative-BadGateway")
    void test_addReviewWithReviewDTO_negative_badGateway() throws UserNotFoundException {
        when(reviewService.addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId())).thenThrow(new RuntimeException("Unable to add review"));
        ResponseEntity<ReviewDTO> response = reviewController.addReview(reviewDTO);
        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId());
    }

    @Test
    @DisplayName("AddReviewWithReviewDTO-Negative-UserNotFound")
    void test_addReviewWithReviewDTO_negative_userNotFound() throws UserNotFoundException {
        when(reviewService.addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId())).thenThrow(new UserNotFoundException("User Not Found - Review Not Added"));
        ResponseEntity<ReviewDTO> response = reviewController.addReview(reviewDTO);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId());
    }

    @Test
    @DisplayName("UpdateReview-Positive-SameUser")
    void test_updateReview_positive_sameUser() throws UserNotFoundException, UserNotAuthorizedException {
        when(reviewService.updateReview(USER_ID, reviewDTO)).thenReturn(reviewDTO);
        ResponseEntity<ReviewDTO> response = reviewController.updateReview(USER_ID, reviewDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reviewDTO, response.getBody());
        verify(reviewService).updateReview(USER_ID, reviewDTO);
    }

    @Test
    @DisplayName("UpdateReview-Negative-Unauthorized")
    void test_updateReview_negative_unauthorized() throws UserNotFoundException, UserNotAuthorizedException {
        when(reviewService.updateReview(USER_ID, reviewDTO)).thenThrow(new UserNotAuthorizedException("User not authorized"));
        ResponseEntity<ReviewDTO> response = reviewController.updateReview(USER_ID, reviewDTO);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(reviewService).updateReview(USER_ID, reviewDTO);
    }

    @Test
    @DisplayName("UpdateReview-Negative-UserNotFound")
    void test_updateReview_negative_userNotFound() throws UserNotFoundException, UserNotAuthorizedException {
        when(reviewService.updateReview(USER_ID, reviewDTO)).thenThrow(new UserNotFoundException("User not found"));
        ResponseEntity<ReviewDTO> response = reviewController.updateReview(USER_ID, reviewDTO);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
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
    @DisplayName("GetReviewById-Positive")
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
    @DisplayName("GetReviewById-Negative-ReviewNotFound")
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
    @DisplayName("GetReviewById-Positive-JSON")
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
    @DisplayName("GetAllReviews-Positive")
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
    @DisplayName("GetAllReviews-Negative-ReviewNotFound")
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
    @DisplayName("GetAllReviews-Positive-JSON")
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
    @DisplayName("GetAllReviewsByUserId-Positive")
    void test_getAllReviewsByUserId_uri_positive() {
        try {
            List<ReviewDTO> reviewList = List.of(reviewDTO);
            when(reviewService.retrieveAllReviewsByUserId(USER_ID)).thenReturn(reviewList);
            mockMvc.perform(get("/dbs/review/all/{userId}", USER_ID))
                    .andExpect(status().isFound())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Exception thrown \{e.toString()}");
        }
    }

    @Test
    @DisplayName("GetAllReviewsByUserId-Negative-ReviewNotFound")
    void test_getAllReviewsByUserId_uri_negative_reviewNotFound() {
        try {
            when(reviewService.retrieveAllReviewsByUserId(USER_ID)).thenThrow(new ReviewNotFoundException("No reviews found for user"));
            mockMvc.perform(get("/dbs/review/all/{userId}", USER_ID))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Exception thrown \{e.toString()}");
        }
    }

    @Test
    @DisplayName("GetAllReviewsByUserId-Positive-JSON")
    void test_getAllReviewsByUserId_json_positive() {
        try {
            List<ReviewDTO> reviewList = List.of(reviewDTO);
            when(reviewService.retrieveAllReviewsByUserId(USER_ID)).thenReturn(reviewList);
            MvcResult mvcResult = mockMvc.perform(get("/dbs/review/all/{userId}", USER_ID))
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
    @DisplayName("AddReviewWithParameters-Positive")
    void test_addReviewWithParameters_uri_positive() {
        try {
            when(reviewService.addReview(5.0f, "Best book!", USER_ID, "ISBN-4002")).thenReturn(reviewDTO);
            mockMvc.perform(post("/dbs/review/add/{rating}/{comment}/{userId}/{bookId}", 5.0f, "Best book!", USER_ID, "ISBN-4002"))
                    .andExpect(status().isCreated())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }

    @Test
    @DisplayName("AddReviewWithParameters-Negative-UserNotFound")
    void test_addReviewWithParameters_uri_negative_userNotFound() {
        try {
            when(reviewService.addReview(5.0f, "Best book!", USER_ID, "ISBN-4002")).thenThrow(new UserNotFoundException("User not found"));
            mockMvc.perform(post("/dbs/review/add/{rating}/{comment}/{userId}/{bookId}", 5.0f, "Best book!", USER_ID, "ISBN-4002"))
                    .andExpect(status().isNotFound())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }

    @Test
    @DisplayName("AddReviewWithParameters-Negative-BadGateway")
    void test_addReviewWithParameters_uri_negative_badGateway() {
        try {
            when(reviewService.addReview(5.0f, "Best book!", USER_ID, "ISBN-4002")).thenThrow(new RuntimeException("Unable to add review"));
            mockMvc.perform(post("/dbs/review/add/{rating}/{comment}/{userId}/{bookId}", 5.0f, "Best book!", USER_ID, "ISBN-4002"))
                    .andExpect(status().isBadGateway())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }

    // Test cases for addReview with ReviewDTO
    @Test
    @DisplayName("AddReviewWithReviewDTO-Positive")
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
    @DisplayName("AddReviewWithReviewDTO-Negative-UserNotFound")
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
    @DisplayName("AddReviewWithReviewDTO-Negative-BadGateway")
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

    // Test cases for updateReview
    @Test
    @DisplayName("UpdateReview-Positive")
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
    @DisplayName("UpdateReview-Negative-Unauthorized")
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
    @DisplayName("UpdateReview-Negative-UserNotFound")
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

    // Test cases for deleteReview
    @Test
    @DisplayName("DeleteReview-Positive")
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
    @DisplayName("DeleteReview-Negative-NotModified")
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
    @DisplayName("DeleteReview-Negative-Unauthorized")
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
    @DisplayName("DeleteReview-Negative-ReviewNotFound")
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