package com.project.exception;

import java.io.Serial;

public class ServiceUnavailableException extends Exception {

	/**
	 * Service is not availble exception.
	 */
	@Serial
	private static final long serialVersionUID = 9L;

	public ServiceUnavailableException(String message) {
		super(message);
	}

}
