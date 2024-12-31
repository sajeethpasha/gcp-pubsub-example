package com.levi.service;

import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.pubsub.v1.PubsubMessage;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class PubSubService {

    private static final Logger LOGGER = Logger.getLogger(PubSubService.class.getName());

    private final PubSubTemplate pubSubTemplate;

    public PubSubService(PubSubTemplate pubSubTemplate) {
        this.pubSubTemplate = pubSubTemplate;
    }

    public void subscribeToTopicOne(String subscriptionName) {
        pubSubTemplate.subscribe(subscriptionName, (BasicAcknowledgeablePubsubMessage message) -> {
            String data = message.getPubsubMessage().getData().toStringUtf8();
            LOGGER.log(Level.INFO, "Message received from topic-one: {0}", data);

            try {
                publishToTopicTwo(data);
                message.ack();
                LOGGER.log(Level.INFO, "Message acknowledged successfully.");
            } catch (Exception ex) {
                LOGGER.log(Level.SEVERE, "Failed to process message: {0}", ex.getMessage());
                message.nack();
            }
        });
    }

    public void publishToTopicTwo(String data) {
        CompletableFuture.supplyAsync(() -> pubSubTemplate.publish("topic-two", data))
                .thenAccept(result -> {
                    LOGGER.log(Level.INFO, "Message published to topic-two with ID: {0}", result);
                })
                .exceptionally(ex -> {
                    LOGGER.log(Level.SEVERE, "Failed to publish message to topic-two: {0}", ex.getMessage());
                    return null;
                });
    }
}
