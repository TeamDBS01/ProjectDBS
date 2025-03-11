package com.project.exception;

public class UserNotFoundException extends Exception {

	/**
	 * User not Authorized exception
	 * If user is Not Admin and Not review creator.
	 */
	private static final long serialVersionUID = 4L;
	
	public UserNotFoundException(String message) {
		super(message);
	}

}
