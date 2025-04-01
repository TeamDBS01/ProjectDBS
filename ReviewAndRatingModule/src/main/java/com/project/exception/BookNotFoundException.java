package com.project.exception;

import java.io.Serial;

public class BookNotFoundException extends Exception {

	/**
	 * Book Not Found exception
	 * If book is not found.
	 */
	@Serial
	private static final long serialVersionUID = 6L;

	public BookNotFoundException(String message) {
		super(message);
	}

}
