package com.hcltech.digitalbankingservice.controller;

import com.hcltech.digitalbankingservice.dto.DebitCardDto;
import com.hcltech.digitalbankingservice.service.DebitCardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api/debitCard")
@Tag(name = "Debit Card Management", description = "Operations related to Debit Cards")
public class DebitCardController {

    @Autowired
    private DebitCardService debitCardService;

    @Operation(summary = "Get Debit Card by Account Number",
            description = "Fetches the debit card associated with the given account number.")
    @GetMapping("/account/{accountNumber}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DebitCardDto> getDebitCardByAccountNo(
            @Parameter(description = "Account Number", required = true) @PathVariable Long accountNumber) {

        log.info("Request to fetch debit card for account number: {}", accountNumber);
        Optional<DebitCardDto> result = debitCardService.getDebitCardByAccountNumber(accountNumber);

        return result.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Debit card for account number {} not found", accountNumber);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "Get Debit Card by Card Number",
            description = "Fetches the debit card details by its card number.")
    @GetMapping("/{debitCardNumber}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<DebitCardDto> getDebitCardByCardNumber(
            @Parameter(description = "Debit Card Number", required = true) @PathVariable Long debitCardNumber) {

        log.info("Request to fetch debit card for card number: {}", debitCardNumber);
        Optional<DebitCardDto> debitCardDto = debitCardService.getDebitCardByDebitCardNumber(debitCardNumber);

        return debitCardDto.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Debit card not found for card number: {}", debitCardNumber);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
                });
    }

    @Operation(summary = "Create Debit Card",
            description = "Creates a new debit card for the specified account number.")
    @PostMapping("/{accountNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DebitCardDto> createDebitCard(
            @Parameter(description = "Account Number", required = true) @PathVariable Long accountNumber,
            @RequestBody DebitCardDto debitCardDto) {

        log.info("Request to create a debit card for account number: {}", accountNumber);
        Optional<DebitCardDto> result = debitCardService.createDebitCard(accountNumber, debitCardDto);

        return result.map(ResponseEntity::ok)
                .orElseGet(() -> {
                    log.warn("Failed to create debit card for account number: {}", accountNumber);
                    return ResponseEntity.notFound().build();
                });
    }

    @Operation(summary = "Update Debit Card PIN",
            description = "Updates the PIN for the specified debit card.")
    @PutMapping("/{debitCardNumber}/update-pin")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> updateDebitCardPin(
            @Parameter(description = "Debit Card Number", required = true) @PathVariable Long debitCardNumber,
            @Parameter(description = "Old PIN", required = true) @RequestParam String oldPin,
            @Parameter(description = "New PIN", required = true) @RequestParam String newPin) {
        // Log method entry
        log.info("Updating PIN for debit card number: {}", debitCardNumber);

        Optional<DebitCardDto> updatedCard = debitCardService.updateDebitCard(debitCardNumber, oldPin, newPin);
        if (updatedCard.isPresent()) {
            log.info("Debit card PIN updated successfully for card number: {}", debitCardNumber);
            return ResponseEntity.ok("Debit card PIN updated successfully.");
        } else {
            log.warn("Debit card not found for card number: {}", debitCardNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Debit card not found.");
        }
    }


    @Operation(summary = "Update Debit Card Status",
            description = "Updates the blocked status of a debit card based on the provided PIN.")
    @PutMapping("/{debitCardNumber}/update-status")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> updateDebitCardStatus(
            @Parameter(description = "Debit Card Number", required = true) @PathVariable Long debitCardNumber,
            @Parameter(description = "PIN", required = true) @RequestParam String pin,
            @Parameter(description = "Blocked Status", required = true) @RequestParam boolean blocked) {
        log.info("Updating status for debit card number: {}, Blocked: {}", debitCardNumber, blocked);

        Optional<DebitCardDto> updatedCard = debitCardService.updateStatus(debitCardNumber, pin, blocked);
        if (updatedCard.isPresent()) {
            log.info("Debit card status updated successfully for card number: {}", debitCardNumber);
            return ResponseEntity.ok("Debit card status updated successfully.");
        } else {
            log.warn("Debit card not found for card number: {}", debitCardNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Debit card not found.");
        }
    }

    @Operation(summary = "Delete Debit Card by Account Number",
            description = "Deletes the debit card associated with the given account number.")
    @DeleteMapping("/deleteByAccountNumber/{accountNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteDebitCardByAccountNumber(
            @Parameter(description = "Account Number", required = true) @PathVariable Long accountNumber) {

        log.info("Request to delete debit card for account number: {}", accountNumber);
        boolean deleted = debitCardService.deleteDebitCardByAccountNumber(accountNumber);

        if (deleted) {
            log.info("Successfully deleted debit card for account number: {}", accountNumber);
            return ResponseEntity.ok("Debit card has been deleted.");
        } else {
            log.warn("Debit card for account number {} not found", accountNumber);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Debit card not found.");
        }
    }

    @Operation(summary = "Delete Debit Card by Card Number",
            description = "Deletes the debit card by its card number.")
    @DeleteMapping("/deleteByDebitCard/{debitCardNumber}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteDebitCardByCardNumber(
            @Parameter(description = "Debit Card Number", required = true) @PathVariable Long debitCardNumber) {

        log.info("Request to delete debit card with card number: {}", debitCardNumber);
        debitCardService.deleteDebitCardByCardNumber(debitCardNumber);
        log.info("Successfully deleted debit card with card number: {}", debitCardNumber);

        return ResponseEntity.ok("Debit card has been deleted.");
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Transfer funds from a debit card to an account", description = "Transfers funds from one debit card to the specified account.")
    @PostMapping("/transfer")
    public ResponseEntity<String> transferFunds(
            @Parameter(description = "From Debit Card Number", required = true) @RequestParam Long fromDebitCardNumber,
            @Parameter(description = "To Account Number", required = true) @RequestParam Long toAccountNumber,
            @Parameter(description = "Amount to Transfer", required = true) @RequestParam Double amount,
            @Parameter(description = "PIN", required = true) @RequestParam String pin) {

        try {
            debitCardService.transferFunds(fromDebitCardNumber, toAccountNumber, amount, pin);
            return ResponseEntity.ok("Transfer successful.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}