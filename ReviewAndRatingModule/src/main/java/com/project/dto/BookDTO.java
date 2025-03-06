package com.project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BookDTO {

	private String BookID;
	private String title;
	private double price;
	private int StockQuantity;
	private int authorId;
	private int categoryId;
}
