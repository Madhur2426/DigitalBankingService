package com.hcltech.digitalbankingservice.dao;

import com.hcltech.digitalbankingservice.model.Account;
import com.hcltech.digitalbankingservice.model.Customer;
import com.hcltech.digitalbankingservice.repository.AccountRepository;
import com.hcltech.digitalbankingservice.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AccountDaoServiceTest {

    @InjectMocks
    private AccountDaoService accountDaoService;
    @Mock
    private AccountRepository accountRepository;
    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testGetByAccountNo_NotFound() {
        Long accountNo = 12345L;

        when(accountRepository.findAccountsByCustomerId(accountNo)).thenReturn(Optional.empty());

        Optional<Account> result = accountDaoService.getByAccountNo(accountNo);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetAllAccount_Success() {
        Account account = new Account();
        account.setAccountNumber(12345L); // Set additional fields as necessary

        when(accountRepository.findAll()).thenReturn(Collections.singletonList(account));

        Optional<List<Account>> result = accountDaoService.getAllAccount();

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
        assertEquals(12345L, result.get().get(0).getAccountNumber());
    }

    @Test
    void testUpdate_Success() {
        Long accountId = 1L;
        Account account = new Account();
        account.setAccountNumber(accountId); // Set necessary fields

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountRepository.save(any(Account.class))).thenReturn(account);

        Account updatedAccount = accountDaoService.update(accountId, account);

        assertEquals(accountId, updatedAccount.getAccountNumber());
        verify(accountRepository, times(1)).save(any(Account.class));
    }
}
