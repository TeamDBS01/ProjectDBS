package com.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("preview")
@ExtendWith(MockitoExtension.class)
class ReviewControllerImplTest {
    private static final long REVIEW_ID = 2L;
    private static final float RATING = 5f;
    private static final String COMMENT = "Best book!";
    private static final long USER_ID = 12L;
    private static final String BOOK_ID = "ISBN-4002";
    private static final String USER_NAME = "Sabarish";
    private static final String BOOK_TITLE = "Guide to Java";
    @Mock
    private ReviewService reviewService;
    @InjectMocks
    private ReviewControllerImpl reviewController;
    private ReviewDTO reviewDTO;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        reviewDTO = new ReviewDTO(REVIEW_ID, RATING, COMMENT, USER_ID, BOOK_ID, USER_NAME, BOOK_TITLE);
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
    }

    @Test
    @DisplayName("GetReviewById-Positive")
    void test_getReviewById_positive() throws ReviewNotFoundException, ServiceUnavailableException {
        when(reviewService.retrieveReviewById(REVIEW_ID)).thenReturn(reviewDTO);
        ResponseEntity<ReviewDTO> response = reviewController.getReviewById(REVIEW_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reviewDTO, response.getBody());
        verify(reviewService).retrieveReviewById(REVIEW_ID);
    }

    @Test
    @DisplayName("GetAllReviews-Positive")
    void test_getAllReviews_positive() throws ReviewNotFoundException, ServiceUnavailableException {
        List<ReviewDTO> reviewList = List.of(reviewDTO);
        when(reviewService.retrieveAllReviews()).thenReturn(reviewList);
        ResponseEntity<List<ReviewDTO>> response = reviewController.getAllReviews();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reviewList, response.getBody());
        verify(reviewService).retrieveAllReviews();
    }


    @Test
    @DisplayName("GetAverageByBookId-Positive")
    void test_getAverageByBookId_positive() {
        List<Float> expected = List.of(RATING, 1f);
        when(reviewService.retrieveAverageRating(any())).thenReturn(expected);
        ResponseEntity<List<Float>> response = reviewController.getAverageByBookId(BOOK_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());
        verify(reviewService).retrieveAverageRating(any());
    }

    @Test
    @DisplayName("GetAllReviewsByUserId-Positive")
    void test_getAllReviewsByUserId_positive() throws ReviewNotFoundException, ServiceUnavailableException {
        List<ReviewDTO> reviewList = List.of(reviewDTO);
        when(reviewService.retrieveAllReviewsByUserId(USER_ID)).thenReturn(reviewList);
        ResponseEntity<List<ReviewDTO>> response = reviewController.getAllReviewsByUserId(USER_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reviewList, response.getBody());
        verify(reviewService).retrieveAllReviewsByUserId(USER_ID);
    }


    @Test
    @DisplayName("GetAllReviewsByBookId-Positive")
    void test_getAllReviewsByBookId_positive() throws ReviewNotFoundException, ServiceUnavailableException {
        List<ReviewDTO> reviewList = List.of(reviewDTO);
        when(reviewService.retrieveAllReviewsByBookId(BOOK_ID)).thenReturn(reviewList);
        ResponseEntity<List<ReviewDTO>> response = reviewController.getAllReviewsByBookId(BOOK_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reviewList, response.getBody());
        verify(reviewService).retrieveAllReviewsByBookId(BOOK_ID);
    }


    @Test
    @DisplayName("AddReviewWithParameters-Positive")
    void test_addReviewWithParameters_positive() throws UserNotFoundException, BookNotFoundException, ServiceUnavailableException {
        when(reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID)).thenReturn(reviewDTO);
        ResponseEntity<ReviewDTO> response = reviewController.addReview(RATING, COMMENT, USER_ID, BOOK_ID);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(reviewDTO, response.getBody());
        verify(reviewService).addReview(RATING, COMMENT, USER_ID, BOOK_ID);
    }


    @Test
    @DisplayName("AddReviewWithReviewDTO-Positive")
    void test_addReviewWithReviewDTO_positive() throws UserNotFoundException, BookNotFoundException, ServiceUnavailableException {
        when(reviewService.addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId())).thenReturn(reviewDTO);
        ResponseEntity<ReviewDTO> response = reviewController.addReview(reviewDTO);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(reviewDTO, response.getBody());
        verify(reviewService).addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId());
    }


    @Test
    @DisplayName("UpdateReview-Positive")
    void test_updateReview_positive() throws UserNotFoundException, UserNotAuthorizedException, IDMismatchException, BookNotFoundException, ServiceUnavailableException {
        when(reviewService.updateReview(USER_ID, reviewDTO)).thenReturn(reviewDTO);
        ResponseEntity<ReviewDTO> response = reviewController.updateReview(USER_ID, reviewDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reviewDTO, response.getBody());
        verify(reviewService).updateReview(USER_ID, reviewDTO);
    }


    @Test
    @DisplayName("DeleteReview-Positive")
    void test_deleteReview_positive() throws UserNotFoundException, ReviewNotFoundException, UserNotAuthorizedException, ServiceUnavailableException {
        when(reviewService.deleteReview(USER_ID, REVIEW_ID)).thenReturn(true);
        ResponseEntity<Boolean> response = reviewController.deleteReview(USER_ID, REVIEW_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());
        verify(reviewService).deleteReview(USER_ID, REVIEW_ID);
    }

    @Test
    @DisplayName("GetAllReviewsFromReviewDelete-Positive")
    void test_getAllReviewsFromReviewDelete_positive() throws ServiceUnavailableException {
        List<ReviewDTO> reviewDeleteList = List.of(reviewDTO);
        when(reviewService.retrieveAllReviewDeletes()).thenReturn(reviewDeleteList);
        ResponseEntity<List<ReviewDTO>> response = reviewController.getAllReviewsFromReviewDelete();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reviewDeleteList, response.getBody());
        verify(reviewService).retrieveAllReviewDeletes();
    }

    @Test
    @DisplayName("AddToReviewDelete-Positive")
    void test_addToReviewDelete_positive() {
        when(reviewService.addToReviewDelete(reviewDTO.getReviewId(), reviewDTO.getReason())).thenReturn(true);
        ResponseEntity<Boolean> response = reviewController.addToReviewDelete(reviewDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.TRUE, response.getBody());
        verify(reviewService).addToReviewDelete(reviewDTO.getReviewId(), reviewDTO.getReason());
    }

    @Test
    @DisplayName("RemoveFromReviewDelete-Positive")
    void test_removeFromReviewDelete_positive() throws UserNotFoundException, ReviewNotFoundException, UserNotAuthorizedException, ServiceUnavailableException {
        when(reviewService.deleteReviewDelete(USER_ID, REVIEW_ID)).thenReturn(true);
        ResponseEntity<Boolean> response = reviewController.removeFromReviewDelete(USER_ID, REVIEW_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(Boolean.TRUE, response.getBody());
        verify(reviewService).deleteReviewDelete(USER_ID, REVIEW_ID);
    }

    @Test
    @DisplayName("GetAllReviewsFromReviewDelete-Uri-Positive")
    void test_getAllReviewsFromReviewDelete_uri_positive() {
        try {
            List<ReviewDTO> reviewDeleteList = List.of(reviewDTO);
            when(reviewService.retrieveAllReviewDeletes()).thenReturn(reviewDeleteList);
            mockMvc.perform(get("/dbs/review/review_delete")).andExpect(status().isOk()).andReturn();
        } catch (Exception e) {
            fail(STR."Exception thrown: \{e}");
        }
    }

    @Test
    @DisplayName("GetAllReviewsFromReviewDelete-Json-Positive")
    void test_getAllReviewsFromReviewDelete_json_positive() {
        try {
            List<ReviewDTO> reviewDeleteList = List.of(reviewDTO);
            when(reviewService.retrieveAllReviewDeletes()).thenReturn(reviewDeleteList);
            MvcResult mvcResult = mockMvc.perform(get("/dbs/review/review_delete")).andExpect(status().isOk()).andReturn();

            String jsonData = mvcResult.getResponse().getContentAsString();
            String actual = JsonPath.parse(jsonData).read("$[0].comment");
            assertEquals("Best book!", actual);
        } catch (Exception e) {
            fail(STR."Exception thrown: \{e.toString()}");
        }
    }

    @Test
    @DisplayName("AddToReviewDelete-Uri-Positive")
    void test_addToReviewDelete_uri_positive() {
        try {
            when(reviewService.addToReviewDelete(reviewDTO.getReviewId(), reviewDTO.getReason())).thenReturn(true);
            mockMvc.perform(post("/dbs/review/review_delete").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(reviewDTO))).andExpect(status().isOk()).andReturn();
        } catch (Exception e) {
            fail(STR."Exception thrown: \{e.toString()}");
        }
    }

    @Test
    @DisplayName("AddToReviewDelete-Json-Positive")
    void test_addToReviewDelete_json_positive() {
        try {
            when(reviewService.addToReviewDelete(reviewDTO.getReviewId(), reviewDTO.getReason())).thenReturn(true);
            MvcResult mvcResult = mockMvc.perform(post("/dbs/review/review_delete").contentType(MediaType.APPLICATION_JSON).content(new ObjectMapper().writeValueAsString(reviewDTO))).andExpect(status().isOk()).andReturn();

            String jsonData = mvcResult.getResponse().getContentAsString();
            Boolean actual = JsonPath.parse(jsonData).read("$");
            assertTrue(actual);
        } catch (Exception e) {
            fail(STR."Exception thrown: \{e.toString()}");
        }
    }

    @Test
    @DisplayName("RemoveFromReviewDelete-Uri-Positive")
    void test_removeFromReviewDelete_uri_positive() {
        try {
            when(reviewService.deleteReviewDelete(USER_ID, REVIEW_ID)).thenReturn(true);
            mockMvc.perform(delete("/dbs/review/review_delete/{userId}/{reviewId}", USER_ID, REVIEW_ID)).andExpect(status().isOk()).andReturn();
        } catch (Exception e) {
            fail(STR."Exception thrown: \{e}");
        }
    }

    @Test
    @DisplayName("RemoveFromReviewDelete-Json-Positive")
    void test_removeFromReviewDelete_json_positive() {
        try {
            when(reviewService.deleteReviewDelete(USER_ID, REVIEW_ID)).thenReturn(true);
            MvcResult mvcResult = mockMvc.perform(delete("/dbs/review/review_delete/{userId}/{reviewId}", USER_ID, REVIEW_ID)).andExpect(status().isOk()).andReturn();

            String jsonData = mvcResult.getResponse().getContentAsString();
            Boolean actual = JsonPath.parse(jsonData).read("$");
            assertTrue(actual);
        } catch (Exception e) {
            fail(STR."Exception thrown: \{e.toString()}");
        }
    }


    @Test
    @DisplayName("GetReviewById-Uri-Positive")
    void test_getReviewById_uri_positive() {
        try {
            when(reviewService.retrieveReviewById(REVIEW_ID)).thenReturn(reviewDTO);
            mockMvc.perform(get("/dbs/review/{reviewId}", REVIEW_ID)).andExpect(status().isOk()).andReturn();
        } catch (Exception e) {
            fail(STR."Exception thrown \{e.toString()}");
        }
    }


    @Test
    @DisplayName("GetReviewById-Json-Positive")
    void test_getReviewById_json_positive() {
        try {
            when(reviewService.retrieveReviewById(REVIEW_ID)).thenReturn(reviewDTO);
            MvcResult mvcResult = mockMvc.perform(get("/dbs/review/{reviewId}", REVIEW_ID)).andExpect(status().isOk()).andReturn();

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
            mockMvc.perform(get("/dbs/review/all")).andExpect(status().isOk()).andReturn();
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
            MvcResult mvcResult = mockMvc.perform(get("/dbs/review/all")).andExpect(status().isOk()).andReturn();

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
            mockMvc.perform(get("/dbs/review/user/{userId}", USER_ID)).andExpect(status().isOk()).andReturn();
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
            MvcResult mvcResult = mockMvc.perform(get("/dbs/review/user/{userId}", USER_ID)).andExpect(status().isOk()).andReturn();

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
            mockMvc.perform(post("/dbs/review/add/values").param("comment", "Best book!").param("rating", String.valueOf(5.0f)).param("userId", String.valueOf(USER_ID)).param("bookId", "ISBN-4002")).andExpect(status().isCreated()).andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }


    @Test
    @DisplayName("AddReviewWithReviewDTO-Uri-Positive")
    void test_addReviewWithReviewDTO_uri_positive() {
        try {
            when(reviewService.addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId())).thenReturn(reviewDTO);
            mockMvc.perform(post("/dbs/review/add").contentType("application/json").content("{\"rating\":5.0,\"comment\":\"Best book!\",\"userId\":12,\"bookId\":\"ISBN-4002\"}")).andExpect(status().isCreated()).andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }


    @Test
    @DisplayName("UpdateReview-Uri-Positive")
    void test_updateReview_uri_positive() {
        try {
            when(reviewService.updateReview(USER_ID, reviewDTO)).thenReturn(reviewDTO);
            mockMvc.perform(put("/dbs/review/update/{userId}", USER_ID).contentType("application/json").content("{\"reviewId\":2,\"rating\":5.0,\"comment\":\"Best book!\",\"userId\":12,\"bookId\":\"ISBN-4002\",\"userName\":\"Sabarish\",\"bookTitle\":\"Guide to Java\"}")).andExpect(status().isOk()).andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }


    @Test
    @DisplayName("DeleteReview-Uri-Positive")
    void test_deleteReview_uri_positive() {
        try {
            when(reviewService.deleteReview(USER_ID, REVIEW_ID)).thenReturn(true);
            mockMvc.perform(delete("/dbs/review/delete/{userId}/{reviewId}", USER_ID, REVIEW_ID)).andExpect(status().isOk()).andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }


    @Test
    @DisplayName("GetAverageByBookId-Uri-Positive")
    void test_getAverageByBookId_uri_positive() {
        when(reviewService.retrieveAverageRating(any())).thenReturn(List.of(RATING, 1f));
        try {
            mockMvc.perform(get("/dbs/review/book/average/{bookId}", BOOK_ID)).andExpect(status().isOk()).andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown:- \{e}");
        }
        verify(reviewService).retrieveAverageRating(any());
    }

}