package com.hcltech.digitalbankingservice.repository;


import com.hcltech.digitalbankingservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    @Query("SELECT a FROM Account a WHERE a.customer.id = :customerId")
    Optional<List<Account>> findAccountsByCustomerId(@Param("customerId") Long customerId);
}

