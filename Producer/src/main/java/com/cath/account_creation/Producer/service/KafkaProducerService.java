package com.cath.account_creation.Producer.service;


import com.cath.account_creation.Producer.model.AccountEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducerService.class);
    private static final String TOPIC = "account-events";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void sendAccountEvent(AccountEvent event) {
        try {
            CompletableFuture<SendResult<String, Object>> future =
                    kafkaTemplate.send(TOPIC, event.getAccountId(), event);

            future.whenComplete((result, exception) -> {
                if (exception == null) {
                    logger.info("Sent account event=[{}] with offset=[{}]",
                            event.toString(), result.getRecordMetadata().offset());
                } else {
                    logger.error("Unable to send account event=[{}] due to : {}",
                            event.toString(), exception.getMessage());
                }
            });
        } catch (Exception e) {
            logger.error("Error sending account event: {}", e.getMessage());
            throw new RuntimeException("Failed to send account event", e);
        }
    }
}