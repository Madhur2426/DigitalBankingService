package com.hcltech.digitalbankingservice.service;

import com.hcltech.digitalbankingservice.dao.AccountDaoService;
import com.hcltech.digitalbankingservice.dto.AccountDto;
import com.hcltech.digitalbankingservice.model.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AccountServiceTest {

    private AccountService accountService;
    private AccountDaoService accountDaoService;
    private ModelMapper modelMapper;

    @BeforeEach
    public void setup() {
        accountDaoService = Mockito.mock(AccountDaoService.class);
        modelMapper = new ModelMapper();
        accountService = new AccountService();
        accountService.accountDaoService = accountDaoService;
        accountService.modelMapper = modelMapper;
    }

    @Test
    void testCreateAccount_Success() {
        Long customerId = 1L;
        AccountDto accountDto = new AccountDto();
        Account account = new Account();
        account.setAccountNumber(12345L); // set necessary fields

        when(accountDaoService.create(any(Long.class), any(Account.class))).thenReturn(Optional.of(account));

        Optional<AccountDto> createdAccount = accountService.createAccount(customerId, accountDto);

        assertTrue(createdAccount.isPresent());
        assertEquals(12345L, createdAccount.get().getAccountNumber());
        verify(accountDaoService, times(1)).create(any(Long.class), any(Account.class));
    }

    @Test
    void testGetByAccountNumber_Success() {
        Long accountNo = 12345L;
        Account account = new Account();
        account.setAccountNumber(accountNo);

        when(accountDaoService.getByAccountNo(accountNo)).thenReturn(Optional.of(account));

        Optional<AccountDto> result = accountService.getByAccountNumber(accountNo);

        assertTrue(result.isPresent());
        assertEquals(accountNo, result.get().getAccountNumber());
    }

    @Test
    void testGetAllAccount_Success() {
        Account account = new Account();
        account.setAccountNumber(12345L); // set necessary fields

        when(accountDaoService.getAllAccount()).thenReturn(Optional.of(Collections.singletonList(account)));

        Optional<List<AccountDto>> result = accountService.getAllAccount();

        assertTrue(result.isPresent());
        assertEquals(1, result.get().size());
    }

}
