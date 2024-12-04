package com.hcltech.digitalbankingservice.dto;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AccountDto {

    private Long accountNumber;
    @Enumerated(EnumType.STRING)
    private AccountTypeDto accountType;
    private Double accountBalance;
    private Integer pin;
    private List<CreditCardDto> creditCards;
    private List<DebitCardDto>  debitCards;
}
