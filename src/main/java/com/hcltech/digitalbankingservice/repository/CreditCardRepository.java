package com.hcltech.digitalbankingservice.repository;

import com.hcltech.digitalbankingservice.model.Account;
import com.hcltech.digitalbankingservice.model.CreditCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CreditCardRepository extends JpaRepository<CreditCard, Long> {

    @Query("SELECT c FROM CreditCard c WHERE c.account.accountNumber = :accountNumber")
    Optional<List<CreditCard>> findCreditCardByAccountNumber(@Param("accountNumber") Long accountNumber);
}


