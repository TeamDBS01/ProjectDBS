package com.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDTO {

	private int reviewId;
	private int userId;
	private String bookId;
	private int rating;
	private String comment;
	
//	@ManyToMany
//	private List<BookDTO> book;
//	@ManyToOne
//	@JoinColumn(name = "userid")
//	private UserDTO user;

	
}
