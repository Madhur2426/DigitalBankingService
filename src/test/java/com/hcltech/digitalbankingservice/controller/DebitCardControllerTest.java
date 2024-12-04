package com.hcltech.digitalbankingservice.controller;

import com.hcltech.digitalbankingservice.dto.DebitCardDto;

import com.hcltech.digitalbankingservice.service.DebitCardService;

import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;

import org.mockito.Mock;

import org.mockito.MockitoAnnotations;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.*;

class DebitCardControllerTest {

    @InjectMocks

    private DebitCardController debitCardController;

    @Mock

    private DebitCardService debitCardService;

    @BeforeEach

    void setUp() {

        MockitoAnnotations.openMocks(this);

    }

    @Test

    void testGetDebitCardByAccountNo_Success() {

        Long accountNumber = 123456789L;

        DebitCardDto debitCardDto = new DebitCardDto(); // populate with test data

        when(debitCardService.getDebitCardByAccountNumber(accountNumber)).thenReturn(Optional.of(debitCardDto));

        ResponseEntity<DebitCardDto> response = debitCardController.getDebitCardByAccountNo(accountNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(debitCardDto, response.getBody());

        verify(debitCardService).getDebitCardByAccountNumber(accountNumber);

    }

    @Test

    void testGetDebitCardByAccountNo_NotFound() {

        Long accountNumber = 123456789L;

        when(debitCardService.getDebitCardByAccountNumber(accountNumber)).thenReturn(Optional.empty());

        ResponseEntity<DebitCardDto> response = debitCardController.getDebitCardByAccountNo(accountNumber);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(debitCardService).getDebitCardByAccountNumber(accountNumber);

    }

    @Test

    void testGetDebitCardByCardNumber_Success() {

        Long debitCardNumber = 987654321L;

        DebitCardDto debitCardDto = new DebitCardDto(); // populate with test data

        when(debitCardService.getDebitCardByDebitCardNumber(debitCardNumber)).thenReturn(Optional.of(debitCardDto));

        ResponseEntity<DebitCardDto> response = debitCardController.getDebitCardByCardNumber(debitCardNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(debitCardDto, response.getBody());

        verify(debitCardService).getDebitCardByDebitCardNumber(debitCardNumber);

    }

    @Test

    void testGetDebitCardByCardNumber_NotFound() {

        Long debitCardNumber = 987654321L;

        when(debitCardService.getDebitCardByDebitCardNumber(debitCardNumber)).thenReturn(Optional.empty());

        ResponseEntity<DebitCardDto> response = debitCardController.getDebitCardByCardNumber(debitCardNumber);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(debitCardService).getDebitCardByDebitCardNumber(debitCardNumber);

    }

    @Test

    void testCreateDebitCard() {

        Long accountNumber = 123456789L;

        DebitCardDto debitCardDto = new DebitCardDto();

        when(debitCardService.createDebitCard(eq(accountNumber), any(DebitCardDto.class))).thenReturn(Optional.of(debitCardDto));

        ResponseEntity<DebitCardDto> response = debitCardController.createDebitCard(accountNumber, debitCardDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals(debitCardDto, response.getBody());

        verify(debitCardService).createDebitCard(accountNumber, debitCardDto);

    }

    @Test

    void testUpdateDebitCardPin_Success() {

        Long debitCardNumber = 987654321L;

        String oldPin = "1234";

        String newPin = "5678";

        DebitCardDto debitCardDto = new DebitCardDto();

        when(debitCardService.updateDebitCard(debitCardNumber, oldPin, newPin)).thenReturn(Optional.of(debitCardDto));

        ResponseEntity<String> response = debitCardController.updateDebitCardPin(debitCardNumber, oldPin, newPin);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        //assertEquals(debitCardDto, response.getBody());

        verify(debitCardService).updateDebitCard(debitCardNumber, oldPin, newPin);

    }

    @Test

    void testUpdateDebitCardPin_NotFound() {

        Long debitCardNumber = 987654321L;

        String oldPin = "1234";

        String newPin = "5678";

        when(debitCardService.updateDebitCard(debitCardNumber, oldPin, newPin)).thenReturn(Optional.empty());

        ResponseEntity<String> response = debitCardController.updateDebitCardPin(debitCardNumber, oldPin, newPin);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(debitCardService).updateDebitCard(debitCardNumber, oldPin, newPin);

    }

//    @Test
//    void testUpdateDebitCardStatus_Success() {
//
//        Long debitCardNumber = 987654321L;
//
//        String pin = "1234";
//
//        boolean blocked = true;
//
//        DebitCardDto debitCardDto = new DebitCardDto();
//
//        when(debitCardService.updateStatus(debitCardNumber, pin, blocked)).thenReturn(Optional.of(debitCardDto));
//
//        ResponseEntity<String> response = debitCardController.updateDebitCardStatus(debitCardNumber, pin, blocked);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//
//        assertEquals(debitCardDto, response.getBody());
//
//        verify(debitCardService).updateStatus(debitCardNumber, pin, blocked);
//
//    }

    @Test
    void testUpdateDebitCardStatus_NotFound() {

        Long debitCardNumber = 987654321L;

        String pin = "1234";

        boolean blocked = true;

        when(debitCardService.updateStatus(debitCardNumber, pin, blocked)).thenReturn(Optional.empty());

        ResponseEntity<String> response = debitCardController.updateDebitCardStatus(debitCardNumber, pin, blocked);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        verify(debitCardService).updateStatus(debitCardNumber, pin, blocked);

    }

    @Test
    void testDeleteDebitCardByAccountNumber_Success() {

        Long accountNumber = 123456789L;

        when(debitCardService.deleteDebitCardByAccountNumber(accountNumber)).thenReturn(true);

        ResponseEntity<String> response = debitCardController.deleteDebitCardByAccountNumber(accountNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals("Debit card has been deleted.", response.getBody());

        verify(debitCardService).deleteDebitCardByAccountNumber(accountNumber);

    }

    @Test

    void testDeleteDebitCardByAccountNumber_NotFound() {

        Long accountNumber = 123456789L;

        when(debitCardService.deleteDebitCardByAccountNumber(accountNumber)).thenReturn(false);

        ResponseEntity<String> response = debitCardController.deleteDebitCardByAccountNumber(accountNumber);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        assertEquals("Debit card not found.", response.getBody());

        verify(debitCardService).deleteDebitCardByAccountNumber(accountNumber);

    }

    @Test

    void testDeleteDebitCardByCardNumber() {

        Long debitCardNumber = 987654321L;

        ResponseEntity<String> response = debitCardController.deleteDebitCardByCardNumber(debitCardNumber);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals("Debit card has been deleted.", response.getBody());

        verify(debitCardService).deleteDebitCardByCardNumber(debitCardNumber);

    }

    @Test

    void testTransferFunds_Success() {

        Long fromDebitCardNumber = 987654321L;

        Long toAccountNumber = 123456789L;

        Double amount = 100.0;

        String pin = "1234";

        ResponseEntity<String> response = debitCardController.transferFunds(fromDebitCardNumber, toAccountNumber, amount, pin);

        assertEquals(HttpStatus.OK, response.getStatusCode());

        assertEquals("Transfer successful.", response.getBody());

        verify(debitCardService).transferFunds(fromDebitCardNumber, toAccountNumber, amount, pin);

    }

    @Test

    void testTransferFunds_Failure() {

        Long fromDebitCardNumber = 987654321L;

        Long toAccountNumber = 123456789L;

        Double amount = 100.0;

        String pin = "1234";

        String errorMessage = "Insufficient funds";

        doThrow(new RuntimeException(errorMessage)).when(debitCardService).transferFunds(fromDebitCardNumber, toAccountNumber, amount, pin);

        ResponseEntity<String> response = debitCardController.transferFunds(fromDebitCardNumber, toAccountNumber, amount, pin);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        assertEquals(errorMessage, response.getBody());

        verify(debitCardService).transferFunds(fromDebitCardNumber, toAccountNumber, amount, pin);

    }

}
