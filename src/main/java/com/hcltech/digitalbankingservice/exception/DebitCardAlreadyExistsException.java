package com.hcltech.digitalbankingservice.exception;

public class DebitCardAlreadyExistsException extends RuntimeException {
    public DebitCardAlreadyExistsException(String message){
        super(message);
    }
}

