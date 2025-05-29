package com.cath.welcome_message_sender.Consumer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendWelcomeMessage(String username, String email, String fullName, String verificationToken) {
        logger.info("Preparing to send welcome email to user: {}", username);

        try {

            String subject = "Welcome to Our Platform!";
            String text = String.format(
                    "Welcome %s!\n\nYour account with username '%s' has been successfully created.\n\n",
                    fullName, username
            );

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject(subject);
            message.setText(text);

            mailSender.send(message);

            logger.info("Welcome email sent successfully to {}", username);

        } catch (Exception e) {
            logger.error("Error sending welcome email for {}: {}", username, e.getMessage());
        }
    }


}