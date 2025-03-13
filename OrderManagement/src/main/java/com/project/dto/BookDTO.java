package com.project.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BookDTO {
	private String BookID;
	private String title;
	private double price;
	private int StockQuantity;
	private int authorId;
	private int categoryId;
}
