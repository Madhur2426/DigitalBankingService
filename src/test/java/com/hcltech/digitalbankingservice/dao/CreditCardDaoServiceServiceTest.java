package com.hcltech.digitalbankingservice.dao;

import com.hcltech.digitalbankingservice.model.CreditCard;
import com.hcltech.digitalbankingservice.repository.CreditCardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CreditCardDaoServiceServiceTest {

    @InjectMocks
    private CreditCardDaoService creditCardService;

    @Mock
    private CreditCardRepository creditCardRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreditCardByNumber_Success() {
        Long number = 123456L;
        CreditCard creditCard = new CreditCard();
        creditCard.setCreditCardNumber(number);

        when(creditCardRepository.findById(number)).thenReturn(Optional.of(creditCard));

        Optional<CreditCard> result = creditCardService.getByNo(number);

        assertTrue(result.isPresent());
        assertEquals(number, result.get().getCreditCardNumber());
    }

    @Test
    void testNoCreditCardByNumber() {
        Long number = 123456L;

        when(creditCardRepository.findById(number)).thenReturn(Optional.empty());

        Optional<CreditCard> result = creditCardService.getByNo(number);

        assertFalse(result.isPresent());
    }

    @Test
    void testGetAllCreditCards() {
        CreditCard card = new CreditCard();
        card.setCreditCardNumber(1234567890123456L);

        when(creditCardRepository.findAll()).thenReturn(Collections.singletonList(card));

        List<CreditCard> result = creditCardService.getAll();

        assertEquals(1, result.size());
        assertEquals(card.getCreditCardNumber(), result.get(0).getCreditCardNumber());
    }

    @Test
    void testSaveCreditCard() {
        CreditCard card = new CreditCard();
        card.setCreditCardNumber(1234567890123456L);

        when(creditCardRepository.save(any(CreditCard.class))).thenReturn(card);

        CreditCard savedCard = creditCardService.create(card);

        assertNotNull(savedCard);
        assertEquals(card.getCreditCardNumber(), savedCard.getCreditCardNumber());

        verify(creditCardRepository, times(1)).save(any(CreditCard.class));
    }

    @Test
    void testUpdateCreditCard() {
        CreditCard card = new CreditCard();
        card.setCreditCardNumber(1234567890123456L);

        when(creditCardRepository.save(any(CreditCard.class))).thenReturn(card);

        CreditCard updatedCard = creditCardService.update(card);

        assertNotNull(updatedCard);
        assertEquals(card.getCreditCardNumber(), updatedCard.getCreditCardNumber());

        verify(creditCardRepository, times(1)).save(any(CreditCard.class));
    }

    @Test
    void testDeleteCreditCardByNumber() {
        Long number = 1234567890123456L;

        doNothing().when(creditCardRepository).deleteById(number);

        assertDoesNotThrow(() -> creditCardService.delete(number));

        verify(creditCardRepository, times(1)).deleteById(number);
    }
}
