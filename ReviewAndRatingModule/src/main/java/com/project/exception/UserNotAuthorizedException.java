package com.project.exception;

public class UserNotAuthorizedException extends Throwable {

	/**
	 * User not Authorized exception
	 * If user is Not Admin and Not review creator.
	 */
	private static final long serialVersionUID = 6L;
	private String message;
	
	public UserNotAuthorizedException(String message) {
		super(message);
	}

//	public void setMessage(String msg) { message = msg; }

	@Override
	public String getMessage() {
		return message;
	}
}
