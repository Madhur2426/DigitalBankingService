package com.hcltech.digitalbankingservice.service;

import com.hcltech.digitalbankingservice.dao.AccountDaoService;
import com.hcltech.digitalbankingservice.dao.DebitCardDaoService;
import com.hcltech.digitalbankingservice.dto.DebitCardDto;
import com.hcltech.digitalbankingservice.exception.*;
import com.hcltech.digitalbankingservice.model.Account;
import com.hcltech.digitalbankingservice.model.DebitCard;
import com.hcltech.digitalbankingservice.model.Transaction;
import com.hcltech.digitalbankingservice.util.EncryptionUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class DebitCardService {

    @Autowired
    DebitCardDaoService debitCardDaoService;

    @Autowired
    AccountDaoService accountDaoService;

    @Autowired
    ModelMapper modelMapper;

    private static final Random random = new Random();

    public DebitCardDto toDto(DebitCard debitCard) {
        return modelMapper.map(debitCard, DebitCardDto.class);
    }

    public DebitCard toEntity(DebitCardDto debitCardDto) {
        DebitCard debitCard = modelMapper.map(debitCardDto, DebitCard.class);
        debitCard.setDebitCardNumber(generateDebitCardNumber());
        debitCard.setPin(EncryptionUtil.encrypt(debitCardDto.getPin()));
        debitCard.setCvv(EncryptionUtil.encrypt(debitCardDto.getCvv()));
        return debitCard;
    }

    public Optional<DebitCardDto> getDebitCardByDebitCardNumber(Long debitCardNumber) {
        Optional<DebitCard> result = debitCardDaoService.findByDebitCardNumber(debitCardNumber);
        return Optional.ofNullable(result.map(this::toDto)
                .orElseThrow(() -> new DebitCardNotFoundException("No Debit Card found for debit card number: " + debitCardNumber)));
    }

    public Optional<DebitCardDto> getDebitCardByAccountNumber(Long accountNumber) {
        List<DebitCard> result = debitCardDaoService.getAll();
        if (result == null || result.isEmpty()) {
            throw new DebitCardNotFoundException("No debit card found for account number: " + accountNumber);
        }
        return Optional.ofNullable(result.stream()
                .filter(debitCard -> debitCard.getAccount() != null && debitCard.getAccount().getAccountNumber().equals(accountNumber))
                .findFirst()
                .map(this::toDto)
                .orElseThrow(() -> new DebitCardNotFoundException("No debit card found for account number: " + accountNumber)));
    }

    public Optional<DebitCardDto> createDebitCard(Long accountNumber, DebitCardDto debitCardDto) {
        Account account = accountDaoService.getByAccountNo(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found with account number: " + accountNumber));

        if (!account.getDebitCards().isEmpty()) {
            throw new DebitCardAlreadyExistsException("Debit card already issued to this account number.");
        }

        DebitCard debitCard = toEntity(debitCardDto);
        debitCard.setFullName(debitCardDto.getFullName());
        debitCard.setExpDate(LocalDate.now().plusYears(5));
        debitCard.setAccount(account);
        debitCard.setIsBlocked(false);

        DebitCard createdDebitCard = debitCardDaoService.save(debitCard);
        return Optional.of(toDto(createdDebitCard));
    }

    public Optional<DebitCardDto> updateDebitCard(Long debitCardNumber, String oldPin, String newPin) {
        try {
            DebitCard debitCard = debitCardDaoService.findByDebitCardNumber(debitCardNumber)
                    .orElseThrow(() -> new DebitCardNotFoundException("No Debit Card found for Debit Card Number: " + debitCardNumber));

            String decryptedOldPin = EncryptionUtil.decrypt(debitCard.getPin());

            if (!decryptedOldPin.equals(oldPin)) {
                System.err.println("The provided old PIN is incorrect for Debit Card Number: " + debitCardNumber);
                return Optional.empty();
            }
            debitCard.setPin(EncryptionUtil.encrypt(newPin));
            debitCardDaoService.update(debitCard);
            System.out.println("PIN updated successfully for Debit Card Number: " + debitCardNumber);
            return Optional.of(toDto(debitCard));
        } catch (Exception e) {
            System.err.println("Error while updating PIN for Debit Card Number: " + debitCardNumber + ". Reason: " + e.getMessage());
            return Optional.empty();
        }
    }


    public Optional<DebitCardDto> updateStatus(Long debitCardNumber, String pin, boolean blocked) {
        try {
            DebitCard debitCard = debitCardDaoService.findByDebitCardNumber(debitCardNumber)
                    .orElseThrow(() -> new DebitCardNotFoundException("Debit Card not found for debit card number: " + debitCardNumber));

            if (!EncryptionUtil.decrypt(debitCard.getPin()).equals(pin)) {
                System.err.println("Invalid PIN for Debit Card Number: " + debitCardNumber);
                return Optional.empty();
            }

            if (debitCard.getIsBlocked() == blocked) {
                System.err.println("The debit card is already " + (blocked ? "blocked" : "unblocked"));
                return Optional.empty();
            }

            debitCard.setIsBlocked(blocked);
            debitCardDaoService.update(debitCard);
            System.out.println("Debit Card status updated for Debit Card Number: " + debitCardNumber);
            return Optional.of(toDto(debitCard));
        } catch (Exception e) {
            System.err.println("Error while updating status for Debit Card Number: " + debitCardNumber + ". Reason: " + e.getMessage());
            return Optional.empty();
        }
    }


    public void deleteDebitCardByCardNumber(Long debitCardNumber) {
        debitCardDaoService.findByDebitCardNumber(debitCardNumber)
                .orElseThrow(() -> new DebitCardNotFoundException("Debit card not found for card number: " + debitCardNumber));

        debitCardDaoService.deleteByDebitCardNumber(debitCardNumber);
    }

    public boolean deleteDebitCardByAccountNumber(Long accountNumber) {
        List<DebitCard> debitCards = debitCardDaoService.getAll();
        Optional<DebitCard> debitCard = debitCards.stream()
                .filter(card -> card.getAccount().getAccountNumber().equals(accountNumber))
                .findFirst();

        return debitCard.map(card -> debitCardDaoService.delete(card.getDebitCardNumber()))
                .orElseThrow(() -> new DebitCardNotFoundException("No Debit Card found for Account Number: " + accountNumber));
    }


    public void transferFunds(Long debitCardNumber, Long recipientAccountNumber, Double amount, String pin) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }

        DebitCard senderDebitCard = debitCardDaoService.findByDebitCardNumber(debitCardNumber)
                .orElseThrow(() -> new DebitCardNotFoundException("Debit card not found for card number: " + debitCardNumber));

        if (senderDebitCard.getIsBlocked()) {
            throw new DebitCardBlockedException("This debit card is blocked. Transactions cannot be performed.");
        }

        if (!EncryptionUtil.decrypt(senderDebitCard.getPin()).equals(pin)) {
            throw new PinMissMatchException("Invalid PIN.");
        }

        Account senderAccount = senderDebitCard.getAccount();
        if (senderAccount.getAccountNumber().equals(recipientAccountNumber)) {
            throw new IllegalArgumentException("You cannot transfer money to your own account.");
        }

        if (senderAccount.getAccountBalance() < amount) {
            throw new InsufficientFundsException("Insufficient balance in account: " + senderAccount.getAccountNumber());
        }

        Account recipientAccount = accountDaoService.getByAccountNo(recipientAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for account number: " + recipientAccountNumber));

        senderAccount.setAccountBalance(senderAccount.getAccountBalance() - amount);
        recipientAccount.setAccountBalance(recipientAccount.getAccountBalance() + amount);
        accountDaoService.updateBalance(senderAccount);
        accountDaoService.updateBalance(recipientAccount);

        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(amount);
        transaction.setTransactionDatetime(LocalDateTime.now());
        transaction.setTransactionType("Debit");
        transaction.setDebitCard(senderDebitCard);
        debitCardDaoService.addTransaction(debitCardNumber, transaction);
    }

    public static Long generateDebitCardNumber() {
        return Math.abs(random.nextLong() % 10000000000000000L);
    }
}
