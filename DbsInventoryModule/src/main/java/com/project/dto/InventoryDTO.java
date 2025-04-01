package com.project.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Inventory.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDTO {
	/**
	 * The ID of the inventory.
	 */
	private Long inventoryId;

	/**
	 * The quantity of books in the inventory.
	 * Must be non-negative.
	 */
	@Min(value=0, message = "Quantity cannot be negative")
	private int quantity;

	/**
	 * The ID of the book.
	 * Cannot be blank.
	 */
	@NotBlank(message = "Book ID cannot be Blank")
	private String book_Id;
}
