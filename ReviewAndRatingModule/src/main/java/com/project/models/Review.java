package com.project.models;

import com.project.dto.BookDTO;
import com.project.dto.UserDTO;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int reviewId;
	
	@Column(name = "rating")
	private float rating;
	@Column(name = "comment")
	private String comment;
	
//	@ManyToOne
//	@JoinColumn(name = "userid")
//	private UserDTO user;
//	@ManyToMany
//	private BookDTO book;
//
//	public Review(float rating, String comment, UserDTO userDTO, BookDTO bookDTO) {}
	
}
