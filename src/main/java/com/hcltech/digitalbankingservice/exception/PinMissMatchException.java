package com.hcltech.digitalbankingservice.exception;

public class PinMissMatchException extends RuntimeException{
    private String message;

    public PinMissMatchException(String message) {
        super(message);
        this.message = message;
    }

}
