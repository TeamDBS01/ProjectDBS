package com.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.dto.BookDTO;
import com.project.dto.ReviewDTO;
import com.project.service.ReviewService;

@RestController
@RequestMapping("dbs/review")
public class ReviewController {

	@Autowired
	private ReviewService reviewService;

//	@GetMapping()
//	public ResponseEntity<ReviewDTO> get() {
//		ReviewDTO reviewDTO = new ReviewDTO(1, 4.5f, "Comment", 22l, "ISBN-0001");
//		return new ResponseEntity<ReviewDTO>(reviewDTO, HttpStatus.OK);
//	}
	
//	@PostMapping("/{review}")
//	public ResponseEntity<ReviewDTO> add(@PathVariable("review") ReviewDTO reviewDTO) {
////		ReviewDTO reviewDTO = new ReviewDTO(1, 4.5f, "Comment", 22l, "ISBN-0001");
////		reviewService.addReview(0, null)
//		return new ResponseEntity<ReviewDTO>(reviewDTO, HttpStatus.OK);
//	}
	
	
//	@PostMapping("/rev")
//	public String get(@RequestBody ReviewDTO review) {
//		BookDTO book = new BookDTO("121", "@12", 12d,12);
//		return reviewService.addReview(4, "Commewnt").toString();
//	}
	
}
