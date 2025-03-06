package com.project.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Entity
@Table(name = "review")
@AllArgsConstructor
@NoArgsConstructor
public class Review {
	
	@Id
	@Column(name = "review_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long reviewId;
	
	@Column(name = "rating")
	private float rating;
	@Column(name = "comment")
	private String comment;
	@Column(name = "user_id")
	private long userId;
	@Column(name = "book_id")
	private String bookId;

	public Review(float rating, String comment, long userDTO, String bookDTO) {}
	
}
