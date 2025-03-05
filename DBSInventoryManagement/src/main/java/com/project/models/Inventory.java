package com.project.models;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(name="Inventory")
public class Inventory {
	@Id
	@Column(name="inventory_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int inventoryID;
    //private Book book;
	@Column(name="book_quantity")
    private int quantity;
}
