package com.project.exception;

/**
 * Exception thrown when a book is out of stock in the inventory.
 */
public class OutOfStockException extends RuntimeException {

    /**
     * Constructs a new OutOfStockException with the specified detail message.
     * @param message the detail message.
     */
	public OutOfStockException(String message) {
        super(message);
    }

    /**
     * Constructs a new OutOfStockException with the specified detail message and cause.
     * @param message the detail message.
     * @param cause the cause of the exception.
     */
    public OutOfStockException(String message, Throwable cause) {
        super(message, cause);
    }
}
