package com.hcltech.digitalbankingservice.repository;

import com.hcltech.digitalbankingservice.model.CreditCard;
import com.hcltech.digitalbankingservice.model.DebitCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DebitCardRepository extends JpaRepository<DebitCard, Long> {

    @Query("SELECT c FROM DebitCard c WHERE c.account.accountNumber = :accountNumber")
    Optional<List<DebitCard>> findDebitCardByAccountNumber(@Param("accountNumber") Long accountNumber);

    Optional<DebitCard> findByAccount_AccountNumber(Long accountNumber);
}


