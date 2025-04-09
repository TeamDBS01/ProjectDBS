package com.project.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

	@Size(min = 3, max = 20, message = "{com.project.BookDTO.BookID.size}")
	private String bookID;


	@NotBlank(message = "{com.project.BookDTO.title.notBlank}")
	@Size(min = 3, max = 100, message = "{com.project.BookDTO.title.size}")
	private String title;

	@Positive(message ="{com.project.BookDTO.price.positive}")
	private double price;

    @Positive(message ="{com.project.BookDTO.authorID.positive}")
	private int authorID;

	@Positive(message ="{com.project.BookDTO.categoryID.positive}")
	private int categoryID;

	private byte[] coverImage;

	private String base64img;

	private String authorName;

	private String categoryName;
}
