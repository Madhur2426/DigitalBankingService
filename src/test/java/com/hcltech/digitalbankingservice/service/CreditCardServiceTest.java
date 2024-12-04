package com.hcltech.digitalbankingservice.service;

import com.hcltech.digitalbankingservice.dao.CreditCardDaoService;
import com.hcltech.digitalbankingservice.exception.CreditCardNotFound;
import com.hcltech.digitalbankingservice.model.CreditCard;
import com.hcltech.digitalbankingservice.repository.AccountRepository;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CreditCardServiceTest {

    @Mock
    private CreditCardDaoService creditCardDaoService;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CreditCardService creditCardService;

    @Test
    void test_to_return_all_credit_cards() {
        CreditCard creditCard = new CreditCard();
        CreditCard creditCard1 = new CreditCard();
        createObjects(creditCard, creditCard1);

        when(creditCardDaoService.getAll()).thenReturn(List.of(creditCard, creditCard1));

        List<CreditCard> cards = creditCardDaoService.getAll();
        assertEquals(2, cards.size());
    }

    @Test
    void test_to_return_credit_card_by_number() {
        CreditCard creditCard = new CreditCard();
        CreditCard creditCard1 = new CreditCard();
        createObjects(creditCard, creditCard1);

        when(creditCardDaoService.getByNo(any())).thenReturn(Optional.of(creditCard));

        Optional<CreditCard> cards = creditCardDaoService.getByNo(1234L);
        assertEquals(1234L, cards.get().getCreditCardNumber());
    }

    @Test
    void test_to_throw_not_found_exception_when_credit_card_not_found_by_number() {
        when(creditCardDaoService.getByNo(any())).thenReturn(Optional.empty());

        Exception exception = assertThrows(CreditCardNotFound.class, () -> creditCardService.getByCreditCardNumber(999L));

        assertEquals("CreditCard with CreditNumber 999 not found", exception.getMessage());
    }

    @Test
    void test_delete_credit_card_by_credit_card_number() {
        Long cardNumber = 1234567890123456L;
        CreditCard card = new CreditCard();
        card.setCreditCardNumber(cardNumber);

        when(creditCardDaoService.delete(cardNumber)).thenReturn(true);

        assertTrue(creditCardDaoService.delete(cardNumber));
    }

    @Test
    void test_delete_not_found_credit_card_by_credit_card_number() {
        Long cardNumber = 1234567890123456L;
        CreditCard card = new CreditCard();
        card.setCreditCardNumber(cardNumber);

        when(creditCardDaoService.delete(cardNumber)).thenReturn(false);

        assertFalse(creditCardDaoService.delete(cardNumber));
    }

    private static void createObjects(CreditCard card1, CreditCard card2) {
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
}
