package com.project.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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

	@Lob
	@Column(name="cover_img", columnDefinition = "LONGBLOB")
	private byte[] coverImage;

	@Column(name="description")
	private String description;

	@Lob
	@Column(name="sample_chapter")
	private byte[] sampleChapter;


	public Book(String bookID, String title, double price, int authorID, int categoryID, String description) {
		this.bookID = bookID;
		this.title = title;
		this.price = price;
		this.authorID = authorID;
		this.categoryID = categoryID;
		this.description=description;
	}
}

