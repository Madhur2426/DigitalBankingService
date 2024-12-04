package com.hcltech.digitalbankingservice.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
public class DebitCard {

    @Id
    private Long debitCardNumber;

    @Column(nullable = false)
    private LocalDate expDate;

    @Column(nullable = false)
    private String cvv;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private Boolean isBlocked;

    @Column(nullable = false)
    private String pin;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_number")
    @JsonBackReference
    private Account account;

    @OneToMany(mappedBy = "debitCard", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();


    public void addTransaction(Transaction transaction){
        transactions.add(transaction);
        transaction.setDebitCard(this);
    }


}
