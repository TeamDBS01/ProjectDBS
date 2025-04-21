package com.project.controller;

import com.project.dto.ReviewDTO;
import com.project.exception.*;
import com.project.service.ReviewService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ReviewController is a REST controller that handles HTTP requests for managing reviews.
 * It provides endpoints to get, add, update, and delete reviews.
 * The controller uses Swagger annotations for API documentation and validation annotations for input validation.
 *
 * @author Sabarish Iyer
 */

@RestController
@Validated
@RequestMapping("dbs/review")
public class ReviewControllerImpl implements ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewControllerImpl(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * Retrieves a review by its ID.
     *
     * @param reviewId The ID of the review to retrieve.
     * @return ResponseEntity containing the review data or an error message.
     */
    @Override
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> getReviewById(@Min(value = 1, message = "{com.project.dto.ReviewDTO.reviewid.min}") @PathVariable long reviewId) throws ReviewNotFoundException, ServiceUnavailableException {
        ResponseEntity<ReviewDTO> response;
        ReviewDTO reviewDTO = reviewService.retrieveReviewById(reviewId);
        response = new ResponseEntity<>(reviewDTO, HttpStatus.OK);
        return response;
    }

    /**
     * Retrieves all reviews.
     *
     * @return ResponseEntity containing a list of all reviews or an error message.
     */
    @Override
    @GetMapping("/all")
    public ResponseEntity<List<ReviewDTO>> getAllReviews() throws ReviewNotFoundException, ServiceUnavailableException {
        ResponseEntity<List<ReviewDTO>> response;
        List<ReviewDTO> reviewDTOList = reviewService.retrieveAllReviews();
        response = new ResponseEntity<>(reviewDTOList, HttpStatus.OK);
        return response;
    }

    /**
     * Retrieves all reviews by a specific user ID.
     *
     * @param userId The ID of the user whose reviews to retrieve.
     * @return ResponseEntity containing a list of reviews or an error message.
     */
    @Override
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewDTO>> getAllReviewsByUserId(@Min(value = 1, message = "{com.project.dto.ReviewDTO.userid.min}") @PathVariable long userId) throws ReviewNotFoundException, ServiceUnavailableException {
        ResponseEntity<List<ReviewDTO>> response;
        List<ReviewDTO> reviewDTOList = reviewService.retrieveAllReviewsByUserId(userId);
        response = new ResponseEntity<>(reviewDTOList, HttpStatus.OK);
        return response;
    }

    /**
     * Retrieves all reviews by a specific book ID.
     *
     * @param bookId The ID of the user whose reviews to retrieve.
     * @return ResponseEntity containing a list of reviews or an error message.
     */
    @Override
    @GetMapping("/book/{bookId}")
    public ResponseEntity<List<ReviewDTO>> getAllReviewsByBookId(@Size(min = 3, max = 20, message = "{com.project.dto.ReviewDTO.bookid.size}") @PathVariable String bookId) throws ReviewNotFoundException, ServiceUnavailableException {
        ResponseEntity<List<ReviewDTO>> response;
        List<ReviewDTO> reviewDTOList = reviewService.retrieveAllReviewsByBookId(bookId);
        response = new ResponseEntity<>(reviewDTOList, HttpStatus.OK);
        return response;
    }

    @Override
    @GetMapping("/book/average/{bookId}")
    public ResponseEntity<List<Float>> getAverageByBookId(@PathVariable String bookId) {
        return ResponseEntity.ok(reviewService.retrieveAverageRating(bookId));
    }

    /**
     * Adds a new review with specified rating, comment, user ID, and book ID.
     *
     * @param rating  The rating of the review.
     * @param comment The comment of the review.
     * @param userId  The ID of the user adding the review.
     * @param bookId  The ID of the book being reviewed.
     * @return ResponseEntity containing the created review or an error message.
     */
    @Override
    @PostMapping("/add/values")
    public ResponseEntity<ReviewDTO> addReview(
            @DecimalMin(value = "0.1", message = "{com.project.dto.ReviewDTO.rating.min}")
            @Max(value = 5, message = "{com.project.dto.ReviewDTO.rating.max}") @RequestParam float rating,
            @Size(min = 3, max = 2000, message = "{com.project.dto.ReviewDTO.comment.size}") @Pattern(regexp = "^\\D.*", message = "{com.project.dto.ReviewDTO.comment.start}") @RequestParam String comment,
            @Min(value = 1, message = "{com.project.dto.ReviewDTO.userid.min}") @RequestParam long userId,
            @Size(min = 3, max = 20, message = "{com.project.dto.ReviewDTO.bookid.size}") @RequestParam String bookId) throws UserNotFoundException, BookNotFoundException, ServiceUnavailableException {
        ResponseEntity<ReviewDTO> response;
        ReviewDTO reviewDTO = null;
        reviewDTO = reviewService.addReview(rating, comment, userId, bookId);
        response = new ResponseEntity<>(reviewDTO, HttpStatus.CREATED);
        return response;
    }

    /**
     * Adds a new review using a ReviewDTO object.
     *
     * @param reviewDTO The ReviewDTO object containing review details.
     * @return ResponseEntity containing the created review or an error message.
     */
    @Override
    @PostMapping("/add")
    public ResponseEntity<ReviewDTO> addReview(@Valid @RequestBody ReviewDTO reviewDTO) throws UserNotFoundException, BookNotFoundException, ServiceUnavailableException {
        ResponseEntity<ReviewDTO> response;
        reviewDTO = reviewService.addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId());
        response = new ResponseEntity<>(reviewDTO, HttpStatus.CREATED);
        return response;
    }

    /**
     * Updates an existing review by user ID.
     *
     * @param userId    The ID of the user updating the review.
     * @param reviewDTO The ReviewDTO object containing updated review details.
     * @return ResponseEntity containing the updated review or an error message.
     */
    @Override
    @PutMapping("/update/{userId}")
    public ResponseEntity<ReviewDTO> updateReview(
            @Min(value = 1, message = "{com.project.dto.ReviewDTO.userid.min}") @PathVariable long userId,
            @Valid @RequestBody ReviewDTO reviewDTO) throws ServiceUnavailableException, UserNotFoundException, UserNotAuthorizedException, IDMismatchException, BookNotFoundException {
        ResponseEntity<ReviewDTO> response;
        reviewDTO = reviewService.updateReview(userId, reviewDTO);
        response = new ResponseEntity<>(reviewDTO, HttpStatus.OK);
        return response;
    }

    /**
     * Deletes a review by user ID and review ID.
     *
     * @param userId   The ID of the user deleting the review.
     * @param reviewId The ID of the review to be deleted.
     * @return ResponseEntity indicating the result of the delete operation.
     */
    @Override
    @DeleteMapping("/delete/{userId}/{reviewId}")
    public ResponseEntity<Boolean> deleteReview(
            @Min(value = 1, message = "{com.project.dto.ReviewDTO.userid.min}") @PathVariable long userId,
            @Min(value = 1, message = "{com.project.dto.ReviewDTO.reviewid.min}") @PathVariable long reviewId) throws UserNotFoundException, ReviewNotFoundException, UserNotAuthorizedException, ServiceUnavailableException {
        ResponseEntity<Boolean> response = new ResponseEntity<>(true, HttpStatus.OK);
        reviewService.deleteReview(userId, reviewId);
        return response;
    }


    @Override
    @GetMapping("/review_delete")
    public ResponseEntity<List<ReviewDTO>> getAllReviewsFromReviewDelete() throws ServiceUnavailableException {
        return ResponseEntity.ok(reviewService.retrieveAllReviewDeletes());
    }

    @Override
    @PostMapping("/review_delete")
    public ResponseEntity<Boolean> addToReviewDelete(@RequestBody ReviewDTO reviewDTO) {
        return ResponseEntity.ok(reviewService.addToReviewDelete(reviewDTO.getReviewId(), reviewDTO.getReason()));
    }

    @Override
    @DeleteMapping("/review_delete/{userId}/{reviewId}")
    public ResponseEntity<Boolean> removeFromReviewDelete(@PathVariable long userId, @PathVariable long reviewId) throws UserNotFoundException, ReviewNotFoundException, UserNotAuthorizedException, ServiceUnavailableException {
        return ResponseEntity.ok(reviewService.deleteReviewDelete(userId, reviewId));
    }
}