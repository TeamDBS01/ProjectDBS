package com.project.exception;

public class ReviewNotFoundException extends Exception {

	/**
	 * Review not found exception
	 */
	private static final long serialVersionUID = 5L;
	
	public ReviewNotFoundException(String message) {
		super(message);
	}

}
