package com.project.global;

import com.project.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application.
 * Handles various exceptions and returns appropriate HTTP responses.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ConstraintViolationException, typically thrown during request validation.
     *
     * @param exception The ConstraintViolationException.
     * @return A map of field names to error messages.
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, String> handleConstraintViolationException(ConstraintViolationException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getConstraintViolations().forEach(violation -> {
            String fieldName = ((PathImpl) violation.getPropertyPath()).getLeafNode().getName();
            errors.put(fieldName, violation.getMessage());
        });
        return errors;
    }

    /**
     * Handles MethodArgumentNotValidException, typically thrown during request body validation.
     *
     * @param exception The MethodArgumentNotValidException.
     * @return A map of field names to error messages.
     */
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }

    /**
     * Handles ResourceNotFoundException, typically thrown when a requested resource is not found.
     *
     * @param ex The ResourceNotFoundException.
     * @return A ResponseEntity with the error message and HTTP status NOT_FOUND.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Handles InsufficientStockException, typically thrown when there is insufficient stock for an order.
     *
     * @param ex The InsufficientStockException.
     * @return A ResponseEntity with the error message and HTTP status BAD_REQUEST.
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<String> handleInsufficientStockException(InsufficientStockException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handles CartEmptyException, typically thrown when attempting to place an order with an empty cart.
     *
     * @param ex The CartEmptyException.
     * @return A ResponseEntity with the error message and HTTP status BAD_REQUEST.
     */
    @ExceptionHandler(CartEmptyException.class)
    public ResponseEntity<String> handleCartEmptyException(CartEmptyException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handles OrderAlreadyPaidException, typically thrown when attempting to pay for an already paid order.
     *
     * @param ex The OrderAlreadyPaidException.
     * @return A ResponseEntity with the error message and HTTP status BAD_REQUEST.
     */
    @ExceptionHandler(OrderAlreadyPaidException.class)
    public ResponseEntity<String> handleOrderAlreadyPaidException(OrderAlreadyPaidException ex){
        return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());

    }

    /**
     * Handles InsufficientCreditsException, typically thrown when a user has insufficient credits for a payment.
     *
     * @param ex The InsufficientCreditsException.
     * @return A ResponseEntity with the error message and HTTP status BAD_REQUEST.
     */
    @ExceptionHandler(InsufficientCreditsException.class)
    public ResponseEntity<String> handleInsufficientCreditsException(InsufficientCreditsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handles SecurityException, typically thrown when a user does not have the required permissions.
     *
     * @param ex The SecurityException.
     * @return A ResponseEntity with the error message and HTTP status FORBIDDEN.
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurityException(SecurityException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    /**
     * Handles IllegalArgumentException, typically thrown when an invalid argument is passed.
     *
     * @param ex The IllegalArgumentException.
     * @return A ResponseEntity with the error message and HTTP status BAD_REQUEST.
     */
    @ExceptionHandler( IllegalArgumentException.class)
    public ResponseEntity<String> handleGenericException( IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handles generic Exception, typically thrown when an unexpected error occurs.
     *
     * @param ex The Exception.
     * @return A ResponseEntity with a generic error message and HTTP status INTERNAL_SERVER_ERROR.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
    }


    /**
     * Handles exceptions of type StatusNotFoundException.
     *
     * This exception is typically thrown when an operation is attempted on an order
     * with an invalid status (e.g., requesting a return on an order that is not delivered).
     *
     * @param ex The StatusNotFoundException that was thrown.
     * @return A ResponseEntity with a BAD_REQUEST status and the exception message.
     */
    @ExceptionHandler(StatusNotFoundException.class)
    public ResponseEntity<String> handleStatusNotFoundException(StatusNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    /**
     * Handles exceptions of type IllegalStateException.
     *
     * This exception is thrown when an operation is attempted that is not permitted
     * in the current state of the application or order (e.g., canceling a delivered order,
     * updating the status of a returned order).
     *
     * @param ex The IllegalStateException that was thrown.
     * @return A ResponseEntity with a BAD_REQUEST status and the exception message.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalStateException(IllegalStateException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

}
