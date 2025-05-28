package com.cath.welcome_message_sender.Consumer.service;

import com.cath.welcome_message_sender.Consumer.model.AccountEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ObjectMapper objectMapper;

    @KafkaListener(topics = "account-events", groupId = "notification-group")
    public void consumeAccountEvent(
            @Payload Map<String, Object> message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {

        logger.info("Received message from topic: {}, partition: {}, offset: {}", topic, partition, offset);
        logger.info("Message content: {}", message);

        try {
            String json = objectMapper.writeValueAsString(message);
            AccountEvent accountEvent = objectMapper.readValue(json, AccountEvent.class);

            logger.info("Parsed AccountEvent: {}", accountEvent);

            processAccountEvent(accountEvent);

        } catch (Exception e) {
            logger.error("Error processing account event: {}", e.getMessage(), e);
        }
    }

    private void processAccountEvent(AccountEvent event) {
        logger.info("Processing account event of type: {} for user: {}",
                event.getEventType(), event.getUsername());

        switch (event.getEventType()) {
            case "ACCOUNT_CREATED":
                handleAccountCreated(event);
                break;
            case "ACCOUNT_DELETED":
                handleAccountDeleted(event);
                break;
            default:
                logger.warn("Unknown event type: {}", event.getEventType());
        }
    }

    private void handleAccountCreated(AccountEvent event) {
        logger.info("Handling ACCOUNT_CREATED event for user: {}", event.getUsername());

        try {
            logger.info("New account created - ID: {}, Username: {}, Email: {}, Full Name: {}, Timestamp: {}, VerificationToken: {}",
                    event.getAccountId(), event.getUsername(), event.getEmail(),
                    event.getFullName(), event.getTimestamp(), event.getVerificationToken());

            // Pass verificationToken to the notification service
            notificationService.sendWelcomeMessage(
                    event.getUsername(),
                    event.getEmail(),
                    event.getFullName(),
                    event.getVerificationToken()
            );

            logger.info("Account creation processing completed for user: {}", event.getUsername());

        } catch (Exception e) {
            logger.error("Error handling ACCOUNT_CREATED event for user {}: {}",
                    event.getUsername(), e.getMessage());
        }
    }

    private void handleAccountDeleted(AccountEvent event) {
        logger.info("Handling ACCOUNT_DELETED event for user: {}", event.getUsername());

        try {
            logger.info("Account deleted - ID: {}, Username: {}, Email: {}, Full Name: {}, Timestamp: {}",
                    event.getAccountId(), event.getUsername(), event.getEmail(),
                    event.getFullName(), event.getTimestamp());

//            notificationService.sendAccountDeletionNotification(
//                    event.getUsername(),
//                    event.getEmail(),
//                    event.getFullName()
//            );

            logger.info("Account deletion processing completed for user: {}", event.getUsername());

        } catch (Exception e) {
            logger.error("Error handling ACCOUNT_DELETED event for user {}: {}",
                    event.getUsername(), e.getMessage());
        }
    }
}