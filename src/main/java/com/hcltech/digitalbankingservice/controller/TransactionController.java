package com.hcltech.digitalbankingservice.controller;

import com.hcltech.digitalbankingservice.dto.FundTransferRequestDto;
import com.hcltech.digitalbankingservice.dto.TransactionDto;
import com.hcltech.digitalbankingservice.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/transaction")
@Tag(name = "Transaction Management", description = "Operations related to transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Add Money to an Account", description = "Adds the specified amount to the account balance for the given account number.")
    @PostMapping("/addMoney/{accountNo}")
    public ResponseEntity<String> addMoneyByAccountNo(
            @Parameter(description = "Account Number", required = true) @PathVariable Long accountNo,
            @Parameter(description = "Amount to Add", required = true) @RequestParam Double amount) {

        log.info("Request to add {} to account number {}", amount, accountNo);
        transactionService.addMoneyByAccountNo(accountNo, amount);
        log.info("Successfully added {} to account number {}", amount, accountNo);

        return ResponseEntity.ok("Money added successfully to account: " + accountNo);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Withdraw Money from an Account", description = "Withdraws the specified amount from the account balance for the given account number.")
    @PostMapping("/withdraw/{accountNo}")
    public ResponseEntity<String> withdrawAmountByAccountNo(
            @Parameter(description = "Account Number", required = true) @PathVariable Long accountNo,
            @Parameter(description = "Amount to Withdraw", required = true) @RequestParam Double amount) {

        log.info("Request to withdraw {} from account number {}", amount, accountNo);
        transactionService.withdrawAmountByAccountNo(accountNo, amount);
        log.info("Successfully withdrew {} from account number {}", amount, accountNo);

        return ResponseEntity.ok("Amount withdrawn successfully from account: " + accountNo);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get Transaction by ID", description = "Fetches a transaction by its unique ID.")
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDto> getTransactionById(
            @Parameter(description = "Transaction ID", required = true) @PathVariable Integer transactionId) {

        log.info("Request to fetch transaction with ID: {}", transactionId);
        TransactionDto transactionDto = transactionService.getTransactionById(transactionId);
        log.info("Transaction with ID: {} fetched successfully", transactionId);

        return ResponseEntity.ok(transactionDto);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get All Transactions", description = "Fetches a list of all transactions.")
    @GetMapping("/all")
    public ResponseEntity<List<TransactionDto>> getAllTransactions() {

        log.info("Request to fetch all transactions");
        List<TransactionDto> transactions = transactionService.getAllTransactions();
        log.info("Successfully fetched all transactions");

        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get Transactions by Account Number", description = "Fetches all transactions associated with a given account number.")
    @GetMapping("/account/{accountNo}")
    public ResponseEntity<List<TransactionDto>> getAllTransactionsByAccountNo(
            @Parameter(description = "Account Number", required = true) @PathVariable Long accountNo) {

        log.info("Request to fetch all transactions for account number: {}", accountNo);
        List<TransactionDto> transactions = transactionService.getAllTransactionsByAccountNo(accountNo);
        log.info("Successfully fetched transactions for account number: {}", accountNo);

        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get Transactions by Credit Card", description = "Fetches all transactions made with a specific credit card.")
    @GetMapping("/creditCard/{creditCardNo}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByCreditCard(
            @Parameter(description = "Credit Card Number", required = true) @PathVariable Long creditCardNo) {

        log.info("Request to fetch all transactions for credit card number: {}", creditCardNo);
        List<TransactionDto> transactions = transactionService.getTransactionsByCreditCard(creditCardNo);
        log.info("Successfully fetched transactions for credit card number: {}", creditCardNo);

        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Get Transactions by Debit Card", description = "Fetches all transactions made with a specific debit card.")
    @GetMapping("/debitCard/{debitCardNo}")
    public ResponseEntity<List<TransactionDto>> getTransactionsByDebitCard(
            @Parameter(description = "Debit Card Number", required = true) @PathVariable Long debitCardNo) {

        log.info("Request to fetch all transactions for debit card number: {}", debitCardNo);
        List<TransactionDto> transactions = transactionService.getTransactionsByDebitCard(debitCardNo);
        log.info("Successfully fetched transactions for debit card number: {}", debitCardNo);

        return ResponseEntity.ok(transactions);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "Fund Transfer", description = "Transfers the specified amount from the source account to the target account.")
    @PostMapping("/fund-transfer")
    public ResponseEntity<String> fundTransfer(
            @RequestBody FundTransferRequestDto fundTransferRequest) {

        log.info("Initiating fund transfer of {} from account number {} to account number {}",
                fundTransferRequest.getAmount(),
                fundTransferRequest.getSourceAccountNumber(),
                fundTransferRequest.getTargetAccountNumber());

        transactionService.fundTransfer(fundTransferRequest);

        log.info("Successfully transferred {} from account number {} to account number {}",
                fundTransferRequest.getAmount(),
                fundTransferRequest.getSourceAccountNumber(),
                fundTransferRequest.getTargetAccountNumber());

        return ResponseEntity.ok("Fund transfer successful from account: " +
                fundTransferRequest.getSourceAccountNumber() + " to account: " +
                fundTransferRequest.getTargetAccountNumber());
    }
}
