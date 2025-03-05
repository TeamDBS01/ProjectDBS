package com.project.entities;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
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
	private int reviewId;
	@Column(name = "user_id")
	private int userId;
	@Column(name = "book_id")
	private String bookId;
	@Column(name = "rating")
	private int rating;
	@Column(name = "comment")
	private String comment;
	
//	@ManyToMany
//	private List<Book> book;
//	@ManyToOne
//	@JoinColumn(name = "userid")
//	private User user;

}
