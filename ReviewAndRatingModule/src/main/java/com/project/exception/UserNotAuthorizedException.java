package com.project.exception;

import java.io.Serial;

public class UserNotAuthorizedException extends Exception {

	/**
	 * User not Authorized exception
	 * If user is Not Admin and Not review creator.
	 */
	@Serial
	private static final long serialVersionUID = 5L;
	private final String message;
	
	public UserNotAuthorizedException(String message) {
		super(message);
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
