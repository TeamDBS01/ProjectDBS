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

	//@NotNull(message = "{com.project.BookDTO.BookID.notNull}")
	@Size(min = 3, max = 20, message = "{com.project.BookDTO.BookID.size}")
	private String bookID;


	@NotBlank(message = "Title cannot be null")
	@Size(min = 3, max = 100, message = "{com.project.BookDTO.title.size}")
	private String title;

	@Positive(message = "Price must be positive")
	private double price;

    @Positive(message = "authorID must be positive")
	private int authorID;

	@Positive(message = "categoryID must be positive")
	private int categoryID;
}
