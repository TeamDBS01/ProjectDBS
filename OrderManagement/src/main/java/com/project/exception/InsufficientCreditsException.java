package com.project.exception;

public class InsufficientCreditsException extends RuntimeException{
    public InsufficientCreditsException(String message){
        super(message);
    }
}

