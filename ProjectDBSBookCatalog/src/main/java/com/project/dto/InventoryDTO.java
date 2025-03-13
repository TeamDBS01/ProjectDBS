package com.project.dto;
 
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
 
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryDTO {
 	@NotNull
	@Positive
	private Long inventoryId;

  	@Positive
	private int quantity;

	  @NotBlank
	private String book_Id;
}