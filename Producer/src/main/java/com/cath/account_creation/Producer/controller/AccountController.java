package com.cath.account_creation.Producer.controller;

import com.cath.account_creation.Producer.model.Account;
import com.cath.account_creation.Producer.model.AccountEvent;
import com.cath.account_creation.Producer.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/accounts")
@CrossOrigin(origins = "*")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @PostMapping
    public ResponseEntity<?> createAccount(@Valid @RequestBody Account account) {
        try {
            Account createdAccount = accountService.createAccount(account);
            return new ResponseEntity<>(createdAccount, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(
                    new ErrorResponse("Error creating account: " + e.getMessage()),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAllAccounts() {
        List<Account> accounts = accountService.getAllAccounts();
        return new ResponseEntity<>(accounts, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAccountById(@PathVariable String id) {
        Optional<Account> account = accountService.getAccountById(id);
        if (account.isPresent()) {
            return new ResponseEntity<>(account.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    new ErrorResponse("Account not found with ID: " + id),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<?> getAccountByUsername(@PathVariable String username) {
        Optional<Account> account = accountService.getAccountByUsername(username);
        if (account.isPresent()) {
            return new ResponseEntity<>(account.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(
                    new ErrorResponse("Account not found with username: " + username),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAccount(@PathVariable String id) {
        try {
            accountService.deleteAccount(id);
            return new ResponseEntity<>(
                    new SuccessResponse("Account deleted successfully"),
                    HttpStatus.OK
            );
        } catch (RuntimeException e) {
            return new ResponseEntity<>(
                    new ErrorResponse("Error deleting account: " + e.getMessage()),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    // Inner classes for response DTOs
    public static class ErrorResponse {
        private String message;
        private long timestamp;

        public ErrorResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }

    public static class SuccessResponse {
        private String message;
        private long timestamp;

        public SuccessResponse(String message) {
            this.message = message;
            this.timestamp = System.currentTimeMillis();
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}