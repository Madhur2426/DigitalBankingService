package com.hcltech.digitalbankingservice.dao;

import com.hcltech.digitalbankingservice.exception.AccountNotFoundException;
import com.hcltech.digitalbankingservice.exception.CustomerNotFoundException;
import com.hcltech.digitalbankingservice.model.Account;
import com.hcltech.digitalbankingservice.model.Customer;
import com.hcltech.digitalbankingservice.repository.AccountRepository;
import com.hcltech.digitalbankingservice.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AccountDaoService {
    private static final Random random = new Random();

    @Autowired
    AccountRepository accountRepository;

    @Autowired
    CustomerRepository customerRepository;


    public Optional<Account> create(Long customerId, Account account) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new  IllegalArgumentException("Customer not Found"));
        account.setAccountNumber(generateAccountNumber());
        account.setCustomer(customer);
        return Optional.of(accountRepository.save(account));
    }

    public Optional<Account> getByAccountNo(Long accountNo) {
        return accountRepository.findById(accountNo);
    }

    public Optional<List<Account>> getByCustomerId(Long customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException("Customer not found");
        }
        return accountRepository.findAccountsByCustomerId(customerId);
    }

    public Optional<List<Account>> getAllAccount() {
        return Optional.of(accountRepository.findAll());
    }

    public void delete(Long accountNo) {
        if (!accountRepository.existsById(accountNo)) {
            throw new AccountNotFoundException("Accounts not found");
        }
        accountRepository.deleteById(accountNo);
    }

    public Account update(Long accountId, Account account) {
        Account existingAccount = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found"));
        existingAccount.setAccountNumber(accountId);
        existingAccount.setAccountType(existingAccount.getAccountType());
        existingAccount.setPin(account.getPin());
        existingAccount.setAccountBalance(account.getAccountBalance());
        return accountRepository.save(existingAccount);
    }

    public static Long generateAccountNumber() {
        return Math.abs(random.nextLong() % 100000000000L);
    }

    public Account updateBalance(Account account) {
        Account existingAccount = accountRepository.findById(account.getAccountNumber()).
                orElseThrow(() -> new AccountNotFoundException("Account Not found"));
        existingAccount.setAccountBalance(account.getAccountBalance());
        return accountRepository.save(existingAccount);
    }
}

