package com.bankapplication.exception;

public class CustomException extends Exception{
    public CustomException(String errorMessage){
        super(errorMessage);
    }
}
