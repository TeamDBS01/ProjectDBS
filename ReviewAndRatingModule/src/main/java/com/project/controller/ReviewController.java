package com.project.controller;

import com.project.dto.ReviewDTO;
import com.project.exception.ReviewNotFoundException;
import com.project.exception.UserNotAuthorizedException;
import com.project.exception.UserNotFoundException;
import com.project.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// DTO - ent - only prims, use YAML
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

	@Autowired
	private ReviewService reviewService;

	@GetMapping("{reviewId}")
	@ResponseBody
	public ResponseEntity<?> get(@PathVariable long reviewId) {
		ResponseEntity<?> response;
		try {
			ReviewDTO reviewDTO = reviewService.retrieveReviewById(reviewId);
			response = new ResponseEntity<>(reviewDTO, HttpStatus.OK);
		} catch (ReviewNotFoundException e) {
			response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		return response;
	}

	@GetMapping("all")
	@ResponseBody
	public ResponseEntity<?> getAll() {
		ResponseEntity<?> response;
		try {
			List<ReviewDTO> reviewDTOList = reviewService.retrieveAllReviews();
			response = new ResponseEntity<>(reviewDTOList, HttpStatus.OK);
		} catch (ReviewNotFoundException e) {
			response = new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		return response;
	}

	@PostMapping("/add/{rating}/{comment}/{userId}")
	public ResponseEntity<Boolean> add(@PathVariable float rating, @PathVariable String comment, @PathVariable long userId) {
//		ReviewDTO reviewDTO = new ReviewDTO(1, 4.5f, "Comment", 22l, "ISBN-0001");
		reviewService.addReview(rating, comment, userId, "BOOK_ISBN-AB");
		return new ResponseEntity<>(true, HttpStatus.OK);
	}
	@PostMapping("/update/{reviewId}/{rating}/{comment}/{userId}")
	public ResponseEntity<String> add(@PathVariable long reviewId,@PathVariable float rating, @PathVariable String comment, @PathVariable long userId) {
		ReviewDTO reviewDTO = new ReviewDTO(reviewId, rating, comment, userId, "ISBN-0001");
		ResponseEntity<String> response;
        try {
            reviewService.updateReview(userId, reviewDTO);
			response = new ResponseEntity<>("true", HttpStatus.OK);
        } catch (UserNotAuthorizedException | UserNotFoundException e) {
			response = new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
        return response;
	}
//	@PostMapping("/add")
//	public ResponseEntity<ReviewDTO> addReview(@RequestBody ReviewDTO reviewDTO) {
////		ReviewDTO reviewDTO = new ReviewDTO(1, 4.5f, "Comment", 22l, "ISBN-0001");
//		reviewService.addReview();
//		return new ResponseEntity<ReviewDTO>(reviewDTO, HttpStatus.OK);
//	}
	
	
//	@PostMapping("/rev")
//	public String get(@RequestBody ReviewDTO review) {
//		BookDTO book = new BookDTO("121", "@12", 12d,12);
//		return reviewService.addReview(4, "Commewnt").toString();
//	}
	
}
