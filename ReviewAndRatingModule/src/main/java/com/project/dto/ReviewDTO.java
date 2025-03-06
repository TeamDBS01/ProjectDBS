package com.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

	private long reviewId;	
	private float rating;
	private String comment;
	
	private long userId;
	private String bookId;

}
