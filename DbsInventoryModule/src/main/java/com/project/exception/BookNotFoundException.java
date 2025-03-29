package com.project.exception;

/**
 * Exception thrown when a book is not found in the inventory.
 */
public class BookNotFoundException extends RuntimeException {

	private static final long serialVersionUID = 1L;

    /**
     * Constructs a new BookNotFoundException with the specified detail message.
     * @param message the detail message.
     */
	public BookNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructs a new BookNotFoundException with the specified detail message and cause.
     * @param message the detail message.
     * @param cause the cause of the exception.
     */
    public BookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
