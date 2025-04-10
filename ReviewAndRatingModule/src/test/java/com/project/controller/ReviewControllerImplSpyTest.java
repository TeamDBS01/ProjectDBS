package com.project.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.project.dto.ReviewDTO;
import com.project.exception.*;
import com.project.feign.BookClient;
import com.project.feign.UserClient;
import com.project.repositories.ReviewRepository;
import com.project.service.ReviewService;
import com.project.service.ReviewServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("preview")
@SpringBootTest
@ActiveProfiles("spytest")
@ExtendWith(MockitoExtension.class)
class ReviewControllerImplSpyTest {
    private static final float RATING = 5f;
    private static final String COMMENT = "Best book!";
    private static final long USER_ID = 12L;
    private static final String BOOK_ID = "ISBN-1212";
    private static long REVIEW_ID;
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private UserClient userClient;
    @Autowired
    private BookClient bookClient;
    private ReviewControllerImpl reviewController;
    private ReviewDTO reviewDTO;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        ReviewService reviewService = new ReviewServiceImpl(reviewRepository, userClient, bookClient, mapper);
        reviewController = new ReviewControllerImpl(Mockito.spy(reviewService));
        mockMvc = MockMvcBuilders.standaloneSetup(reviewController).build();
        reviewDTO = reviewService.addReview(RATING, COMMENT, USER_ID, BOOK_ID);
        REVIEW_ID = reviewDTO.getReviewId();
    }

    @Test
    @DisplayName("GetReviewById-Positive")
    void test_getReviewById_positive() throws ReviewNotFoundException, ServiceUnavailableException {
        ResponseEntity<ReviewDTO> response = reviewController.getReviewById(REVIEW_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reviewDTO, response.getBody());
    }


    @Test
    @DisplayName("GetAllReviews-Positive")
    void test_getAllReviews_positive() throws ReviewNotFoundException, ServiceUnavailableException {
        List<ReviewDTO> reviewList = List.of(reviewDTO);
        ResponseEntity<List<ReviewDTO>> response = reviewController.getAllReviews();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().isEmpty());
        assertTrue(response.getBody().containsAll(reviewList));
    }


    @Test
    @DisplayName("GetAllReviewsByUserId-Positive")
    void test_getAllReviewsByUserId_positive() throws ReviewNotFoundException, ServiceUnavailableException {
        List<ReviewDTO> reviewList = List.of(reviewDTO);
        ResponseEntity<List<ReviewDTO>> response = reviewController.getAllReviewsByUserId(USER_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsAll(reviewList));
        assertFalse(response.getBody().isEmpty());
    }


    @Test
    @DisplayName("GetAllReviewsByBookId-Positive")
    void test_getAllReviewsByBookId_positive() throws ReviewNotFoundException, ServiceUnavailableException {
        List<ReviewDTO> reviewList = List.of(reviewDTO);
        ResponseEntity<List<ReviewDTO>> response = reviewController.getAllReviewsByBookId(BOOK_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsAll(reviewList));
        assertFalse(response.getBody().isEmpty());
    }


    @Test
    @DisplayName("AddReviewWithParameters-Positive")
    void test_addReviewWithParameters_positive() throws UserNotFoundException, BookNotFoundException, ServiceUnavailableException {
        ResponseEntity<ReviewDTO> response = reviewController.addReview(RATING, COMMENT, USER_ID, BOOK_ID);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        reviewDTO.setReviewId(response.getBody().getReviewId());
        assertEquals(reviewDTO, response.getBody());
    }


    @Test
    @DisplayName("AddReviewWithReviewDTO-Positive")
    void test_addReviewWithReviewDTO_positive() throws UserNotFoundException, BookNotFoundException, ServiceUnavailableException {
        ResponseEntity<ReviewDTO> response = reviewController.addReview(reviewDTO);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        reviewDTO.setReviewId(response.getBody().getReviewId());
        assertEquals(reviewDTO, response.getBody());
    }


    @Test
    @DisplayName("UpdateReview-Positive")
    void test_updateReview_positive() throws UserNotFoundException, UserNotAuthorizedException, IDMismatchException, BookNotFoundException, ServiceUnavailableException {
        ResponseEntity<ReviewDTO> response = reviewController.updateReview(USER_ID, reviewDTO);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reviewDTO, response.getBody());
    }


    @Test
    @DisplayName("DeleteReview-Positive")
    void test_deleteReview_positive() throws UserNotFoundException, ReviewNotFoundException, UserNotAuthorizedException, ServiceUnavailableException {
        ResponseEntity<Boolean> response = reviewController.deleteReview(USER_ID, REVIEW_ID);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.hasBody());
    }

