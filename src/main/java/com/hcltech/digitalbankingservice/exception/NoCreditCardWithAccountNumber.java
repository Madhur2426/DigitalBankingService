package com.hcltech.digitalbankingservice.exception;

public class NoCreditCardWithAccountNumber extends RuntimeException {

    private Long accountNumber;

    public NoCreditCardWithAccountNumber(Long accountNumber) {

        super(String.format("CreditCard with AccountNumber %d not found", accountNumber));

        this.accountNumber = accountNumber;

    }

}