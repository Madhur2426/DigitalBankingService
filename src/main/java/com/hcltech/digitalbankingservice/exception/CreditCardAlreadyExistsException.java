package com.hcltech.digitalbankingservice.exception;

public class CreditCardAlreadyExistsException extends RuntimeException {

    public CreditCardAlreadyExistsException(String message) {
        super(message);
    }
}
