package com.hcltech.digitalbankingservice.service;

import com.hcltech.digitalbankingservice.dao.AccountDaoService;
import com.hcltech.digitalbankingservice.dto.AccountDto;
import com.hcltech.digitalbankingservice.exception.AccountAlreadyExistsException;
import com.hcltech.digitalbankingservice.exception.AccountNotFoundException;
import com.hcltech.digitalbankingservice.model.Account;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AccountService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    AccountDaoService accountDaoService;

    public Optional<AccountDto> createAccount(Long customerId, AccountDto accountDto) {
        Optional<List<Account>> existingAccounts = accountDaoService.getByCustomerId(customerId);

        if (existingAccounts.isPresent() && existingAccounts.get().stream()
                .anyMatch(account -> String.valueOf(account.getAccountType()).equals(String.valueOf(accountDto.getAccountType())))) {
            throw new AccountAlreadyExistsException("An account of type " + accountDto.getAccountType() + " already exists for the customer.");
        }
        Account account = toEntity(accountDto);
        return Optional.of(toDto(accountDaoService.create(customerId, account).get()));
    }

    public Optional<AccountDto> getByAccountNumber(Long accountNo) {
        Optional<Account> result = accountDaoService.getByAccountNo(accountNo);
        if (result.isPresent()) {
            AccountDto res = toDto(result.get());
            return Optional.of(res);
        }
        throw new AccountNotFoundException("Account with account number " + accountNo + " not found.");
    }

    public Optional<List<AccountDto>> getByCustomerId(Long customerId) {
        Optional<List<Account>> results = accountDaoService.getByCustomerId(customerId);
        if (results.isPresent()) {
            List<AccountDto> accountList = results.get().stream()
                    .map(result -> modelMapper.map(result, AccountDto.class))
                    .collect(Collectors.toList());
            return Optional.of(accountList);
        }
        return Optional.empty();
    }

    public Optional<List<AccountDto>> getAllAccount() {
        Optional<List<Account>> results = accountDaoService.getAllAccount();
        if (results.isPresent()) {
            List<AccountDto> accountList = results.get().stream()
                    .map(result -> modelMapper.map(result, AccountDto.class))
                    .collect(Collectors.toList());
            return Optional.of(accountList);
        }
        return Optional.empty();
    }

    public AccountDto updateAccount(Long accountId, AccountDto accountDTO) {
        Optional<Account> existingAccount = accountDaoService.getByAccountNo(accountId);
        if (existingAccount.isEmpty()) {
            throw new AccountNotFoundException("Account with account ID " + accountId + " not found.");
        }
        Account account = toEntity(accountDTO);
        return toDto(accountDaoService.update(accountId, account));
    }

    public void deleteAccountByAccountNo(Long accountNo) {
        Optional<Account> existingAccount = accountDaoService.getByAccountNo(accountNo);
        if (existingAccount.isEmpty()) {
            throw new AccountNotFoundException("Account with account number " + accountNo + " not found.");
        }
        accountDaoService.delete(accountNo);
    }

    private Account toEntity(AccountDto accountDto) {
        return modelMapper.map(accountDto, Account.class);
    }

    private AccountDto toDto(Account account) {
        return modelMapper.map(account, AccountDto.class);
    }
}