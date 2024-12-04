package com.hcltech.digitalbankingservice.service;

import com.hcltech.digitalbankingservice.dao.TransactionDaoService;
import com.hcltech.digitalbankingservice.dto.FundTransferRequestDto;
import com.hcltech.digitalbankingservice.dto.TransactionDto;
import com.hcltech.digitalbankingservice.model.Account;
import com.hcltech.digitalbankingservice.model.Transaction;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionDaoService transactionDaoService;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private EmailService emailService;

    public void addMoneyByAccountNo(Long accountNo, Double amount) {
        Account account = transactionDaoService.findAccountByAccountNo(accountNo);
        account.setAccountBalance(account.getAccountBalance() + amount);
        transactionDaoService.saveAccount(account);

        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(amount);
        transaction.setTransactionDatetime(LocalDateTime.now());
        transaction.setTransactionType("CREDIT");
        transaction.setAccount(account);
        String maskAccountNumber = maskAccountNumber(accountNo);
        transactionDaoService.saveTransaction(transaction);
        emailService.sendTransactionEmail(account.getCustomer().getEmail(), "credited", amount, maskAccountNumber);
    }

    public void withdrawAmountByAccountNo(Long accountNo, Double amount) {
        Account account = transactionDaoService.findAccountByAccountNo(accountNo);

        if (account.getAccountBalance() < amount) {
            throw new IllegalArgumentException("Insufficient funds in account.");
        }

        account.setAccountBalance(account.getAccountBalance() - amount);
        transactionDaoService.saveAccount(account);

        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(amount);
        transaction.setTransactionDatetime(LocalDateTime.now());
        transaction.setTransactionType("DEBIT");
        transaction.setAccount(account);
        String maskAccountNumber = maskAccountNumber(accountNo);
        transactionDaoService.saveTransaction(transaction);
        emailService.sendTransactionEmail(account.getCustomer().getEmail(), "debited", amount, maskAccountNumber);


    }


    public TransactionDto getTransactionById(Integer transactionId) {
        Transaction transaction = transactionDaoService.findTransactionById(transactionId);
        return modelMapper.map(transaction, TransactionDto.class);
    }

    public List<TransactionDto> getAllTransactions() {
        return transactionDaoService.findAllTransactions().stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDto.class))
                .collect(Collectors.toList());
    }

    public List<TransactionDto> getAllTransactionsByAccountNo(Long accountNo) {
        return transactionDaoService.findByAccountNumber(accountNo).stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDto.class))
                .collect(Collectors.toList());
    }

    public List<TransactionDto> getTransactionsByCreditCard(Long creditCardNumber) {
        return transactionDaoService.findByCreditCardNumber(creditCardNumber).stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDto.class))
                .collect(Collectors.toList());
    }

    public List<TransactionDto> getTransactionsByDebitCard(Long debitCardNumber) {
        return transactionDaoService.findByDebitCardNumber(debitCardNumber).stream()
                .map(transaction -> modelMapper.map(transaction, TransactionDto.class))
                .collect(Collectors.toList());
    }

    public void fundTransfer(FundTransferRequestDto fundTransferRequestDto) {

        Account fromAccount = transactionDaoService.findAccountByAccountNo(fundTransferRequestDto.getSourceAccountNumber());

        if (fromAccount == null) {
            throw new IllegalArgumentException("Source account number is invalid.");
        }

        Account toAccount = transactionDaoService.findAccountByAccountNo(fundTransferRequestDto.getTargetAccountNumber());
        if (toAccount == null) {
            throw new IllegalArgumentException("Target account number is invalid.");
        }

        if (!fromAccount.getPin().equals(fundTransferRequestDto.getPin())) {
            throw new IllegalArgumentException("Invalid PIN.");
        }

        if (fromAccount.getAccountBalance() < fundTransferRequestDto.getAmount()) {
            throw new IllegalArgumentException("Insufficient funds in source account.");
        }

        fromAccount.setAccountBalance(fromAccount.getAccountBalance() - fundTransferRequestDto.getAmount());
        toAccount.setAccountBalance(toAccount.getAccountBalance() + fundTransferRequestDto.getAmount());

        transactionDaoService.saveAccount(fromAccount);
        transactionDaoService.saveAccount(toAccount);

        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(fundTransferRequestDto.getAmount());
        transaction.setTransactionDatetime(LocalDateTime.now());
        transaction.setTransactionType("Fund Transfer");
        transaction.setAccount(fromAccount);
        transactionDaoService.saveTransaction(transaction);

        String maskedFromAccount = maskAccountNumber(fromAccount.getAccountNumber());
        String maskedToAccount = maskAccountNumber(toAccount.getAccountNumber());

        emailService.sendTransactionEmail(fromAccount.getCustomer().getEmail(), "debited", fundTransferRequestDto.getAmount(), maskedFromAccount);
        emailService.sendTransactionEmail(toAccount.getCustomer().getEmail(), "credited", fundTransferRequestDto.getAmount(), maskedToAccount);
    }

    private String maskAccountNumber(Long accountNumber) {
        String accString = String.valueOf(accountNumber);
        return accString.substring(accString.length() - 4);
    }

}
