package com.project.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDTO {

	private Long inventoryId;

	@Min(value=0, message = "Quantity cannot be negative")
	private int quantity;

	@NotBlank(message = "Book ID cannot be Blank")
	private String book_Id;
}
