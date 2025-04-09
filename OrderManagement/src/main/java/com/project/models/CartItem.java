package com.project.models;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartItem {
	private String BookId;
	private int quantity;
	
	public CartItem(String bookId, int quantity) {
		super();
		BookId = bookId;
		this.quantity = quantity;
	}

}
