package com.hcltech.digitalbankingservice.dao;

import com.hcltech.digitalbankingservice.model.DebitCard;
import com.hcltech.digitalbankingservice.repository.DebitCardRepository;
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

public class DebitCardDaoServiceTest {

    @InjectMocks
    private DebitCardDaoService debitCardDaoService;

    @Mock
    private DebitCardRepository debitCardRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindByDebitCardNumber_NotFound() {
        Long debitCardNumber = 1234567890123456L;

        when(debitCardRepository.findById(debitCardNumber)).thenReturn(Optional.empty());

        Optional<DebitCard> result = debitCardDaoService.findByDebitCardNumber(debitCardNumber);

        assertFalse(result.isPresent());
    }

    @Test
    void testFindByDebitCardNumber_Success() {
        Long debitCardNumber = 1234567890123456L;

        DebitCard debitCard = new DebitCard();
        debitCard.setDebitCardNumber(debitCardNumber);

        when(debitCardRepository.findById(debitCardNumber)).thenReturn(Optional.of(debitCard));

        Optional<DebitCard> result = debitCardDaoService.findByDebitCardNumber(debitCardNumber);

        assertTrue(result.isPresent());
        assertEquals(debitCardNumber, result.get().getDebitCardNumber());
    }

    @Test
    void testGetAll_Success() {
        DebitCard debitCard = new DebitCard();
        debitCard.setDebitCardNumber(1234567890123456L);

        when(debitCardRepository.findAll()).thenReturn(Collections.singletonList(debitCard));

        List<DebitCard> result = debitCardDaoService.getAll();

        assertEquals(1, result.size());
        assertEquals(debitCard.getDebitCardNumber(), result.get(0).getDebitCardNumber());
    }

    @Test
    void testSave_Success() {
        DebitCard debitCard = new DebitCard();
        debitCard.setDebitCardNumber(1234567890123456L);

        when(debitCardRepository.save(any(DebitCard.class))).thenReturn(debitCard);

        DebitCard savedDebitCard = debitCardDaoService.save(debitCard);

        assertNotNull(savedDebitCard);
        assertEquals(debitCard.getDebitCardNumber(), savedDebitCard.getDebitCardNumber());

        verify(debitCardRepository, times(1)).save(any(DebitCard.class));
    }

    @Test
    void testUpdate_Success() {
        Long debitCardNumber = 1234567890123456L;

        DebitCard debitCard = new DebitCard();
        debitCard.setDebitCardNumber(debitCardNumber);

        when(debitCardRepository.save(any(DebitCard.class))).thenReturn(debitCard);

        DebitCard updatedDebitCard = debitCardDaoService.update(debitCard);

        assertNotNull(updatedDebitCard);
        assertEquals(debitCardNumber, updatedDebitCard.getDebitCardNumber());

        verify(debitCardRepository, times(1)).save(any(DebitCard.class));
    }

    @Test
    void testDelete_Success() {
        Long debitCardNumber = 1234567890123456L;

        doNothing().when(debitCardRepository).deleteById(debitCardNumber);

        assertDoesNotThrow(() -> debitCardDaoService.deleteByDebitCardNumber(debitCardNumber));

        verify(debitCardRepository, times(1)).deleteById(debitCardNumber);
    }
}
