package com.hcltech.digitalbankingservice.exception;

public class CreditCardNotFound extends RuntimeException {

    private Long creditCardNumber;

    public CreditCardNotFound(Long creditCardNumber) {

        super(String.format("CreditCard with CreditNumber %d not found", creditCardNumber));

        this.creditCardNumber = creditCardNumber;

    }

}

