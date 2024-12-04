package com.hcltech.digitalbankingservice.service;

import com.hcltech.digitalbankingservice.dao.CustomerDaoService;
import com.hcltech.digitalbankingservice.dto.CustomerDto;
import com.hcltech.digitalbankingservice.exception.CustomerAlreadyExistsException;
import com.hcltech.digitalbankingservice.exception.CustomerNotFoundException;
import com.hcltech.digitalbankingservice.model.Customer;
import com.hcltech.digitalbankingservice.util.EncryptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CustomerService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CustomerDaoService customerDaoService;


    public Optional<CustomerDto> save(CustomerDto customerDto) {
        log.info("Attempting to save customer with Aadhar number: {}", customerDto.getAadharNumber());

        if (checkCustomerIsRegistered(customerDto.getAadharNumber())) {
            log.warn("Customer with Aadhar number {} already exists.", customerDto.getAadharNumber());
            throw new CustomerAlreadyExistsException("Customer with Aadhar number " + customerDto.getAadharNumber() + " already exists.");
        }

        customerDto.setAadharNumber(EncryptionUtil.encrypt(customerDto.getAadharNumber()));
        Customer customer = toEntity(customerDto);
        Customer savedCustomer = customerDaoService.save(customer);

        log.info("Customer saved successfully with ID: {}", savedCustomer.getId());
        return Optional.of(toDto(savedCustomer));
    }

    public boolean checkCustomerIsRegistered(String aadharNumber) {
        boolean exists = customerDaoService.isAadharNumberExists(EncryptionUtil.encrypt(aadharNumber));
        log.info("Aadhar number {} exists: {}", aadharNumber, exists);
        return exists;
    }

    public Optional<CustomerDto> findCustomerById(long id) {
        log.info("Fetching customer by ID: {}", id);
        Optional<Customer> customer = customerDaoService.findCustomerById(id);
        if (customer.isEmpty()) {
            log.error("Customer with ID {} not found.", id);
            throw new CustomerNotFoundException("Customer with ID " + id + " not found.");
        }
        log.info("Customer found with ID: {}", id);
        return customer.map(this::toDto);
    }

    public List<CustomerDto> getAll() {
        log.info("Fetching all customers.");
        List<Customer> customers = customerDaoService.getAll();
        log.info("Total customers found: {}", customers.size());
        return customers.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public Optional<CustomerDto> update(long id, CustomerDto customerDto) {
        log.info("Updating customer with ID: {}", id);

        if (customerDaoService.findCustomerById(id).isEmpty()) {
            log.error("Customer with ID {} not found.", id);
            throw new CustomerNotFoundException("Customer with ID " + id + " not found.");
        }

        Customer customer = toEntity(customerDto);
        customer.setId(id);
        Customer updatedCustomer = customerDaoService.update(customer);

        log.info("Customer with ID {} updated successfully.", id);
        return Optional.of(toDto(updatedCustomer));
    }

    public void delete(long id) {
        log.info("Deleting customer with ID: {}", id);

        if (customerDaoService.findCustomerById(id).isEmpty()) {
            log.error("Customer with ID {} not found.", id);
            throw new CustomerNotFoundException("Customer with ID " + id + " not found.");
        }

        customerDaoService.delete(id);
        log.info("Customer with ID {} deleted successfully.", id);
    }

    private CustomerDto toDto(Customer customer) {
        return modelMapper.map(customer, CustomerDto.class);
    }

    private Customer toEntity(CustomerDto customerDto) {
        return modelMapper.map(customerDto, Customer.class);
    }
}
