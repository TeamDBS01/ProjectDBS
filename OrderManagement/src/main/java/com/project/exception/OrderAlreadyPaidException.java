package com.project.exception;

public class OrderAlreadyPaidException extends  RuntimeException{
    public  OrderAlreadyPaidException(String message){
        super(message);
    }
}
