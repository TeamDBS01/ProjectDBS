package com.project.exception;

import java.io.Serial;

public class ReviewNotFoundException extends Exception {

	/**
	 * Review not found exception
	 */
	@Serial
	private static final long serialVersionUID = 8L;
	
	public ReviewNotFoundException(String message) {
		super(message);
	}

}
