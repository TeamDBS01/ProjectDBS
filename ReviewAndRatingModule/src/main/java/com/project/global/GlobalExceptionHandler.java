package com.project.global;

import com.project.exception.*;
import jakarta.validation.ConstraintViolationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

@SuppressWarnings("preview")
@RestControllerAdvice
public class GlobalExceptionHandler {

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

    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    @ExceptionHandler(IllegalStateException.class)
    public String handleServiceUnavailableException(IllegalStateException exception) {
        String str = "Unknown Exception In Global Exception Handler";
        if (exception.getCause().getClass().equals(ServiceUnavailableException.class)) {
            Scanner scanner=new Scanner(exception.getMessage());
            scanner.useDelimiter("xception: ");
            while(scanner.hasNext()){
                str = scanner.next();
            }
            scanner.close();
        }
        else{
            str = handleException(exception).getBody();
        }
        return str;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IDMismatchException.class, UserNotFoundException.class, UserNotAuthorizedException.class, ReviewNotFoundException.class, BookNotFoundException.class})
    public String handleCustomExceptions(Exception exception) {
        String str = "Unknown Exception In Global Exception Handler";
        Scanner scanner=new Scanner(exception.getMessage());
        scanner.useDelimiter("xception: ");
        while(scanner.hasNext()){
            str = scanner.next();
        }
        scanner.close();
        return str;
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({Exception.class})
    protected ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity
                .badRequest()
                .body(STR."Exception occurred inside API :- \{e}");
    }
}