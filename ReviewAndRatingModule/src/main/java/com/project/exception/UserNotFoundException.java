package com.project.exception;

import java.io.Serial;

public class UserNotFoundException extends Exception {

	/**
	 * User Not Found exception
	 * If user is not found.
	 */
	@Serial
	private static final long serialVersionUID = 4L;
	
	public UserNotFoundException(String message) {
		super(message);
	}

}
