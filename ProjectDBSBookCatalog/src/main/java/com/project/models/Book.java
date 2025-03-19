package com.project.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="book")
public class Book {
	@Id
	@Column(name="book_id")
	private String bookID;
	@Column(name="title")
	private String title;
	@Column(name="price")
	private double price;

	@Column(name="author_id")
	private int authorID;
	@Column(name="category_id")
	private int categoryID;
}
