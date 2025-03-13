package com.project.controller;

import com.project.dto.ReviewDTO;
import com.project.exception.ReviewNotFoundException;
import com.project.exception.UserNotAuthorizedException;
import com.project.exception.UserNotFoundException;
import com.project.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Sabarish Iyer
 * ReviewController is a REST controller that handles HTTP requests for managing reviews.
 * It provides endpoints to get, add, update, and delete reviews.
 * The controller uses Swagger annotations for API documentation and validation annotations for input validation.
 */

@SuppressWarnings("preview")
@RestController
@Validated
@RequestMapping("dbs/review")
@Tag(description = "Review Rest API", name = "ReviewRestAPIV01")
public class ReviewController {

	private final ReviewService reviewService;

	@Autowired
	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);

	/**
	 * Retrieves a review by its ID.
	 * @param reviewId The ID of the review to retrieve.
	 * @return ResponseEntity containing the review data or an error message.
	 */
	@Operation(description = "Get Operation for Resource Review by Id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "302", description = "Get the review by Id"),
			@ApiResponse(responseCode = "404", description = "Review not found")
	})
	@GetMapping("/{reviewId}")
	public ResponseEntity<ReviewDTO> getReviewById(@Min(value = 1, message = "{com.project.dto.ReviewDTO.reviewid.min}") @PathVariable long reviewId) {
		ResponseEntity<ReviewDTO> response;
//		HttpHeaders headers = new HttpHeaders();
//		headers.add(HttpHeaders.CONTENT_TYPE, "application/json; charset=UTF-8");
//		headers.add("Location", "/dbs/review");
		try {
			ReviewDTO reviewDTO = reviewService.retrieveReviewById(reviewId);
//			response = new ResponseEntity<>(reviewDTO, headers, HttpStatus.FOUND);
			response = new ResponseEntity<>(reviewDTO, HttpStatus.FOUND);
		} catch (ReviewNotFoundException e) {
			logger.error(e.getMessage());
//			response = new ResponseEntity<>(new ReviewDTO(e.toString()), headers, HttpStatus.NOT_FOUND);
			response = new ResponseEntity<>(new ReviewDTO(e.toString()), HttpStatus.NOT_FOUND);
		}
		return response;
	}

	/**
	 * Retrieves all reviews.
	 * @return ResponseEntity containing a list of all reviews or an error message.
	 */
	@Operation(description = "Get Operation for all Reviews")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "302", description = "Get all reviews"),
			@ApiResponse(responseCode = "404", description = "No Reviews found")
	})
	@GetMapping("/all")
	public ResponseEntity<List<ReviewDTO>> getAllReviews() {
		ResponseEntity<List<ReviewDTO>> response;
		try {
			List<ReviewDTO> reviewDTOList = reviewService.retrieveAllReviews();
			response = new ResponseEntity<>(reviewDTOList, HttpStatus.FOUND);
		} catch (ReviewNotFoundException e) {
			logger.error(e.getMessage());
			response = new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
		}
		return response;
	}

	/**
	 * Retrieves all reviews by a specific user ID.
	 * @param userId The ID of the user whose reviews to retrieve.
	 * @return ResponseEntity containing a list of reviews or an error message.
	 */
	@Operation(description = "Get Operation for all Reviews by User Id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "302", description = "All reviews found by User Id"),
			@ApiResponse(responseCode = "404", description = "No Reviews found for given User Id")
	})
	@GetMapping("/all/{userId}")
	public ResponseEntity<List<ReviewDTO>> getAllReviewsByUserId(@Min(value = 1, message = "{com.project.dto.ReviewDTO.userid.min}") @PathVariable long userId) {
		ResponseEntity<List<ReviewDTO>> response;
		try {
			List<ReviewDTO> reviewDTOList = reviewService.retrieveAllReviewsByUserId(userId);
			response = new ResponseEntity<>(reviewDTOList, HttpStatus.FOUND);
		} catch (ReviewNotFoundException e) {
			logger.error(e.getMessage());
			response = new ResponseEntity<>(List.of(), HttpStatus.NOT_FOUND);
		}
		return response;
	}

	/**
	 * Adds a new review with specified rating, comment, user ID, and book ID.
	 * @param rating The rating of the review.
	 * @param comment The comment of the review.
	 * @param userId The ID of the user adding the review.
	 * @param bookId The ID of the book being reviewed.
	 * @return ResponseEntity containing the created review or an error message.
	 */
	@Operation(description = "Add Operation for a Review with rating, comment, userId, and bookId")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Review created"),
			@ApiResponse(responseCode = "404", description = "User Not Found - Unable to add Review"),
			@ApiResponse(responseCode = "502", description = "Bad gateway - Unable to add Review")
	})
	@PostMapping("/add/{rating}/{comment}/{userId}/{bookId}")
	public ResponseEntity<ReviewDTO> addReview(
			@DecimalMin(value = "0.1", message = "{com.project.dto.ReviewDTO.rating.min}")
			@Max(value = 5, message = "{com.project.dto.ReviewDTO.rating.max}") @PathVariable float rating,
			@Size(min = 3, max = 200, message = "{com.project.dto.ReviewDTO.comment.size}") @PathVariable String comment,
			@Min(value = 1, message = "{com.project.dto.ReviewDTO.userid.min}") @PathVariable long userId,
			@NotBlank(message = "{com.project.dto.ReviewDTO.bookid.min}") @PathVariable String bookId) {
		ResponseEntity<ReviewDTO> response;
        ReviewDTO reviewDTO = null;
        try {
			reviewDTO = reviewService.addReview(rating, comment, userId, bookId);
			response = new ResponseEntity<>(reviewDTO, HttpStatus.CREATED);
		} catch (UserNotFoundException e) {
			logger.error(e.toString());
			response = new ResponseEntity<>(new ReviewDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
			logger.error(e.toString());
			response = new ResponseEntity<>(new ReviewDTO(e.getMessage()), HttpStatus.BAD_GATEWAY);
        }
        return response;
	}

	/**
	 * Adds a new review using a ReviewDTO object.
	 * @param reviewDTO The ReviewDTO object containing review details.
	 * @return ResponseEntity containing the created review or an error message.
	 */
	@Operation(description = "Add Operation for a Review with ReviewDTO")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Review created"),
			@ApiResponse(responseCode = "404", description = "User Not Found - Unable to add Review"),
			@ApiResponse(responseCode = "502", description = "Bad gateway - Review not created")
	})
	@PostMapping("/add")
	public ResponseEntity<ReviewDTO> addReview(@Valid @RequestBody ReviewDTO reviewDTO) {
		ResponseEntity<ReviewDTO> response;
        try {
			reviewDTO = reviewService.addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId());
			response = new ResponseEntity<>(reviewDTO, HttpStatus.CREATED);
		} catch (UserNotFoundException e) {
			logger.error(e.toString());
			response = new ResponseEntity<>(new ReviewDTO(e.getMessage()), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
			logger.error(e.toString());
			response = new ResponseEntity<>(new ReviewDTO(e.getMessage()), HttpStatus.BAD_GATEWAY);
        }
        return response;
	}

	/**
	 * Updates an existing review by user ID.
	 * @param userId The ID of the user updating the review.
	 * @param reviewDTO The ReviewDTO object containing updated review details.
	 * @return ResponseEntity containing the updated review or an error message.
	 */
	@Operation(description = "Update Operation for a Review by User Id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Review updated"),
			@ApiResponse(responseCode = "400", description = "Bad request - Review Not Updated"),
			@ApiResponse(responseCode = "401", description = "Unauthorized - User not authorized"),
			@ApiResponse(responseCode = "404", description = "User not found")
	})
	@PatchMapping("/update/{userId}")
	public ResponseEntity<ReviewDTO> updateReview(
			@Min(value = 1, message = "{com.project.dto.ReviewDTO.userid.min}") @PathVariable long userId,
			@Valid @RequestBody ReviewDTO reviewDTO) {
		ResponseEntity<ReviewDTO> response;
		try {
            reviewDTO = reviewService.updateReview(userId, reviewDTO);
			response = new ResponseEntity<>(reviewDTO, HttpStatus.OK);
        } catch (UserNotAuthorizedException | UserNotFoundException e) {
			logger.error(e.toString());
			response = switch (e) {
				case UserNotAuthorizedException ex -> new ResponseEntity<>(new ReviewDTO(STR."Exception: \{ex.getClass().getSimpleName()}, Message: \{ex.getMessage()}"), HttpStatus.UNAUTHORIZED);
				case UserNotFoundException ex -> new ResponseEntity<>(new ReviewDTO(STR."Exception: \{ex.getClass().getSimpleName()}, Message: \{ex.getMessage()}"), HttpStatus.NOT_FOUND);
				default -> new ResponseEntity<>(new ReviewDTO(STR."Exception: \{e.getClass().getSimpleName()}, Message: \{e.getMessage()}"), HttpStatus.BAD_REQUEST);
			};
        }
		return response;
	}

	/**
	 * Deletes a review by user ID and review ID.
	 * @param userId The ID of the user deleting the review.
	 * @param reviewId The ID of the review to be deleted.
	 * @return ResponseEntity indicating the result of the delete operation.
	 */
	@Operation(description = "Delete Operation for a Review by User Id and Review Id")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Review deleted"),
			@ApiResponse(responseCode = "304", description = "Review not modified"),
			@ApiResponse(responseCode = "401", description = "Unauthorized"),
			@ApiResponse(responseCode = "404", description = "Review or User not found")
	})
	@DeleteMapping("/delete/{userId}/{reviewId}")
	public ResponseEntity<Boolean> deleteReview(
			@Min(value = 1, message = "{com.project.dto.ReviewDTO.userid.min}") @PathVariable long userId,
			@Min(value = 1, message = "{com.project.dto.ReviewDTO.reviewid.min}") @PathVariable long reviewId) {
		ResponseEntity<Boolean> response = new ResponseEntity<>(true, HttpStatus.OK);
		try {
            if (!reviewService.deleteReview(userId, reviewId)) {
				response = new ResponseEntity<>(false, HttpStatus.NOT_MODIFIED);
			}
        } catch (ReviewNotFoundException | UserNotAuthorizedException | UserNotFoundException e) {
			logger.error(e.getMessage());
			response = switch (e) {
				case ReviewNotFoundException ex -> new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
				case UserNotAuthorizedException ex -> new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
				case UserNotFoundException ex -> new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
				default -> new ResponseEntity<>(false, HttpStatus.BAD_REQUEST);
			};
        }
        return response;
	}
}