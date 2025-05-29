package com.cath.account_creation.Producer.service;


import com.cath.account_creation.Producer.model.Account;
import com.cath.account_creation.Producer.model.AccountEvent;
import com.cath.account_creation.Producer.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountService {

    private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    public Account createAccount(Account account) {
        logger.info("Creating account for username: {}", account.getUsername());

        // Check if username or email already exists
        if (accountRepository.existsByUsername(account.getUsername())) {
            throw new RuntimeException("Username already exists: " + account.getUsername());
        }

        if (accountRepository.existsByEmail(account.getEmail())) {
            throw new RuntimeException("Email already exists: " + account.getEmail());
        }
        // Generate verification token
        String token = UUID.randomUUID().toString();
        account.setVerificationToken(token);
        account.setEmailVerified(false);

        // Save account to MongoDB
        Account savedAccount = accountRepository.save(account);
        logger.info("Account created successfully with ID: {}", savedAccount.getId());

        // Send event to Kafka
        AccountEvent event = new AccountEvent("ACCOUNT_CREATED", savedAccount);
        kafkaProducerService.sendAccountEvent(event);
        logger.info("Account creation event sent to Kafka for user: {}", savedAccount.getUsername());

        return savedAccount;
    }

    public List<Account> getAllAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> getAccountById(String id) {
        return accountRepository.findById(id);
    }

    public Optional<Account> getAccountByUsername(String username) {
        return accountRepository.findByUsername(username);
    }

    public void deleteAccount(String id) {
        Optional<Account> account = accountRepository.findById(id);
        if (account.isPresent()) {
            accountRepository.deleteById(id);
            logger.info("Account deleted with ID: {}", id);

            // Send deletion event to Kafka
            AccountEvent event = new AccountEvent("ACCOUNT_DELETED", account.get());
            kafkaProducerService.sendAccountEvent(event);
        } else {
            throw new RuntimeException("Account not found with ID: " + id);
        }
    }
}
