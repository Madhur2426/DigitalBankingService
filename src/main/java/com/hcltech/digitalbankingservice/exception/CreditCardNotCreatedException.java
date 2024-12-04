package com.hcltech.digitalbankingservice.exception;

public class CreditCardNotCreatedException extends RuntimeException{
    private Long accountNumber;

    public CreditCardNotCreatedException(Long accountNumber) {
        super(String.format("CreditCard with AccountNumber %d can't be created as per eligibility..", accountNumber));
        this.accountNumber = accountNumber;

    }

}
