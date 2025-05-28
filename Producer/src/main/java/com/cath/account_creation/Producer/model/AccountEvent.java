package com.cath.account_creation.Producer.model;

import java.time.LocalDateTime;

public class AccountEvent {
    private String eventId;
    private String eventType;
    private String accountId;
    private String username;
    private String email;
    private String fullName;
    private LocalDateTime timestamp;

    public AccountEvent() {
        this.timestamp = LocalDateTime.now();
    }

    public AccountEvent(String eventType, Account account) {
        this();
        this.eventType = eventType;
        this.accountId = account.getId();
        this.username = account.getUsername();
        this.email = account.getEmail();
        this.fullName = account.getFullName();
        this.eventId = "EVT-" + System.currentTimeMillis();
    }

    // Getters and Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return "AccountEvent{" +
                "eventId='" + eventId + '\'' +
                ", eventType='" + eventType + '\'' +
                ", accountId='" + accountId + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", fullName='" + fullName + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}