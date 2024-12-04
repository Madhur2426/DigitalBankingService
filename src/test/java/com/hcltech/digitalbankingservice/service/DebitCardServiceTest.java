package com.hcltech.digitalbankingservice.service;

import com.hcltech.digitalbankingservice.dao.AccountDaoService;
import com.hcltech.digitalbankingservice.dao.DebitCardDaoService;
import com.hcltech.digitalbankingservice.dto.DebitCardDto;
import com.hcltech.digitalbankingservice.model.DebitCard;
import com.hcltech.digitalbankingservice.util.EncryptionUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DebitCardServiceTest {

    private DebitCardService debitCardService;
    private DebitCardDaoService debitCardDaoService;
    private AccountDaoService accountDaoService;

    @BeforeEach
    public void setup() {
        debitCardDaoService = Mockito.mock(DebitCardDaoService.class);
        accountDaoService = Mockito.mock(AccountDaoService.class);

        ModelMapper modelMapper = new ModelMapper();
        debitCardService = new DebitCardService();
        debitCardService.debitCardDaoService = debitCardDaoService;
        debitCardService.accountDaoService = accountDaoService;
        debitCardService.modelMapper = modelMapper;
    }

    @Test
    void testGetDebitCardByDebitCardNumber_Success() {
        Long debitCardNumber = 1234567890123456L;
        DebitCard debitCard = new DebitCard();
        debitCard.setDebitCardNumber(debitCardNumber);

        when(debitCardDaoService.findByDebitCardNumber(debitCardNumber)).thenReturn(Optional.of(debitCard));

        Optional<DebitCardDto> result = debitCardService.getDebitCardByDebitCardNumber(debitCardNumber);

        assertTrue(result.isPresent());
        assertEquals(debitCardNumber, result.get().getDebitCardNumber());
    }

    @Test
    void testUpdateDebitCard_Success() {
        Long debitCardNumber = 1234567890123456L;
        String oldPin = "1234";
        String newPin = "5678";

        DebitCard debitCard = new DebitCard();
        debitCard.setPin(EncryptionUtil.encrypt(oldPin));

        when(debitCardDaoService.findByDebitCardNumber(debitCardNumber)).thenReturn(Optional.of(debitCard));
        when(debitCardDaoService.update(any(DebitCard.class))).thenReturn(debitCard);

        Optional<DebitCardDto> result = debitCardService.updateDebitCard(debitCardNumber, oldPin, newPin);

        assertTrue(result.isPresent(), "Expected the result to be present.");
        assertEquals(newPin, EncryptionUtil.decrypt(result.get().getPin()), "The PIN should be updated correctly.");
    }

    @Test
    void testDeleteDebitCardByCardNumber_Success() {
        Long debitCardNumber = 1234567890123456L;
        DebitCard debitCard = new DebitCard();
        debitCard.setDebitCardNumber(debitCardNumber);

        when(debitCardDaoService.findByDebitCardNumber(debitCardNumber)).thenReturn(Optional.of(debitCard));

        assertDoesNotThrow(() -> debitCardService.deleteDebitCardByCardNumber(debitCardNumber));

        verify(debitCardDaoService, times(1)).deleteByDebitCardNumber(debitCardNumber);
    }
}
