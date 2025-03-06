package com.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.project.dto.BookDTO;
import com.project.dto.ReviewDTO;
import com.project.exception.ReviewNotFoundException;
import com.project.service.ReviewService;

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
			ReviewDTO reviewDTO = reviewService.getReview(reviewId);
			response = new ResponseEntity<ReviewDTO>(reviewDTO, HttpStatus.OK);
		} catch (ReviewNotFoundException e) {
			response = new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		return response;
	}

	@GetMapping("all")
	@ResponseBody
	public ResponseEntity<?> getAll() {
		ResponseEntity<?> response;
		try {
			List<ReviewDTO> reviewDTOList = reviewService.getAllReviews();
			response = new ResponseEntity<List<ReviewDTO>>(reviewDTOList, HttpStatus.OK);
		} catch (ReviewNotFoundException e) {
			response = new ResponseEntity<String>(e.getMessage(), HttpStatus.NOT_FOUND);
		}
		return response;
	}

	@PostMapping("/add/{rating}/{comment}")
	public ResponseEntity<Boolean> add(@PathVariable float rating, @PathVariable String comment) {
//		ReviewDTO reviewDTO = new ReviewDTO(1, 4.5f, "Comment", 22l, "ISBN-0001");
		reviewService.addReview(12, "BOOK_ISBN-AB" , rating, comment);
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
	}
	@PostMapping("/update/{reviewId}/{rating}/{comment}")
	public ResponseEntity<Boolean> add(@PathVariable long reviewId,@PathVariable float rating, @PathVariable String comment) {
		ReviewDTO reviewDTO = new ReviewDTO(reviewId, rating, comment, 22l, "ISBN-0001");
		reviewService.updateReview(12, reviewDTO);
		return new ResponseEntity<Boolean>(true, HttpStatus.OK);
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
