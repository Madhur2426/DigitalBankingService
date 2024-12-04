package com.hcltech.digitalbankingservice.model;

import jakarta.persistence.*;

import lombok.Getter;

import lombok.Setter;

import java.time.LocalDate;

import java.util.List;

@Getter

@Setter

@Entity

public class CreditCard {

    @Id
    private Long creditCardNumber;

    @Column(nullable = false)
    private LocalDate expDate;

    @Column(nullable = false)
    private String cvv;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private Boolean isBlocked;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_number")
    private Account account;

    @Column(nullable = false)
    private String pin;

    @Column(nullable = false)
    private double cardLimit;

    private double availableLimit;

    @OneToMany(mappedBy = "creditCard", cascade = CascadeType.ALL)
    private List <Transaction> transactions;

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
        transaction.setCreditCard(this);
    }

}