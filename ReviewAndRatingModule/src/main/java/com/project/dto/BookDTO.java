package com.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {

	private String bookID;
	private String title;
	private double price;

	private long inventoryID;
	private int authorID;
	private int categoryID;
}
