package com.hcltech.digitalbankingservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.hcltech.digitalbankingservice.model.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    @Query("SELECT COUNT(c) > 0 FROM Customer c WHERE c.aadharNumber = :aadharNumber")
    boolean existsByAadharNumber(@Param("aadharNumber") String aadharNumber);
}

