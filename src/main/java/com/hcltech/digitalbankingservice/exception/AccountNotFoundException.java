package com.hcltech.digitalbankingservice.exception;


public class AccountNotFoundException extends RuntimeException {

    public AccountNotFoundException(String message) {
        super(message);
    }
}

