package com.project.exception;

/**
 * Exception thrown when attempting to add a book that already exists in the inventory.
 */
public class BookAlreadyExistsException extends RuntimeException {
    /**
     * Constructs a new BookAlreadyExistsException with the specified detail message.
     * @param message the detail message.
     */
    public BookAlreadyExistsException(String message) {
        super(message);
    }
}
