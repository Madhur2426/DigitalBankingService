package com.hcltech.digitalbankingservice.controller;

import com.hcltech.digitalbankingservice.dto.CustomerDto;
import com.hcltech.digitalbankingservice.service.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
@Slf4j
@RestController
@RequestMapping("/api/customer")
@Tag(name = "Customer Management", description = "Operations related to customer management")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // Only Admins can create a new customer
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new customer", description = "Creates a new customer with the provided details.")
    @PostMapping
    public ResponseEntity<Optional<CustomerDto>> createCustomer(
            @Parameter(description = "Customer details", required = true) @Valid @RequestBody CustomerDto customerDto) {

        log.info("Request to create a new customer: {}", customerDto);
        Optional<CustomerDto> savedCustomer = customerService.save(customerDto);

        return ResponseEntity.ok(savedCustomer);
    }

    // Only Admins can fetch all customers
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all customers", description = "Fetches a list of all customers.")
    @GetMapping("admin/all")
    public ResponseEntity<List<CustomerDto>> getAllCustomers() {
        log.info("Request to fetch all customers");
        List<CustomerDto> customers = customerService.getAll();
        log.info("Successfully fetched all customers");
        return ResponseEntity.ok(customers);
    }

    // Only Admins can delete a customer
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete a customer", description = "Deletes a customer by the provided ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(
            @Parameter(description = "Customer ID", required = true) @PathVariable long id) {

        log.info("Request to delete customer with ID: {}", id);
        customerService.delete(id);
        log.info("Successfully deleted customer with ID: {}", id);
        return ResponseEntity.ok().build();
    }

    // Both Admins and Users can update customer details
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Update customer details", description = "Updates the details of a customer for the provided ID.")
    @PutMapping("/update/{id}")
    public ResponseEntity<CustomerDto> updateCustomer(
            @Parameter(description = "Customer ID", required = true) @PathVariable("id") long id,
            @Parameter(description = "Updated customer details", required = true) @RequestBody CustomerDto customerDto) {

        log.info("Request to update customer with ID: {}", id);
        Optional<CustomerDto> updatedCustomer = customerService.update(id, customerDto);

        return updatedCustomer.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Customer with ID: {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // Both Admins and Users can view customer details
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get customer by ID", description = "Fetches a customer's details by their ID.")
    @GetMapping("/{id}")
    public ResponseEntity<CustomerDto> getCustomerById(
            @Parameter(description = "Customer ID", required = true) @PathVariable long id) {

        log.info("Request to fetch customer with ID: {}", id);
        Optional<CustomerDto> customer = customerService.findCustomerById(id);

        return customer.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Customer with ID: {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // Both Admins and Users can check if a customer exists by Aadhar number
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Check if customer exists", description = "Checks if a customer exists by Aadhar number.")
    @GetMapping("/exists")
    public ResponseEntity<String> checkCustomerExists(
            @Parameter(description = "Aadhar number", required = true) @RequestParam String aadharNumber) {

        log.info("Request to check if customer exists with Aadhar number: {}", aadharNumber);
        boolean exists = customerService.checkCustomerIsRegistered(aadharNumber);

        if (exists) {
            return ResponseEntity.ok("Customer exists with Aadhar number: " + aadharNumber);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Customer with Aadhar number " + aadharNumber + " does not exist.");
        }
    }
}
