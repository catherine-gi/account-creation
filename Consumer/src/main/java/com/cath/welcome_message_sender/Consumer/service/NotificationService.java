package com.cath.welcome_message_sender.Consumer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private WebClient.Builder webClientBuilder;

    public void sendWelcomeMessage(String username, String email, String fullName) {
        logger.info("Preparing to send welcome message to user: {}", username);

        try {
            // Prepare welcome message payload
            Map<String, Object> payload = new HashMap<>();
            payload.put("recipient", email);
            payload.put("username", username);
            payload.put("fullName", fullName);
            payload.put("subject", "Welcome to Our Platform!");
            payload.put("message", createWelcomeMessage(username, fullName));
            payload.put("messageType", "WELCOME");

            // Call external notification API (simulated)
            WebClient webClient = webClientBuilder.build();

            Mono<String> response = webClient
                    .post()
                    .uri("http://localhost:8083/api/notifications/send")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(result ->
                            logger.info("Welcome message sent successfully to {}: {}", username, result))
                    .doOnError(error ->
                            logger.error("Failed to send welcome message to {}: {}", username, error.getMessage()))
                    .onErrorReturn("Failed to send notification");

            // Subscribe to execute the call
            response.subscribe();

        } catch (Exception e) {
            logger.error("Error preparing welcome message for {}: {}", username, e.getMessage());
        }
    }

    private String createWelcomeMessage(String username, String fullName) {
        return String.format(
                "Welcome %s!" +
                        "Your account with username '%s' has been successfully created. " ,
                fullName, username
        );
    }

    public void sendAccountDeletionNotification(String username, String email, String fullName) {
        logger.info("Preparing to send account deletion notification to user: {}", username);

        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("recipient", email);
            payload.put("username", username);
            payload.put("fullName", fullName);
            payload.put("subject", "Account Deletion Confirmation");
            payload.put("message", createDeletionMessage(username, fullName));
            payload.put("messageType", "ACCOUNT_DELETION");

            WebClient webClient = webClientBuilder.build();

            Mono<String> response = webClient
                    .post()
                    .uri("http://localhost:8083/api/notifications/send")
                    .bodyValue(payload)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(result ->
                            logger.info("Deletion notification sent successfully to {}: {}", username, result))
                    .doOnError(error ->
                            logger.error("Failed to send deletion notification to {}: {}", username, error.getMessage()))
                    .onErrorReturn("Failed to send notification");

            response.subscribe();

        } catch (Exception e) {
            logger.error("Error preparing deletion notification for {}: {}", username, e.getMessage());
        }
    }

    private String createDeletionMessage(String username, String fullName) {
        return String.format(
                "Dear %s,\n\n" +
                        "This is to confirm that your account with username '%s' has been successfully deleted from our platform.\n\n" +
                        "If this was not requested by you, please contact our support team immediately.\n\n" +
                        "Thank you for being part of our community.\n\n" +
                        "Best regards,\n" +
                        "The Platform Team",
                fullName, username
        );
    }
}