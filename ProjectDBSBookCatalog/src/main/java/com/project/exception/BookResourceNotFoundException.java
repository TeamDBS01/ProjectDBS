package com.project.exception;

import lombok.Getter;

import java.io.Serial;

@Getter
public class BookResourceNotFoundException extends Exception{
	
	/**
	 * 
	 */
	@Serial
	private static final long serialVersionUID = 1L;

	public BookResourceNotFoundException(String message) {
		super(message);
	}

}
