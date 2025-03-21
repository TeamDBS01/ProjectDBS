package com.project.controller;

import com.project.dto.ReviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(description = "Review Rest API", name = "ReviewRestAPIV01")
public interface ReviewController {

    @Operation(description = "Get Operation for Returning OK")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Returns OK")})
    @GetMapping("/ok")
    ResponseEntity<Boolean> getOk();

    @Operation(description = "Get Operation for Resource Review by Id")
    @ApiResponses(value = {@ApiResponse(responseCode = "302", description = "Get the review by Id"), @ApiResponse(responseCode = "404", description = "Review not found")})
    ResponseEntity<ReviewDTO> getReviewById(@Min(value = 1, message = "{com.project.dto.ReviewDTO.reviewid.min}") @PathVariable long reviewId);

    @Operation(description = "Get Operation for all Reviews")
    @ApiResponses(value = {@ApiResponse(responseCode = "302", description = "Get all reviews"), @ApiResponse(responseCode = "404", description = "No Reviews found")})
    ResponseEntity<List<ReviewDTO>> getAllReviews();

    @Operation(description = "Get Operation for all Reviews by User Id")
    @ApiResponses(value = {@ApiResponse(responseCode = "302", description = "All reviews found by User Id"), @ApiResponse(responseCode = "404", description = "No Reviews found for given User Id")})
    ResponseEntity<List<ReviewDTO>> getAllReviewsByUserId(@Min(value = 1, message = "{com.project.dto.ReviewDTO.userid.min}") @PathVariable long userId);

    @Operation(description = "Get Operation for all Reviews by Book Id")
    @ApiResponses(value = {@ApiResponse(responseCode = "302", description = "All reviews found by Book Id"), @ApiResponse(responseCode = "404", description = "No Reviews found for given Book Id")})
    ResponseEntity<List<ReviewDTO>> getAllReviewsByBookId(@Size(min = 3, max = 20, message = "{com.project.dto.ReviewDTO.bookid.size}") @PathVariable String bookId);

    @Operation(description = "Add Operation for a Review with rating, comment, userId, and bookId")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Review created"), @ApiResponse(responseCode = "404", description = "User/Book Not Found - Unable to add Review"), @ApiResponse(responseCode = "502", description = "Bad gateway - Unable to add Review")})
    ResponseEntity<ReviewDTO> addReview(@DecimalMin(value = "0.1", message = "{com.project.dto.ReviewDTO.rating.min}") @Max(value = 5, message = "{com.project.dto.ReviewDTO.rating.max}") @RequestParam float rating, @Size(min = 3, max = 200, message = "{com.project.dto.ReviewDTO.comment.size}") @Pattern(regexp = "^\\D.*", message = "{com.project.dto.ReviewDTO.comment.start}") @RequestParam String comment, @Min(value = 1, message = "{com.project.dto.ReviewDTO.userid.min}") @RequestParam long userId, @Size(min = 3, max = 200, message = "{com.project.dto.ReviewDTO.bookid.min}") @RequestParam String bookId);

    @Operation(description = "Add Operation for a Review with ReviewDTO")
    @ApiResponses(value = {@ApiResponse(responseCode = "201", description = "Review created"), @ApiResponse(responseCode = "404", description = "User/Book Not Found - Unable to add Review"), @ApiResponse(responseCode = "502", description = "Bad gateway - Review not created")})
    ResponseEntity<ReviewDTO> addReview(@Valid @RequestBody ReviewDTO reviewDTO);

    @Operation(description = "Update Operation for a Review by User Id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Review updated"), @ApiResponse(responseCode = "400", description = "Bad request - Review Not Updated"), @ApiResponse(responseCode = "401", description = "Unauthorized - User not authorized"), @ApiResponse(responseCode = "404", description = "User not found")})
    ResponseEntity<ReviewDTO> updateReview(@Min(value = 1, message = "{com.project.dto.ReviewDTO.userid.min}") @PathVariable long userId, @Valid @RequestBody ReviewDTO reviewDTO);

    @Operation(description = "Delete Operation for a Review by User Id and Review Id")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Review deleted"), @ApiResponse(responseCode = "304", description = "Review not modified"), @ApiResponse(responseCode = "401", description = "Unauthorized"), @ApiResponse(responseCode = "404", description = "Review or User not found")})
    ResponseEntity<Boolean> deleteReview(@Min(value = 1, message = "{com.project.dto.ReviewDTO.userid.min}") @PathVariable long userId, @Min(value = 1, message = "{com.project.dto.ReviewDTO.reviewid.min}") @PathVariable long reviewId);
}