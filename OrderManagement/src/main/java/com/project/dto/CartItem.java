package com.project.dto;

public class CartItem {
	private String BookId;
	private int quantity;
	
	public CartItem(String bookId, int quantity) {
		super();
		BookId = bookId;
		this.quantity = quantity;
	}
	public String getBookId() {
		return BookId;
	}
	public void setBookId(String bookId) {
		BookId = bookId;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	
}
