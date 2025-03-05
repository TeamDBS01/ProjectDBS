package com.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

	private int reviewId;
	private float rating;
	private String comment;
	
	private BookDTO book;
	private UserDTO user;

	
}
