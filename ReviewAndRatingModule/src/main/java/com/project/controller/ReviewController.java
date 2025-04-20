package com.project.controller;

import com.project.dto.ReviewDTO;
import com.project.exception.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(description = "Review Rest API", name = "ReviewRestAPIV01")
public interface ReviewController {

    @Operation(description = "Get Operation for Resource Review by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get the review by Id"),
    })
    ResponseEntity<ReviewDTO> getReviewById(@Min(value = 1, message = "{com.project.dto.ReviewDTO.reviewid.min}") @PathVariable long reviewId) throws ReviewNotFoundException, ServiceUnavailableException;

    @Operation(description = "Get Operation for all Reviews")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get all reviews"),
    })
    ResponseEntity<List<ReviewDTO>> getAllReviews() throws ReviewNotFoundException, ServiceUnavailableException;

    @Operation(description = "Get Operation for all Reviews by User Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All reviews found by User Id"),
    })
    ResponseEntity<List<ReviewDTO>> getAllReviewsByUserId(@Min(value = 1, message = "{com.project.dto.ReviewDTO.userid.min}") @PathVariable long userId) throws ReviewNotFoundException, ServiceUnavailableException;

    @Operation(description = "Get Operation for all Reviews by Book Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All reviews found by Book Id"),
    })
    ResponseEntity<List<ReviewDTO>> getAllReviewsByBookId(@Size(min = 3, max = 20, message = "{com.project.dto.ReviewDTO.bookid.size}") @PathVariable String bookId) throws ReviewNotFoundException, ServiceUnavailableException;

    @Operation(description = "Get Operation for average Rating by Book Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Average Rating found by Book Id"),
    })
    ResponseEntity<List<Float>> getAverageByBookId(@Size(min = 3, max = 20, message = "{com.project.dto.ReviewDTO.bookid.size}") @PathVariable String bookId) throws ReviewNotFoundException, ServiceUnavailableException;

    @Operation(description = "Add Operation for a Review with rating, comment, userId, and bookId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created"),
    })
    ResponseEntity<ReviewDTO> addReview(@DecimalMin(value = "0.1", message = "{com.project.dto.ReviewDTO.rating.min}") @Max(value = 5, message = "{com.project.dto.ReviewDTO.rating.max}") @RequestParam float rating,
                                        @Size(min = 3, max = 2000, message = "{com.project.dto.ReviewDTO.comment.size}") @Pattern(regexp = "^\\D.*", message = "{com.project.dto.ReviewDTO.comment.start}") @RequestParam String comment,
                                        @Min(value = 1, message = "{com.project.dto.ReviewDTO.userid.min}") @RequestParam long userId,
                                        @Size(min = 3, max = 20, message = "{com.project.dto.ReviewDTO.bookid.size}") @RequestParam String bookId) throws UserNotFoundException, BookNotFoundException, ServiceUnavailableException;

    @Operation(description = "Add Operation for a Review with ReviewDTO")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review created"),
    })
    ResponseEntity<ReviewDTO> addReview(@Valid @RequestBody ReviewDTO reviewDTO) throws UserNotFoundException, BookNotFoundException, ServiceUnavailableException;

    @Operation(description = "Update Operation for a Review by User Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review updated"),
    })
    ResponseEntity<ReviewDTO> updateReview(@Min(value = 1, message = "{com.project.dto.ReviewDTO.userid.min}") @PathVariable long userId,
                                           @Valid @RequestBody ReviewDTO reviewDTO) throws ServiceUnavailableException, UserNotFoundException, UserNotAuthorizedException, IDMismatchException, BookNotFoundException;

    @Operation(description = "Delete Operation for a Review by User Id and Review Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Review deleted"),
    })
    ResponseEntity<Boolean> deleteReview(@Min(value = 1, message = "{com.project.dto.ReviewDTO.userid.min}") @PathVariable long userId,
                                         @Min(value = 1, message = "{com.project.dto.ReviewDTO.reviewid.min}") @PathVariable long reviewId) throws UserNotFoundException, ReviewNotFoundException, UserNotAuthorizedException, ServiceUnavailableException;

    @Operation(description = "Add Operation for a ReviewDelete with reviewID and comment")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Review Delete created"),
    })
    ResponseEntity<Boolean> addToReviewDelete(ReviewDTO reviewDTO);
}