package com.hcltech.digitalbankingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hcltech.digitalbankingservice.dto.FundTransferRequestDto;
import com.hcltech.digitalbankingservice.dto.TransactionDto;
import com.hcltech.digitalbankingservice.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(transactionController).build();
    }

    @Test
    void testAddMoneyByAccountNo() throws Exception {
        Long accountNo = 12345L;
        Double amount = 100.0;

        mockMvc.perform(post("/api/transaction/addMoney/{accountNo}", accountNo)
                        .param("amount", String.valueOf(amount))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Money added successfully to account: " + accountNo));

        verify(transactionService, times(1)).addMoneyByAccountNo(accountNo, amount);
    }

    @Test
    void testWithdrawAmountByAccountNo() throws Exception {
        Long accountNo = 12345L;
        Double amount = 50.0;

        mockMvc.perform(post("/api/transaction/withdraw/{accountNo}", accountNo)
                        .param("amount", String.valueOf(amount))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("Amount withdrawn successfully from account: " + accountNo));

        verify(transactionService, times(1)).withdrawAmountByAccountNo(accountNo, amount);
    }

    @Test
    void testGetTransactionById() throws Exception {
        Integer transactionId = 1;
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setTransactionId(transactionId);

        when(transactionService.getTransactionById(transactionId)).thenReturn(transactionDto);

        mockMvc.perform(get("/api/transaction/{transactionId}", transactionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactionId").value(transactionId));

        verify(transactionService, times(1)).getTransactionById(transactionId);
    }

    @Test
    void testGetAllTransactions() throws Exception {
        TransactionDto transaction1 = new TransactionDto();
        TransactionDto transaction2 = new TransactionDto();
        List<TransactionDto> transactions = Arrays.asList(transaction1, transaction2);

        when(transactionService.getAllTransactions()).thenReturn(transactions);

        mockMvc.perform(get("/api/transaction/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(2)));

        verify(transactionService, times(1)).getAllTransactions();
    }

    @Test
    void testGetAllTransactionsByAccountNo() throws Exception {
        Long accountNo = 12345L;
        TransactionDto transactionDto = new TransactionDto();
        transactionDto.setTransactionId(1); // Add necessary fields
        List<TransactionDto> transactions = Arrays.asList(transactionDto);

        when(transactionService.getAllTransactionsByAccountNo(accountNo)).thenReturn(transactions);

        mockMvc.perform(get("/api/transaction/account/{accountNo}", accountNo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)))
                .andExpect(jsonPath("$[0].transactionId").value(1));

        verify(transactionService, times(1)).getAllTransactionsByAccountNo(accountNo);
    }


    @Test
    void testGetTransactionsByCreditCard() throws Exception {
        Long creditCardNo = 1234567890123456L;
        TransactionDto transactionDto = new TransactionDto();
        List<TransactionDto> transactions = Arrays.asList(transactionDto);

        when(transactionService.getTransactionsByCreditCard(creditCardNo)).thenReturn(transactions);

        mockMvc.perform(get("/api/transaction/creditCard/{creditCardNo}", creditCardNo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)));

        verify(transactionService, times(1)).getTransactionsByCreditCard(creditCardNo);
    }

    @Test
    void testGetTransactionsByDebitCard() throws Exception {
        Long debitCardNo = 1234567890123456L;
        TransactionDto transactionDto = new TransactionDto();
        List<TransactionDto> transactions = Arrays.asList(transactionDto);

        when(transactionService.getTransactionsByDebitCard(debitCardNo)).thenReturn(transactions);

        mockMvc.perform(get("/api/transaction/debitCard/{debitCardNo}", debitCardNo)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", org.hamcrest.Matchers.hasSize(1)));

        verify(transactionService, times(1)).getTransactionsByDebitCard(debitCardNo);
    }

    @Test
    void testFundTransfer() throws Exception {
        FundTransferRequestDto fundTransferRequest = new FundTransferRequestDto();
        fundTransferRequest.setSourceAccountNumber(12345L);
        fundTransferRequest.setTargetAccountNumber(67890L);
        fundTransferRequest.setAmount(50.0);
        fundTransferRequest.setPin(1234);

        mockMvc.perform(post("/api/transaction/fund-transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(fundTransferRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string("Fund transfer successful from account: 12345 to account: 67890"));

        verify(transactionService, times(1)).fundTransfer(any(FundTransferRequestDto.class));
    }
}
