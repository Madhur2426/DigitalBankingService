package com.hcltech.digitalbankingservice.dao;

import com.hcltech.digitalbankingservice.model.Account;
import com.hcltech.digitalbankingservice.model.CreditCard;
import com.hcltech.digitalbankingservice.repository.AccountRepository;
import com.hcltech.digitalbankingservice.repository.CreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CreditCardDaoService {

    private final CreditCardRepository creditCardRepository;

    @Autowired
    public AccountRepository accountRepository;

    @Autowired
    public CreditCardDaoService(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    public List<CreditCard> getAll() {
        return creditCardRepository.findAll();
    }

    public Optional<CreditCard> getByNo(Long creditCardNumber) {
        return creditCardRepository.findById(creditCardNumber);
    }

    public CreditCard create(CreditCard creditCard) {
        return creditCardRepository.save(creditCard);
    }

    public CreditCard update(CreditCard creditCard) {
        return creditCardRepository.save(creditCard);
    }

    public boolean delete(Long creditCardNumber) {
        creditCardRepository.deleteById(creditCardNumber);
        return true;
    }

    public Optional<List<CreditCard>> getByAccountNumber(Long accountNumber) {
        return creditCardRepository.findCreditCardByAccountNumber(accountNumber);
    }
}
