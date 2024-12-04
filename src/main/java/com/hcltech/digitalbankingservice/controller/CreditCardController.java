package com.hcltech.digitalbankingservice.controller;

import com.hcltech.digitalbankingservice.dto.CreditCardDto;
import com.hcltech.digitalbankingservice.service.CreditCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Slf4j
@RestController
@RequestMapping("/api/creditCard")
@Tag(name = "Credit Card Management", description = "Operations related to credit card management")
public class CreditCardController {

    @Autowired
    private CreditCardService creditCardService;

    // Both Admins and Users can fetch all credit cards
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get all credit cards", description = "Fetches a list of all credit cards.")
    @GetMapping
    public ResponseEntity<List<CreditCardDto>> getAllCreditCards() {
        log.info("Request to fetch all credit cards");
        List<CreditCardDto> creditCards = creditCardService.getAll();
        log.info("Successfully fetched all credit cards");
        return ResponseEntity.ok(creditCards);
    }

    // Both Admins and Users can fetch credit card by card number
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get credit card by card number", description = "Fetches the credit card details for the given credit card number.")
    @GetMapping("/{creditCardNumber}")
    public ResponseEntity<CreditCardDto> getCreditCardByCreditCardNo(
            @Parameter(description = "Credit Card Number", required = true) @PathVariable Long creditCardNumber) {

        log.info("Request to fetch credit card with number: {}", creditCardNumber);
        Optional<CreditCardDto> result = creditCardService.getByCreditCardNumber(creditCardNumber);
        return result.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Credit card with number {} not found", creditCardNumber);
                    return ResponseEntity.notFound().build();
                });
    }

    // Both Admins and Users can fetch credit card by account number
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Get credit card by account number", description = "Fetches the credit card details associated with the given account number.")
    @GetMapping("/account/{accountNumber}")
    public ResponseEntity<CreditCardDto> getCreditCardByAccountNo(
            @Parameter(description = "Account Number", required = true) @PathVariable Long accountNumber) {

        log.info("Request to fetch credit card for account number: {}", accountNumber);
        Optional<CreditCardDto> result = creditCardService.getCreditCardByAccountNumber(accountNumber);
        return result.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Credit card for account number {} not found", accountNumber);
                    return ResponseEntity.notFound().build();
                });
    }

    // Only Admins can update credit card PIN
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update credit card PIN", description = "Updates the PIN of a credit card for the provided card number.")
    @PutMapping("/{creditCardNumber}/{oldPin}/{newPin}")
    public ResponseEntity<CreditCardDto> updateCreditCardPin(
            @Parameter(description = "Credit Card Number", required = true) @PathVariable Long creditCardNumber,
            @Parameter(description = "Old PIN", required = true) @PathVariable String oldPin,
            @Parameter(description = "New PIN", required = true) @PathVariable String newPin) {

        log.info("Request to update PIN for credit card number: {}", creditCardNumber);
        Optional<CreditCardDto> result = creditCardService.update(creditCardNumber, oldPin, newPin);
        return result.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Credit card with number {} not found", creditCardNumber);
                    return ResponseEntity.notFound().build();
                });
    }

    // Only Admins can block or unblock a credit card
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Block or unblock credit card", description = "Blocks or unblocks a credit card based on the provided status.")
    @PutMapping("/{creditCardNumber}/{pin}/status/{blocked}")
    public ResponseEntity<CreditCardDto> updateCreditCardStatus(
            @Parameter(description = "Credit Card Number", required = true) @PathVariable long creditCardNumber,
            @Parameter(description = "PIN", required = true) @PathVariable String pin,
            @Parameter(description = "Blocked status", required = true) @PathVariable boolean blocked) {

        log.info("Request to update status for credit card number: {}. Blocked: {}", creditCardNumber, blocked);
        Optional<CreditCardDto> result = creditCardService.updateStatus(creditCardNumber, pin, blocked);
        return result.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Credit card with number {} not found", creditCardNumber);
                    return ResponseEntity.notFound().build();
                });
    }

    // Only Admins can delete a credit card
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete credit card", description = "Deletes a credit card for the given credit card number.")
    @DeleteMapping("/{creditCardNumber}")
    public ResponseEntity<String> deleteCreditCard(
            @Parameter(description = "Credit Card Number", required = true) @PathVariable Long creditCardNumber) {

        log.info("Request to delete credit card with number: {}", creditCardNumber);
        boolean status = creditCardService.delete(creditCardNumber);
        if (status) {
            log.info("Successfully deleted credit card with number: {}", creditCardNumber);
            return ResponseEntity.ok("Credit Card Deleted Successfully.");
        } else {
            log.warn("Credit card with number {} not found", creditCardNumber);
            return ResponseEntity.notFound().build();
        }
    }

    // Only Admins can delete credit card by account number
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete credit card by account number", description = "Deletes a credit card associated with the provided account number.")
    @DeleteMapping("/account/{accountNumber}")
    public ResponseEntity<String> deleteCreditCardByAccountNo(
            @Parameter(description = "Account Number", required = true) @PathVariable Long accountNumber) {

        log.info("Request to delete credit card for account number: {}", accountNumber);
        boolean result = creditCardService.deleteCreditCardByAccountNumber(accountNumber);
        if (result) {
            log.info("Successfully deleted credit card for account number: {}", accountNumber);
            return ResponseEntity.ok("Credit Card Deleted Successfully.");
        } else {
            log.warn("Credit card for account number {} not found", accountNumber);
            return ResponseEntity.notFound().build();
        }
    }

    // Only Admins can create a new credit card
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create credit card", description = "Creates a new credit card associated with the provided account number.")
    @PostMapping("/{accountNumber}")
    public ResponseEntity<CreditCardDto> createCreditCard(
            @Parameter(description = "Account Number", required = true) @PathVariable Long accountNumber,
            @RequestBody CreditCardDto creditCardDto) {

        log.info("Request to create credit card for account number: {}", accountNumber);
        Optional<CreditCardDto> result = creditCardService.createCreditCard(accountNumber,creditCardDto);
        return result.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Failed to create credit card for account number: {}", accountNumber);
                    return ResponseEntity.notFound().build();
                });
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Transfer funds from a Credit card to an account", description = "Transfers funds from credit card to the specified account.")
    @PostMapping("/transfer")
    public ResponseEntity<String> transferFunds(
            @Parameter(description = "From Credit Card Number", required = true) @RequestParam Long creditCardNumber,
            @Parameter(description = "To Account Number", required = true) @RequestParam Long toAccountNumber,
            @Parameter(description = "Amount to Transfer", required = true) @RequestParam Double amount,
            @Parameter(description = "PIN", required = true) @RequestParam String pin) {

        try {
            creditCardService.transferFunds(creditCardNumber, toAccountNumber, amount, pin);
            return ResponseEntity.ok("Transfer successful.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Transfer funds from Account to Credit Card", description = "Transfers funds from account to the specified credit card.")
    @PostMapping("/account/transfer")
    public ResponseEntity<String> transferFundsToAccount(
            @Parameter(description = "From Credit Card Number", required = true) @RequestParam Long creditCardNumber,
            @Parameter(description = "To Account Number", required = true) @RequestParam Long accountNumber,
            @Parameter(description = "Amount to Transfer", required = true) @RequestParam Double amount,
            @Parameter(description = "PIN", required = true) @RequestParam int pin) {

        try {
            creditCardService.transferFundsToAccount(creditCardNumber, accountNumber, amount, pin);
            return ResponseEntity.ok("Transfer successful.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
