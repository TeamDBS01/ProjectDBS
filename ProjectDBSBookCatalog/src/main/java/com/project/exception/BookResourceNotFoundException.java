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
	
	
	private final String message;
	public BookResourceNotFoundException(String message) {
		this.message=message;
	}

}
