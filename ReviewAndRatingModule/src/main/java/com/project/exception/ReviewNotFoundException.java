package com.project.exception;

public class ReviewNotFoundException extends Throwable {

	/**
	 * Review not found exception
	 */
	private static final long serialVersionUID = 5L;
	private String message;
	
	public ReviewNotFoundException(String message) {
		super(message);
	}
	
	public void setMessage(String msg) {
		message = msg;
	}
	
	public String getMessage() {
		return message;
	}
}
