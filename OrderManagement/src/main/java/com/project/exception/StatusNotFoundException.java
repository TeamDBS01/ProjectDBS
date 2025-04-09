package com.project.exception;

public class StatusNotFoundException extends RuntimeException{
    public StatusNotFoundException(String message){
        super(message);
    }
}