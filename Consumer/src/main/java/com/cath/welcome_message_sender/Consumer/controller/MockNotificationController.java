package com.cath.welcome_message_sender.Consumer.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
public class MockNotificationController {

    private static final Logger logger = LoggerFactory.getLogger(MockNotificationController.class);

    @PostMapping("/send")
    public ResponseEntity<?> sendNotification(@RequestBody Map<String, Object> payload) {
        try {
            String recipient = (String) payload.get("recipient");
            String username = (String) payload.get("username");
            String subject = (String) payload.get("subject");
            String messageType = (String) payload.get("messageType");

            logger.info("=== MOCK NOTIFICATION API ===");
            logger.info("Sending {} notification to: {}", messageType, recipient);
            logger.info("Username: {}", username);
            logger.info("Subject: {}", subject);
            logger.info("Message: {}", payload.get("message"));
            logger.info("============================");

            // Simulate some processing time
            Thread.sleep(100);

            return ResponseEntity.ok(new NotificationResponse(
                    "SUCCESS",
                    "Notification sent successfully to " + recipient,
                    System.currentTimeMillis()
            ));

        } catch (Exception e) {
            logger.error("Error sending notification: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new NotificationResponse(
                            "ERROR",
                            "Failed to send notification: " + e.getMessage(),
                            System.currentTimeMillis()
                    ));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Mock Notification Service is running");
    }

    // Response DTO
    public static class NotificationResponse {
        private String status;
        private String message;
        private long timestamp;

        public NotificationResponse(String status, String message, long timestamp) {
            this.status = status;
            this.message = message;
            this.timestamp = timestamp;
        }

        // Getters and setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }

        public long getTimestamp() { return timestamp; }
        public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    }
}