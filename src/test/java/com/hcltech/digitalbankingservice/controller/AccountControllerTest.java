package com.hcltech.digitalbankingservice.controller;
import com.hcltech.digitalbankingservice.dto.AccountDto;
import com.hcltech.digitalbankingservice.exception.AccountNotFoundException;
import com.hcltech.digitalbankingservice.service.AccountService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@SpringBootTest
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Mock
    private AccountService accountService;

    @InjectMocks
    private AccountController accountController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(accountController).build();
    }

    @Test
    void testGetAllAccounts_Success() throws Exception {
        AccountDto account1 = new AccountDto();
        account1.setAccountNumber(12345L);

        AccountDto account2 = new AccountDto();
        account2.setAccountNumber(67890L);

        List<AccountDto> accountList = Arrays.asList(account1, account2);

        when(accountService.getAllAccount()).thenReturn(Optional.of(accountList));

        mockMvc.perform(get("/api/account/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value(12345L))
                .andExpect(jsonPath("$[1].accountNumber").value(67890L));

        verify(accountService, times(1)).getAllAccount();
    }

    @Test
    void testGetAccountByAccountNo_Success() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountNumber(12345L);

        when(accountService.getByAccountNumber(12345L)).thenReturn(Optional.of(accountDto));

        mockMvc.perform(get("/api/account/12345"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(12345L));

        verify(accountService, times(1)).getByAccountNumber(12345L);
    }

    @Test
    void testGetAccountByAccountNo_NotFound() throws Exception {
        when(accountService.getByAccountNumber(12345L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/account/12345"))
                .andExpect(status().isNotFound());

        verify(accountService, times(1)).getByAccountNumber(12345L);
    }

    @Test
    void testGetAccountByCustomerId_Success() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountNumber(12345L);

        List<AccountDto> accountList = Arrays.asList(accountDto);

        when(accountService.getByCustomerId(1L)).thenReturn(Optional.of(accountList));

        mockMvc.perform(get("/api/account/customer/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountNumber").value(12345L));

        verify(accountService, times(1)).getByCustomerId(1L);
    }

    @Test
    void testCreateAccount_Success() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountNumber(12345L);

        when(accountService.createAccount(anyLong(), any(AccountDto.class))).thenReturn(Optional.of(accountDto));

        mockMvc.perform(post("/api/account/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\": 12345}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(12345L));

        verify(accountService, times(1)).createAccount(anyLong(), any(AccountDto.class));
    }

    @Test
    void testCreateAccount_Failure() throws Exception {
        when(accountService.createAccount(anyLong(), any(AccountDto.class))).thenReturn(Optional.empty());

        mockMvc.perform(post("/api/account/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\": 12345}"))
                .andExpect(status().isBadRequest());

        verify(accountService, times(1)).createAccount(anyLong(), any(AccountDto.class));
    }

    @Test
    void testUpdateAccount_Success() throws Exception {
        AccountDto accountDto = new AccountDto();
        accountDto.setAccountNumber(12345L);

        when(accountService.updateAccount(anyLong(), any(AccountDto.class))).thenReturn(accountDto);

        mockMvc.perform(put("/api/account/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\": 12345}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber").value(12345L));

        verify(accountService, times(1)).updateAccount(anyLong(), any(AccountDto.class));
    }

    @Test
    void testDeleteAccountByAccountNo_Success() throws Exception {
        doNothing().when(accountService).deleteAccountByAccountNo(12345L);

        mockMvc.perform(delete("/api/account/12345"))
                .andExpect(status().isOk())
                .andExpect(content().string("Account deleted successfully."));

        verify(accountService, times(1)).deleteAccountByAccountNo(12345L);
    }

    @Test
    void testDeleteAccountByAccountNo_NotFound() throws Exception {
        doThrow(new AccountNotFoundException("Account not found")).when(accountService).deleteAccountByAccountNo(12345L);

        mockMvc.perform(delete("/api/account/12345"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Account not found"));

        verify(accountService, times(1)).deleteAccountByAccountNo(12345L);
    }
}
