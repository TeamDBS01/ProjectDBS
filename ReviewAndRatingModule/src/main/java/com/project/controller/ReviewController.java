package com.project.controller;

import com.project.dto.ReviewDTO;
import com.project.exception.ReviewNotFoundException;
import com.project.exception.UserNotAuthorizedException;
import com.project.exception.UserNotFoundException;
import com.project.service.ReviewService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * global excep handler
 * SonarQube75 - NOT COVERING SERVICE
 * Swagger - docs for all files
 * AOP
 * Know all things mets fets used in file
 * Java21 - StringTemplate
 * Tekstac
 */

@RestController
@RequestMapping("dbs/review")
public class ReviewController {

	private final ReviewService reviewService;

	@Autowired
	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	private static final Logger logger = LoggerFactory.getLogger(ReviewController.class);


	@GetMapping("{reviewId}")
	@ResponseBody
	public ResponseEntity<ReviewDTO> getReviewById(@PathVariable long reviewId) {
		ResponseEntity<ReviewDTO> response;
		try {
			ReviewDTO reviewDTO = reviewService.retrieveReviewById(reviewId);
			response = new ResponseEntity<>(reviewDTO, HttpStatus.FOUND);
		} catch (ReviewNotFoundException e) {
			logger.error(e.getMessage());
			response = new ResponseEntity<>(new ReviewDTO(), HttpStatus.NOT_FOUND);
		}
		return response;
	}

	@GetMapping("/all")
	@ResponseBody
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

	@GetMapping("/all/{userId}")
	@ResponseBody
	public ResponseEntity<List<ReviewDTO>> getAllReviewsByUserId(@PathVariable long userId) {
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

	@PostMapping("/add/{rating}/{comment}/{userId}/{bookId}")
	public ResponseEntity<ReviewDTO> addReview(@PathVariable float rating, @PathVariable String comment, @PathVariable long userId, @PathVariable String bookId) {
		ResponseEntity<ReviewDTO> response;
        ReviewDTO reviewDTO = null;
        try {
            reviewDTO = reviewService.addReview(rating, comment, userId, bookId);
			response = new ResponseEntity<>(reviewDTO, HttpStatus.CREATED);
        } catch (Exception e) {
			logger.error(e.getMessage());
			response = new ResponseEntity<>(reviewDTO, HttpStatus.BAD_GATEWAY);
        }
        return response;
	}

	@PostMapping("/add/")
	public ResponseEntity<ReviewDTO> addReview(@RequestBody ReviewDTO reviewDTO) {
		ResponseEntity<ReviewDTO> response;
        try {
            reviewDTO = reviewService.addReview(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(), reviewDTO.getBookId());
			response = new ResponseEntity<>(reviewDTO, HttpStatus.CREATED);
        } catch (Exception e) {
			logger.error(e.getMessage());
			response = new ResponseEntity<>(reviewDTO, HttpStatus.BAD_GATEWAY);
        }
        return response;
	}

	@PatchMapping("/update/{userId}")
	public ResponseEntity<ReviewDTO> updateReview(@PathVariable long userId, @RequestBody ReviewDTO reviewDTO) {
		ResponseEntity<ReviewDTO> response;
		try {
            reviewDTO = reviewService.updateReview(userId, reviewDTO);
			response = new ResponseEntity<>(reviewDTO, HttpStatus.OK);
        } catch (UserNotAuthorizedException | UserNotFoundException e) {
			logger.error(e.getMessage());
			response = new ResponseEntity<>(new ReviewDTO(), HttpStatus.NOT_MODIFIED);
        }
		return response;
	}

	@DeleteMapping("/delete/{userId}/{reviewId}")
	public ResponseEntity<Boolean> deleteReview(@PathVariable long userId, @PathVariable long reviewId) {
		ResponseEntity<Boolean> response = new ResponseEntity<>(true, HttpStatus.OK);
		try {
            if (!reviewService.deleteReview(userId, reviewId)) {
				response = new ResponseEntity<>(false, HttpStatus.NOT_MODIFIED);
			}
        } catch (ReviewNotFoundException | UserNotAuthorizedException | UserNotFoundException e) {
			logger.error(e.getMessage());
			response = new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
        }
        return response;
	}
}