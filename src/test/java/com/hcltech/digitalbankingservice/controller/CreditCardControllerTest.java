package com.hcltech.digitalbankingservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.hcltech.digitalbankingservice.dto.CreditCardDto;

import com.hcltech.digitalbankingservice.exception.CreditCardNotCreatedException;

import com.hcltech.digitalbankingservice.exception.CreditCardNotFound;

import com.hcltech.digitalbankingservice.exception.PinMissMatchException;

import com.hcltech.digitalbankingservice.model.Account;

import com.hcltech.digitalbankingservice.service.AccountService;

import com.hcltech.digitalbankingservice.service.CreditCardService;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;

import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.HttpStatus;

import org.springframework.http.MediaType;

import org.springframework.http.ResponseEntity;

import org.springframework.test.web.servlet.MockMvc;

import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;

import java.util.List;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest

@AutoConfigureMockMvc

public class CreditCardControllerTest {

    @Autowired

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper;

    @Mock

    private CreditCardService creditCardService;

    @InjectMocks

    private CreditCardController creditCardController;

    @Autowired

    public CreditCardControllerTest(ObjectMapper objectMapper) {

        this.objectMapper = objectMapper;

    }

    @BeforeEach

    void setUp() {

        MockitoAnnotations.openMocks(this);

        mockMvc = MockMvcBuilders.standaloneSetup(creditCardController).build();

    }

    @Test

    void should_return_all_credit_cards() throws Exception {

        //Arrange

        CreditCardDto dto1 = new CreditCardDto();

        CreditCardDto dto2 = new CreditCardDto();

        createObjects(dto1,dto2);

        when(creditCardService.getAll()).thenReturn(List.of(dto1,dto2));

        String expDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy,MM,dd"));

        //Act

        mockMvc.perform(get("/api/creditCard"))

                .andExpect(status().isOk())

                .andExpect(jsonPath("$.size()").value(2))

                .andExpect(jsonPath("$[0].creditCardNumber").value(1234L))

//                .andExpect(jsonPath("$[0].expDate").value(expDate))

                .andExpect(jsonPath("$[0].cvv").value("123"))

                .andExpect(jsonPath("$[0].fullName").value("Mahesh A"))

                .andExpect(jsonPath("$[0].isBlocked").value(false))

                .andExpect(jsonPath("$[0].pin").value("1234"))

                .andExpect(jsonPath("$[1].creditCardNumber").value(5678L))

//                .andExpect(jsonPath("$[1].expDate").value(expDate))

                .andExpect(jsonPath("$[1].cvv").value("456"))

                .andExpect(jsonPath("$[1].fullName").value("Dinesh A"))

                .andExpect(jsonPath("$[1].isBlocked").value(false))

                .andExpect(jsonPath("$[1].pin").value("5678"));

    }

    @Test

    void should_return_credit_card_by_credit_card_number() throws Exception {

        CreditCardDto card1=new CreditCardDto();

        CreditCardDto card2=new CreditCardDto();


        createObjects(card1,card2);

        when(creditCardService.getByCreditCardNumber(any())).thenReturn(Optional.of(card1));

        String expDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //Act

        mockMvc.perform(get("/api/creditCard/{creditCardNumber}",1234L))

                .andExpect(status().isOk())

                .andExpect(MockMvcResultMatchers.jsonPath("$.creditCardNumber").value(1234L))

                //.andExpect(MockMvcResultMatchers.jsonPath("$.expDate").value(expDate))

                .andExpect(MockMvcResultMatchers.jsonPath("$.cvv").value("123"))

                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value("Mahesh A"))

                .andExpect(MockMvcResultMatchers.jsonPath("$.isBlocked").value(false))

