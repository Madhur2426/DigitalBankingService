package com.hcltech.digitalbankingservice.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@Getter
@Setter
public class CreditCardDto {

    private Long creditCardNumber;
    private LocalDate expDate;
    private String cvv;
    private String fullName;
    private Boolean isBlocked;
    private AccountDto accountDto;
    private String pin;
    private Double cardLimit;
    private Double availableLimit;
}
