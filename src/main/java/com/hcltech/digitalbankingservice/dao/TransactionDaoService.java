package com.hcltech.digitalbankingservice.dao;

import com.hcltech.digitalbankingservice.exception.AccountNotFoundException;
import com.hcltech.digitalbankingservice.exception.TransactionNotFoundException;
import com.hcltech.digitalbankingservice.model.Account;
import com.hcltech.digitalbankingservice.model.Transaction;
import com.hcltech.digitalbankingservice.repository.AccountRepository;
import com.hcltech.digitalbankingservice.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionDaoService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    public Account findAccountByAccountNo(Long accountNo) {
        return accountRepository.findById(accountNo)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with account number: " + accountNo));
    }

    public void saveAccount(Account account) {
        accountRepository.save(account);
    }

    public void saveTransaction(Transaction transaction) {
        transactionRepository.save(transaction);
    }

    public Transaction findTransactionById(Integer transactionId) {
        return transactionRepository.findById(transactionId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found: " + transactionId));
    }

    public List<Transaction> findAllTransactions() {
        return transactionRepository.findAll();
    }

    public List<Transaction> findByAccountNumber(Long accountNo) {
        return transactionRepository.findByAccountNumber(accountNo);
    }

    public List<Transaction> findByCreditCardNumber(Long creditCardNumber) {
        return transactionRepository.findByCreditCardNumber(creditCardNumber);
    }

    public List<Transaction> findByDebitCardNumber(Long debitCardNumber) {
        return transactionRepository.findByDebitCardNumber(debitCardNumber);
    }
}
