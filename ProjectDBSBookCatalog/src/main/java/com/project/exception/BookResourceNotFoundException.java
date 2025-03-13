package com.project.exception;

public class BookResourceNotFoundException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private String message;
	public BookResourceNotFoundException(String message) {
		this.message=message;
	}
	
	public String getMessage() {
		return message;
	}
}
