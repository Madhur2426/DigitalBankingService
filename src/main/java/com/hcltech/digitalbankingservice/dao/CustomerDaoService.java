package com.hcltech.digitalbankingservice.dao;

import com.hcltech.digitalbankingservice.model.Customer;
import com.hcltech.digitalbankingservice.repository.CustomerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class CustomerDaoService {


    @Autowired
    private CustomerRepository customerRepository;

    public Customer save(Customer customer) {
        customerRepository.save(customer);
        log.info("Customer saved successfully");
        return customer;
    }


    public List<Customer> getAll() {
            return customerRepository.findAll();
    }

    public void delete(long id) {
        customerRepository.findById(id)
                .ifPresent(customerRepository::delete);
    }

    public Customer update(Customer customer) {
            customerRepository.save(customer);
            log.info("Customer updated successfully");
            return customer;
    }

    public Optional<Customer> findCustomerById(long id) {
            return customerRepository.findById(id);
    }

    public boolean isAadharNumberExists(String aadharNumber) {
        boolean exists = customerRepository.existsByAadharNumber(aadharNumber);
        log.info("Account number {} exists: {}", aadharNumber, exists);
        return exists;
    }
}
