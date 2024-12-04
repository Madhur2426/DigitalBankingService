package com.hcltech.digitalbankingservice.dao;

import com.hcltech.digitalbankingservice.model.Transaction;
import com.hcltech.digitalbankingservice.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.Mockito.*;

class TransactionDaoServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionDaoService transactionDaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindTransactionById() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(1);

        when(transactionRepository.findById(1)).thenReturn(Optional.of(transaction));

        Transaction result = transactionDaoService.findTransactionById(1);

        assert (result.getTransactionId() == 1);
        verify(transactionRepository, times(1)).findById(1);
    }

    @Test
    void testFindAllTransactions() {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(1);

        when(transactionRepository.findAll()).thenReturn(Collections.singletonList(transaction));

        var result = transactionDaoService.findAllTransactions();

        assert (result.size() == 1);
        verify(transactionRepository, times(1)).findAll();
    }

    @Test
    void testSaveTransaction() {
        Transaction transaction = new Transaction();

        transactionDaoService.saveTransaction(transaction);

        verify(transactionRepository, times(1)).save(transaction);
    }
}
