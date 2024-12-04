package com.hcltech.digitalbankingservice.service;

import com.hcltech.digitalbankingservice.dao.TransactionDaoService;
import com.hcltech.digitalbankingservice.dto.FundTransferRequestDto;
import com.hcltech.digitalbankingservice.dto.TransactionDto;
import com.hcltech.digitalbankingservice.model.Account;
import com.hcltech.digitalbankingservice.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionDaoService transactionDaoService;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }


    @Test
    void testWithdrawAmountByAccountNo_InsufficientFunds() {
        Account account = new Account();
        account.setAccountBalance(50.00);
        when(transactionDaoService.findAccountByAccountNo(12345L)).thenReturn(account);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.withdrawAmountByAccountNo(12345L, 100.00);
        });

        String expectedMessage = "Insufficient funds in account.";
        String actualMessage = exception.getMessage();
        assert (expectedMessage.equals(actualMessage));
    }

    @Test
    void testGetTransactionById() {
        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(100.00);
        when(transactionDaoService.findTransactionById(1)).thenReturn(transaction);
        when(modelMapper.map(transaction, TransactionDto.class)).thenReturn(new TransactionDto());

        TransactionDto result = transactionService.getTransactionById(1);

        verify(transactionDaoService, times(1)).findTransactionById(1);
        verify(modelMapper, times(1)).map(transaction, TransactionDto.class);
    }

    @Test
    void testGetAllTransactions() {
        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(100.00);
        when(transactionDaoService.findAllTransactions()).thenReturn(Collections.singletonList(transaction));
        when(modelMapper.map(transaction, TransactionDto.class)).thenReturn(new TransactionDto());

        List<TransactionDto> result = transactionService.getAllTransactions();

        verify(transactionDaoService, times(1)).findAllTransactions();
        verify(modelMapper, times(1)).map(transaction, TransactionDto.class);
    }

}
