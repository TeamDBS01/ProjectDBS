package com.project.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

	private Long reviewId;

	@DecimalMin(value = "0.1", message = "{com.project.dto.ReviewDTO.rating.min}")
	@Max(value = 5, message = "{com.project.dto.ReviewDTO.rating.max}")
	private Float rating;

	@Size(min = 3, max = 2000, message = "{com.project.dto.ReviewDTO.comment.size}")
	@Pattern(regexp = "^\\D.*", message = "{com.project.dto.ReviewDTO.comment.start}")
	private String comment;

	@Min(value = 1, message = "{com.project.dto.ReviewDTO.userid.min}")
	private Long userId;
	@Size(min = 3, max = 20, message = "{com.project.dto.ReviewDTO.bookid.size}")
	private String bookId;

	private String userName;
//	private String bookTitle;

	public ReviewDTO(String comment) {
		this.comment = comment;
	}

	public ReviewDTO(Long reviewId, Float rating, String comment, Long userId, String bookId) {
		this.reviewId = reviewId;
		this.rating = rating;
		this.comment = comment;
		this.userId = userId;
		this.bookId = bookId;
	}
}