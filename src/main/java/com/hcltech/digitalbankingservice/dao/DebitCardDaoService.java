package com.hcltech.digitalbankingservice.dao;

import com.hcltech.digitalbankingservice.model.DebitCard;
import com.hcltech.digitalbankingservice.model.Transaction;
import com.hcltech.digitalbankingservice.repository.AccountRepository;
import com.hcltech.digitalbankingservice.repository.DebitCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DebitCardDaoService {

    @Autowired
    private DebitCardRepository debitCardRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionDaoService transactionDaoService;

    public Optional<DebitCard> findByDebitCardNumber(Long debitCardNumber) {
        return debitCardRepository.findById(debitCardNumber);
    }

    public Optional<List<DebitCard>> getByAccountNumber(Long accountNumber) {
        if (!accountRepository.existsById(accountNumber)) {
            throw new IllegalArgumentException("Account not found");
        }
        return debitCardRepository.findDebitCardByAccountNumber(accountNumber);
    }

    public List<DebitCard> getAll() {
        return debitCardRepository.findAll();
    }

    public DebitCard save(DebitCard debitCard) {
        return debitCardRepository.save(debitCard);
    }

    public DebitCard update(DebitCard debitCard) {
        return debitCardRepository.save(debitCard);
    }

    public void deleteByDebitCardNumber(Long debitCardNumber) {
        debitCardRepository.deleteById(debitCardNumber);
    }

    public boolean delete(Long debitCardNumber) {
        debitCardRepository.deleteById(debitCardNumber);
        return true;
    }

    public void addTransaction(Long debitCardNumber, Transaction transaction) {
        DebitCard debitCard = findByDebitCardNumber(debitCardNumber)
                .orElseThrow(() -> new RuntimeException("Debit Card not found"));
        transaction.setDebitCard(debitCard);
        transactionDaoService.saveTransaction(transaction);
        debitCard.addTransaction(transaction);
        save(debitCard);
    }

    public Optional<DebitCard> findAllByAccountNumber(Long accountNumber) {
        return debitCardRepository.findByAccount_AccountNumber(accountNumber);
    }
}
