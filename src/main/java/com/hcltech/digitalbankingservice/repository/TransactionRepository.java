package com.hcltech.digitalbankingservice.repository;

import com.hcltech.digitalbankingservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

    @Query("SELECT t FROM Transaction t WHERE t.account.accountNumber = :accountNumber")
    List<Transaction> findByAccountNumber(@Param("accountNumber") Long accountNumber);

    @Query("SELECT t FROM Transaction t WHERE t.creditCard.creditCardNumber = :creditCardNumber")
    List<Transaction> findByCreditCardNumber(@Param("creditCardNumber") Long creditCardNumber);

    @Query("SELECT t FROM Transaction t WHERE t.debitCard.debitCardNumber = :debitCardNumber")
    List<Transaction> findByDebitCardNumber(@Param("debitCardNumber") Long debitCardNumber);
}
