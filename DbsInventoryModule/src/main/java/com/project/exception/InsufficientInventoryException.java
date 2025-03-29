package com.project.exception;

/**
 * Exception thrown when there is insufficient inventory to fulfill a request.
 */
public class InsufficientInventoryException extends RuntimeException {

    /**
     * Constructs a new InsufficientInventoryException with the specified detail message.
     * @param message the detail message.
     */
    public InsufficientInventoryException(String message) {
        super(message);
    }
}
