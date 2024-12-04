package com.hcltech.digitalbankingservice.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@Getter
@Setter
public class DebitCardDto {

    private Long debitCardNumber;
    private LocalDate expDate;
    private String cvv;
    private String fullName;
    private Boolean isBlocked;
    private String pin;
    private AccountDto accountDto;

}

