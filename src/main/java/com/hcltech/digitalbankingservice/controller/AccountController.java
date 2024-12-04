package com.hcltech.digitalbankingservice.controller;

import com.hcltech.digitalbankingservice.dto.AccountDto;
import com.hcltech.digitalbankingservice.exception.AccountNotFoundException;
import com.hcltech.digitalbankingservice.service.AccountService;
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
@RequestMapping("/api/account")
@Tag(name = "Account Management", description = "Operations related to bank accounts management")
public class AccountController {

    @Autowired
    private AccountService accountService;

    // Both Admins and Users can fetch all accounts
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get all accounts", description = "Fetches a list of all bank accounts.")
    @GetMapping("/all")
    public ResponseEntity<List<AccountDto>> getAllAccounts() {
        log.info("Request to fetch all accounts");
        Optional<List<AccountDto>> result = accountService.getAllAccount();
        return result.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("No accounts found");
                    return ResponseEntity.notFound().build();
                });
    }

    // Both Admins and Users can fetch account by account number
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get account by account number", description = "Fetches the details of an account by account number.")
    @GetMapping("/{accountNo}")
    public ResponseEntity<AccountDto> getAccountByAccountNo(
            @Parameter(description = "Account Number", required = true) @PathVariable Long accountNo) {

        log.info("Request to fetch account with account number: {}", accountNo);
        Optional<AccountDto> result = accountService.getByAccountNumber(accountNo);
        return result.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Account with number {} not found", accountNo);
                    return ResponseEntity.notFound().build();
                });
    }

    // Both Admins and Users can fetch accounts by customer ID
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get accounts by customer ID", description = "Fetches all accounts linked to a specific customer by their customer ID.")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<AccountDto>> getAccountByCustomerId(
            @Parameter(description = "Customer ID", required = true) @PathVariable Long customerId) {

        log.info("Request to fetch accounts for customer ID: {}", customerId);
        Optional<List<AccountDto>> result = accountService.getByCustomerId(customerId);
        return result.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("No accounts found for customer ID: {}", customerId);
                    return ResponseEntity.notFound().build();
                });
    }

    // Only Admins can create a new account
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create a new account", description = "Creates a new account for a given customer.")
    @PostMapping("/{customerId}")
    public ResponseEntity<AccountDto> createAccount(
            @Parameter(description = "Customer ID", required = true) @PathVariable Long customerId,
            @Valid @RequestBody AccountDto accountDTO) {

        log.info("Request to create a new account for customer ID: {}", customerId);
        Optional<AccountDto> createdAccount = accountService.createAccount(customerId, accountDTO);
        return createdAccount.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Account creation failed for customer ID: {}", customerId);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
                });
    }

    // Only Admins can update account details
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update account details", description = "Updates the account details for the provided account ID.")
    @PutMapping("/{accountId}")
    public ResponseEntity<AccountDto> updateAccount(
            @Parameter(description = "Account ID", required = true) @PathVariable Long accountId,
            @RequestBody AccountDto accountDTO) {

        log.info("Request to update account with ID: {}", accountId);
        AccountDto updatedAccount = accountService.updateAccount(accountId, accountDTO);
        log.info("Successfully updated account with ID: {}", accountId);
        return ResponseEntity.ok(updatedAccount);
    }

    // Only Admins can delete an account
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete account by account number", description = "Deletes an account based on the provided account number.")
    @DeleteMapping("/{accountNo}")
    public ResponseEntity<String> deleteAccountByAccountNo(
            @Parameter(description = "Account Number", required = true) @PathVariable Long accountNo) {

        log.info("Request to delete account with account number: {}", accountNo);
        try {
            accountService.deleteAccountByAccountNo(accountNo);
            log.info("Successfully deleted account with account number: {}", accountNo);
            return ResponseEntity.ok("Account deleted successfully.");
        } catch (AccountNotFoundException e) {
            log.warn("Account with number {} not found: ", accountNo);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
