package com.project.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a review entity.
 * Mapped to the "review" table in the database.
 *
 * @author Sabarish Iyer
 */

@Data
@Entity
@Table(name = "review")
@NoArgsConstructor
@AllArgsConstructor
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

	/**
	 * Constructs a new Review with the specified rating, comment, userId, and bookId.
	 *
	 * @param rating the rating of the review
	 * @param comment the comment of the review
	 * @param userId the ID of the user who wrote the review
	 * @param bookId the ID of the book being reviewed
	 */
	public Review(float rating, String comment, long userId, String bookId) {
		this.rating = rating;
		this.comment = comment;
		this.userId = userId;
		this.bookId = bookId;
	}
}
