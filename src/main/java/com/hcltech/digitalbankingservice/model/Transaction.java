package com.hcltech.digitalbankingservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@ToString
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;

    @Column(nullable = false)
    private Double transactionAmount;

    @Column(nullable = false)
    private LocalDateTime transactionDatetime;

    @Column(nullable = false)
    private String transactionType;

    @ManyToOne
    @JoinColumn(name = "account_number")
    @JsonBackReference
    private Account account;

    @ManyToOne
    @JoinColumn(name = "debit_card_number")
    private DebitCard debitCard;

    @ManyToOne
    @JoinColumn(name = "credit_card_number")
    private CreditCard creditCard;
}
