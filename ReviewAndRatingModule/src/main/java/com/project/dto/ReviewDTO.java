package com.project.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

	@Min(value = 1, message = "{com.project.dto.ReviewDTO.reviewid.min}")
	private long reviewId;
	@DecimalMin(value = "0.1", message = "{com.project.dto.ReviewDTO.rating.min}")
	@Max(value = 5, message = "{com.project.dto.ReviewDTO.rating.max}")
	private float rating;
	@Size(min = 3, max = 200, message = "{com.project.dto.ReviewDTO.comment.size}")
	private String comment;

	@Min(value = 1, message = "{com.project.dto.ReviewDTO.userid.min}")
	private long userId;
	@Size(min = 3, max = 20, message = "{com.project.dto.ReviewDTO.bookid.size}")
	private String bookId;

	public ReviewDTO(String comment) {
		this.comment = comment;
	}
}