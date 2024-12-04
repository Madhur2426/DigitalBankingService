package com.hcltech.digitalbankingservice.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class TransactionDto {

    private Integer transactionId;
    private Double transactionAmount;
    private LocalDateTime transactionDatetime;
    private String transactionType;
    private AccountDto accountDto;
    private DebitCardDto debitCardDto;
    private CreditCardDto creditCardDto;
}