                .andExpect(MockMvcResultMatchers.jsonPath("$.pin").value("1234"));

    }

    @Test

    void should_return_created_credit_card() throws Exception {

        CreditCardDto card1=new CreditCardDto();

        CreditCardDto card2=new CreditCardDto();

        createObjects(card1,card2);

        when(creditCardService.createCreditCard(any(),any())).thenReturn(Optional.of(card1));

        String expDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //Act

        mockMvc.perform(post("/api/creditCard/{creditCardNumber}",1234L).contentType(MediaType.APPLICATION_JSON)

                        .content(objectMapper.writeValueAsString(card1)))

                .andExpect(status().isOk())

                .andExpect(MockMvcResultMatchers.jsonPath("$.creditCardNumber").value(1234L))

                //.andExpect(MockMvcResultMatchers.jsonPath("$.expDate").value(expDate))

                .andExpect(MockMvcResultMatchers.jsonPath("$.cvv").value("123"))

                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value("Mahesh A"))

                .andExpect(MockMvcResultMatchers.jsonPath("$.isBlocked").value(false))

                .andExpect(MockMvcResultMatchers.jsonPath("$.pin").value("1234"));

    }

    @Test

    void should_return_not_created_credit_card() throws Exception {

        CreditCardDto card1=new CreditCardDto();

        CreditCardDto card2=new CreditCardDto();

        createObjects(card1,card2);


        when(creditCardService.getByCreditCardNumber(any())).thenThrow(new CreditCardNotCreatedException(9999L));

        mockMvc.perform(post("/api/creditCard/{creditCardNumber}",9999L).contentType(MediaType.APPLICATION_JSON)

                        .content(objectMapper.writeValueAsString(card1)))

                .andExpect(status().isNotFound());

    }

    @Test

    void should_return_pin_updated_credit_card_by_credit_card_number() throws Exception {

        CreditCardDto card1=new CreditCardDto();

        CreditCardDto card2=new CreditCardDto();


        createObjects(card1,card2);

        card1.setPin("3456");

        when(creditCardService.update(any(),any(),any())).thenReturn(Optional.of(card1));

        String expDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //Act

        mockMvc.perform(put("/api/creditCard/{creditCardNumber}/{oldPin}/{newPin}",1234L,"1234","3456"))

                .andExpect(status().isOk())

                .andExpect(MockMvcResultMatchers.jsonPath("$.creditCardNumber").value(1234L))

                //.andExpect(MockMvcResultMatchers.jsonPath("$.expDate").value(expDate))

                .andExpect(MockMvcResultMatchers.jsonPath("$.cvv").value("123"))

                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value("Mahesh A"))

                .andExpect(MockMvcResultMatchers.jsonPath("$.isBlocked").value(false))

                .andExpect(MockMvcResultMatchers.jsonPath("$.pin").value("3456"));

    }

    @Test

    void should_return_deleted_credit_card_by_credit_card_number() throws Exception {

        CreditCardDto card1=new CreditCardDto();

        CreditCardDto card2=new CreditCardDto();


        createObjects(card1,card2);

        when(creditCardService.delete(any())).thenReturn(true);

        String expDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //Act

        mockMvc.perform(delete("/api/creditCard/{creditCardNumber}", 1234L))

                .andExpect(status().isOk())

                .andExpect(content().string("Credit Card Deleted Successfully."));

    }

    private static void createObjects(CreditCardDto card1, CreditCardDto card2)

    {

        card1.setCreditCardNumber(1234L);

        card1.setFullName("Mahesh A");

        card1.setExpDate(LocalDate.now());

        card1.setPin("1234");

        card1.setIsBlocked(false);

        card1.setCvv("123");

        card2.setCreditCardNumber(5678L);

        card2.setFullName("Dinesh A");

        card2.setExpDate(LocalDate.now());

        card2.setPin("5678");

        card2.setIsBlocked(false);

        card2.setCvv("456");

    }

    //-----------------

    @Test

    void test_return_empty_list_of_credit_cards() throws Exception {

        when(creditCardService.getAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/creditCard"))

                .andExpect(status().isOk());

    }

    @Test

    void should_return_deleted_credit_card_by_account_number() throws Exception {

        CreditCardDto card1=new CreditCardDto();

        CreditCardDto card2=new CreditCardDto();


        createObjects(card1,card2);

        when(creditCardService.deleteCreditCardByAccountNumber(1234L)).thenReturn(true);

        String expDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //Act

        mockMvc.perform(delete("/api/creditCard/account/{accountNumber}", 1234L))

                .andExpect(status().isOk())

                .andExpect(content().string("Credit Card Deleted Successfully."));

    }

    @Test

    void test_return_pin_not_updated_credit_card_by_credit_card_number() throws Exception {

        CreditCardDto card1=new CreditCardDto();

        CreditCardDto card2=new CreditCardDto();


        createObjects(card1,card2);

        when(creditCardService.update(1234L,"3241","5432")).thenThrow(new PinMissMatchException("pin not updated"));

        String expDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //Act

        mockMvc.perform(put("/api/creditCard/{creditCardNumber}/{oldPin}/{newPin}",1234L,"1234","3456"))

                .andExpect(status().isNotFound());

    }

    @Test

    void should_return_credit_card_by_account_number() throws Exception {

        CreditCardDto card1=new CreditCardDto();

        CreditCardDto card2=new CreditCardDto();


        createObjects(card1,card2);

        when(creditCardService.getCreditCardByAccountNumber(1234L)).thenReturn(Optional.of(card1));

        String expDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //Act

        mockMvc.perform(get("/api/creditCard/account/{accountNumber}",1234L))

                .andExpect(status().isOk())

                .andExpect(MockMvcResultMatchers.jsonPath("$.creditCardNumber").value(1234L))

                //.andExpect(MockMvcResultMatchers.jsonPath("$.expDate").value(expDate))

                .andExpect(MockMvcResultMatchers.jsonPath("$.cvv").value("123"))

                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value("Mahesh A"))

                .andExpect(MockMvcResultMatchers.jsonPath("$.isBlocked").value(false))

                .andExpect(MockMvcResultMatchers.jsonPath("$.pin").value("1234"));

    }

    @Test

    void should_return_status_updated_credit_card_by_credit_card_number() throws Exception {

        CreditCardDto card1=new CreditCardDto();

        CreditCardDto card2=new CreditCardDto();


        createObjects(card1,card2);

        card1.setIsBlocked(true);

        when(creditCardService.updateStatus(1234L,"1234",true)).thenReturn(Optional.of(card1));

        String expDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        //Act

        mockMvc.perform(put("/api/creditCard/{creditCardNumber}/{pin}/status/{blocked}",1234L,"1234",true))

                .andExpect(status().isOk())

                .andExpect(MockMvcResultMatchers.jsonPath("$.creditCardNumber").value(1234L))

                //.andExpect(MockMvcResultMatchers.jsonPath("$.expDate").value(expDate))

                .andExpect(MockMvcResultMatchers.jsonPath("$.cvv").value("123"))

                .andExpect(MockMvcResultMatchers.jsonPath("$.fullName").value("Mahesh A"))

                .andExpect(MockMvcResultMatchers.jsonPath("$.isBlocked").value(true))

                .andExpect(MockMvcResultMatchers.jsonPath("$.pin").value("1234"));

    }

    @Test

    void test_transfer_funds_to_account_success() {

        Long fromCardNumber = 987654321L;

        Long toAccountNumber = 123456789L;

        Double amount = 100.0;

        String pin = "1234";

        ResponseEntity<String> response = creditCardController.transferFunds(fromCardNumber, toAccountNumber, amount, pin);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals("Transfer successful.", response.getBody());

        verify(creditCardService).transferFunds(fromCardNumber, toAccountNumber, amount, pin);

    }

    @Test

    void test_transfer_funds_to_credit_card_success() {

        Long toCardNumber = 987654321L;

        Long fromAccountNumber = 123456789L;

        Double amount = 100.0;

        int pin = 1234;

        ResponseEntity<String> response = creditCardController.transferFundsToAccount(toCardNumber,fromAccountNumber,amount,pin);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals("Transfer successful.", response.getBody());

        verify(creditCardService).transferFundsToAccount(toCardNumber, fromAccountNumber, amount, pin);

    }

    @Test

    void test_transfer_funds_to_account_failure() {

        Long fromCreditCardNumber = 987654321L;

        Long toAccountNumber = 123456789L;

        Double amount = 100.0;

        String pin = "1234";

        String errorMessage = "Insufficient funds";

        doThrow(new RuntimeException(errorMessage)).when(creditCardService).transferFunds(fromCreditCardNumber, toAccountNumber, amount, pin);

        ResponseEntity<String> response = creditCardController.transferFunds(fromCreditCardNumber, toAccountNumber, amount, pin);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertEquals(errorMessage, response.getBody());

        verify(creditCardService).transferFunds(fromCreditCardNumber, toAccountNumber, amount, pin);

    }

    @Test

    void test_transfer_funds_to_credit_card_failure() {

        Long toCardNumber = 987654321L;

        Long fromAccountNumber = 123456789L;

        Double amount = 100.0;

        int pin = 1234;

        String errorMessage = "Insufficient funds";

        doThrow(new RuntimeException(errorMessage)).when(creditCardService).transferFundsToAccount(toCardNumber,fromAccountNumber,amount,pin);

        ResponseEntity<String> response = creditCardController.transferFundsToAccount(toCardNumber, fromAccountNumber, amount, pin);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertEquals(errorMessage, response.getBody());

        verify(creditCardService).transferFundsToAccount(toCardNumber, fromAccountNumber, amount, pin);

    }

}
