package com.hcltech.digitalbankingservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FundTransferRequestDto {

    @NotNull(message = "Source account number cannot be null.")
    private Long sourceAccountNumber;

    @NotNull(message = "Target account number cannot be null.")
    private Long targetAccountNumber;

    @NotNull(message = "PIN cannot be null.")
    private Integer pin;

    @NotNull(message = "Amount cannot be null.")
    private Double amount;
}

