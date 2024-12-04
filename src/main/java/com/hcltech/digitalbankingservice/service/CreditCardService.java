package com.hcltech.digitalbankingservice.service;

import com.hcltech.digitalbankingservice.dao.AccountDaoService;
import com.hcltech.digitalbankingservice.dao.CreditCardDaoService;
import com.hcltech.digitalbankingservice.dao.TransactionDaoService;
import com.hcltech.digitalbankingservice.dto.CreditCardDto;
import com.hcltech.digitalbankingservice.exception.*;
import com.hcltech.digitalbankingservice.model.Account;
import com.hcltech.digitalbankingservice.model.CreditCard;
import com.hcltech.digitalbankingservice.model.Transaction;
import com.hcltech.digitalbankingservice.util.EncryptionUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CreditCardService {

    private final CreditCardDaoService creditCardDao;
    private final AccountDaoService accountDaoService;
    private final TransactionDaoService transactionDaoService;
    private final ModelMapper modelMapper;
    private static final Random random = new Random();

    @Autowired
    public CreditCardService(CreditCardDaoService creditCardDao, BCryptPasswordEncoder bCryptPasswordEncoder,
                             AccountDaoService accountDaoService, TransactionDaoService transactionDaoService,
                             ModelMapper modelMapper) {
        this.creditCardDao = creditCardDao;
        this.accountDaoService = accountDaoService;
        this.transactionDaoService = transactionDaoService;
        this.modelMapper = modelMapper;
    }

    public List<CreditCardDto> getAll() {
        List<CreditCard> result = creditCardDao.getAll();
        if (result != null && !result.isEmpty()) {
            return result.stream().map(this::toDto).collect(Collectors.toList());
        }
        throw new NoCreditCardFound("No Credit Cards found in DB");
    }

    public Optional<CreditCardDto> getByCreditCardNumber(Long creditCardNumber) {
        Optional<CreditCard> result = creditCardDao.getByNo(creditCardNumber);
        if (result.isPresent()) {
            return Optional.of(toDto(result.get()));
        }
        throw new CreditCardNotFound(creditCardNumber);
    }

    public Optional<CreditCardDto> createCreditCard(Long accountNumber, CreditCardDto creditCardDto) {
        Optional<Account> accountOptional = accountDaoService.getByAccountNo(accountNumber);
        if (accountOptional.isPresent()) {
            Optional<List<CreditCard>> existingCards = creditCardDao.getByAccountNumber(accountNumber);
            if (existingCards.isPresent() && !existingCards.get().isEmpty()) {
                throw new CreditCardAlreadyExistsException("A credit card is already associated with account number: " + accountNumber);
            }
            CreditCard creditCard = toEntity(creditCardDto);
            creditCard.setAccount(accountOptional.get());
            creditCard.setExpDate(LocalDate.now().plusYears(5));
            CreditCard createdCreditCard = creditCardDao.create(creditCard);
            return Optional.of(toDto(createdCreditCard));
        } else {
            throw new AccountNotFoundException("Account not found with account number: " + accountNumber);
        }
    }

    public Optional<CreditCardDto> update(Long creditCardNumber, String oldPin, String newPin) {
        Optional<CreditCard> result = creditCardDao.getByNo(creditCardNumber);
        if (result.isPresent()) {
            CreditCard creditCard = result.get();
            if (EncryptionUtil.decrypt(creditCard.getPin()).equals(oldPin)) {
                creditCard.setPin(EncryptionUtil.encrypt(newPin));
                return Optional.of(toDto(creditCardDao.update(creditCard)));
            } else {
                throw new PinMissMatchException(String.format("CreditCard with pin %s mismatched, update failed.", oldPin));
            }
        }
        throw new CreditCardNotFound(creditCardNumber);
    }

    public Optional<CreditCardDto> updateStatus(long creditCardNumber, String pin, boolean blocked) {
        Optional<CreditCard> result = creditCardDao.getByNo(creditCardNumber);
        if (result.isPresent()) {
            CreditCard creditCard = result.get();
            if (EncryptionUtil.decrypt(creditCard.getPin()).equals(pin)) {
                creditCard.setIsBlocked(blocked);
                return Optional.of(toDto(creditCardDao.update(creditCard)));
            } else {
                throw new PinMissMatchException(String.format("CreditCard with pin %s mismatched, update failed.", pin));
            }
        }
        throw new CreditCardNotFound(creditCardNumber);
    }

    public Optional<CreditCardDto> getCreditCardByAccountNumber(Long accountNumber) {
        List<CreditCard> result = creditCardDao.getAll();
        if (result != null && !result.isEmpty()) {
            Stream<CreditCard> creditCardStream = result.stream()
                    .filter(creditCard -> creditCard.getAccount().getAccountNumber().equals(accountNumber));
            return Optional.of(toDto(creditCardStream.findFirst().orElseThrow(() -> new NoCreditCardWithAccountNumber(accountNumber))));
        }
        throw new NoCreditCardWithAccountNumber(accountNumber);
    }

    public boolean delete(Long creditCardNumber) {
        Optional<CreditCard> result = creditCardDao.getByNo(creditCardNumber);
        if (result.isPresent()) {
            return creditCardDao.delete(creditCardNumber);
        }
        throw new CreditCardNotFound(creditCardNumber);
    }

    public boolean deleteCreditCardByAccountNumber(long accountNumber) {
        List<CreditCard> result = creditCardDao.getAll();
        if (result != null && !result.isEmpty()) {
            Optional<CreditCard> card = result.stream()
                    .filter(creditCard -> creditCard.getAccount().getAccountNumber().equals(accountNumber))
                    .findFirst();
            if (card.isPresent()) {
                return creditCardDao.delete(card.get().getCreditCardNumber());
            }
        }
        throw new NoCreditCardWithAccountNumber(accountNumber);
    }

    public CreditCardDto toDto(CreditCard creditCard) {
        CreditCardDto creditCardDto = modelMapper.map(creditCard, CreditCardDto.class);
        creditCardDto.setPin(EncryptionUtil.decrypt(creditCard.getPin()));
        creditCardDto.setCvv(EncryptionUtil.decrypt(creditCard.getCvv()));
        return creditCardDto;
    }

    public CreditCard toEntity(CreditCardDto creditCardDto) {
        CreditCard creditCard = modelMapper.map(creditCardDto, CreditCard.class);
        creditCard.setCreditCardNumber(generateCreditCardNumber());
        creditCard.setPin(EncryptionUtil.encrypt(creditCardDto.getPin()));
        creditCard.setCvv(EncryptionUtil.encrypt(generateCvv()));
        creditCard.setAvailableLimit(creditCardDto.getCardLimit());
        return creditCard;
    }

    public void transferFunds(Long creditCardNumber, Long recipientAccountNumber, Double amount, String pin) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }

        CreditCard card = creditCardDao.getByNo(creditCardNumber)
                .orElseThrow(() -> new CreditCardNotFound(creditCardNumber));

        if (card.getIsBlocked()) {
            throw new UnsupportedOperationException("CreditCard is Blocked, can't transfer.");
        }

        if (!EncryptionUtil.decrypt(card.getPin()).equals(pin)) {
            throw new PinMissMatchException("Invalid PIN.");
        }

        if (card.getAvailableLimit() < amount) {
            throw new InsufficientFundsException("Available limit is less, can't make transaction: " + card.getCreditCardNumber());
        }

        Account recipientAccount = accountDaoService.getByAccountNo(recipientAccountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for account number: " + recipientAccountNumber));

        card.setAvailableLimit(card.getAvailableLimit() - amount);
        recipientAccount.setAccountBalance(recipientAccount.getAccountBalance() + amount);
        accountDaoService.updateBalance(recipientAccount);

        Transaction transaction = new Transaction();
        transaction.setTransactionAmount(amount);
        transaction.setTransactionDatetime(LocalDateTime.now());
        transaction.setTransactionType("Credit");
        transaction.setCreditCard(card);

        transactionDaoService.saveTransaction(transaction);
        card.addTransaction(transaction);
        creditCardDao.update(card);
    }

    public void transferFundsToAccount(Long creditCardNumber, Long accountNumber, Double amount, int pin) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Transfer amount must be greater than zero.");
        }

        CreditCard card = creditCardDao.getByNo(creditCardNumber)
                .orElseThrow(() -> new CreditCardNotFound(creditCardNumber));

        if (card.getIsBlocked()) {
            throw new UnsupportedOperationException("CreditCard Pin Not matched, can't transfer.");
        }

        Account account = accountDaoService.getByAccountNo(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found for account number: " + accountNumber));

        if (account.getPin() != pin) {
            throw new PinMissMatchException("Pin Matching failed, can't transfer.");
        }

        if (account.getAccountBalance() < amount) {
            throw new UnsupportedOperationException("Account balance is less, can't transfer.");
        }

        card.setAvailableLimit(card.getAvailableLimit() + amount);
        account.setAccountBalance(account.getAccountBalance() - amount);
        accountDaoService.updateBalance(account);
        creditCardDao.update(card);
    }

    private long generateCreditCardNumber() {
        return random.nextLong(1000_0000_0000_0000L, 9999_9999_9999_9999L);
    }

    private String generateCvv() {
        return String.format("%03d", random.nextInt(1000));
    }
}