//    @Test
//    @DisplayName("GetOk")
//    void test_getOk() {
//        ResponseEntity<Boolean> response = reviewController.getOk();
//        assertEquals(Boolean.TRUE, response.getBody());
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//    }

    @Test
    @DisplayName("GetReviewById-Uri-Positive")
    void test_getReviewById_uri_positive() {
        try {
            mockMvc.perform(get("/dbs/review/{reviewId}", REVIEW_ID))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Exception thrown \{e.toString()}");
        }
    }


    @Test
    @DisplayName("GetReviewById-Json-Positive")
    void test_getReviewById_json_positive() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/dbs/review/{reviewId}", REVIEW_ID))
                    .andExpect(status().isOk())
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

            mockMvc.perform(get("/dbs/review/all"))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Exception thrown \{e.toString()}");
        }
    }


    @Test
    @DisplayName("GetAllReviews-Json-Positive")
    void test_getAllReviews_json_positive() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/dbs/review/all"))
                    .andExpect(status().isOk())
                    .andReturn();

            String jsonData = mvcResult.getResponse().getContentAsString();
            List<ReviewDTO> jsonObject = new ObjectMapper().readValue(jsonData, new TypeReference<>() {});
            ReviewDTO actual = jsonObject.stream()
                    .filter(o -> o.getReviewId() == REVIEW_ID)
                    .findFirst().orElseThrow();
            assertEquals(reviewDTO, actual);
        } catch (Exception e) {
            fail(STR."Exception thrown \{e.toString()}");
        }
    }

    @Test
    @DisplayName("GetAllReviewsByUserId-Uri-Positive")
    void test_getAllReviewsByUserId_uri_positive() {
        try {
            mockMvc.perform(get("/dbs/review/user/{userId}", USER_ID))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Exception thrown \{e.toString()}");
        }
    }


    @Test
    @DisplayName("GetAllReviewsByUserId-Json-Positive")
    void test_getAllReviewsByUserId_json_positive() {
        try {
            MvcResult mvcResult = mockMvc.perform(get("/dbs/review/user/{userId}", USER_ID))
                    .andExpect(status().isOk())
                    .andReturn();

            String jsonData = mvcResult.getResponse().getContentAsString();
            List<ReviewDTO> jsonObject = new ObjectMapper().readValue(jsonData, new TypeReference<>() {});
            ReviewDTO actual = jsonObject.stream()
                    .filter(o -> o.getReviewId() == REVIEW_ID)
                    .findFirst().orElseThrow();
            assertEquals(reviewDTO, actual);
        } catch (Exception e) {
            fail(STR."Exception thrown \{e.toString()}");
        }
    }

    @Test
    @DisplayName("AddReviewWithParameters-Uri-Positive")
    void test_addReviewWithParameters_uri_positive() {
        try {
            mockMvc.perform(post("/dbs/review/add/values")
                            .param("comment", "Best book!")
                            .param("rating", String.valueOf(5.0f))
                            .param("userId", String.valueOf(USER_ID))
                            .param("bookId", "ISBN-1212"))
                    .andExpect(status().isCreated())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }


    @Test
    @DisplayName("AddReviewWithReviewDTO-Uri-Positive")
    void test_addReviewWithReviewDTO_uri_positive() {
        try {
            mockMvc.perform(post("/dbs/review/add")
                            .contentType("application/json")
                            .content("{\"rating\":5.0,\"comment\":\"Best book!\",\"userId\":12,\"bookId\":\"ISBN-1212\"}"))
                    .andExpect(status().isCreated())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }


    @Test
    @DisplayName("UpdateReview-Uri-Positive")
    void test_updateReview_uri_positive() {
        try {
            mockMvc.perform(patch("/dbs/review/update/{userId}", USER_ID)
                            .contentType("application/json")
                            .content(STR."{\"reviewId\":\{REVIEW_ID},\"rating\":4.0,\"comment\":\"Good book!\",\"userId\":12,\"bookId\":\"ISBN-1212\"}"))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }


    @Test
    @DisplayName("DeleteReview-Uri-Positive")
    void test_deleteReview_uri_positive() {
        try {
            assertEquals("true", mockMvc.perform(delete("/dbs/review/delete/{userId}/{reviewId}", USER_ID, REVIEW_ID))
                    .andExpect(status().isOk())
                    .andReturn().getResponse().getContentAsString());
        } catch (Exception e) {
            fail(STR."Error thrown: \{e.toString()}");
        }
    }
}