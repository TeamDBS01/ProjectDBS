package com.project.exception;

import java.io.Serial;

public class IDMismatchException extends Exception {

	/**
	 * ID Mismatch exception
	 * If review ID or user ID or book ID is changed.
	 */
	@Serial
	private static final long serialVersionUID = 7L;
	private final String message;

	public IDMismatchException(String message) {
		super(message);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
