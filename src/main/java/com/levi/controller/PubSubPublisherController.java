package com.levi.controller;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/publish")
public class PubSubPublisherController {

    private static final Logger LOGGER = Logger.getLogger(PubSubPublisherController.class.getName());

    @Autowired
    private PubSubTemplate pubSubTemplate;

    /**
     * Endpoint to publish a message to topic-two.
     *
     * @param message The message to publish
     * @return Response indicating the status of the publishing operation
     */
    @PostMapping
    public String publishMessage(@RequestParam String message) {
        LOGGER.log(Level.INFO, "Received request to publish message: {0}", message);

        // Validate the message content
        if (message == null || message.trim().isEmpty()) {
            LOGGER.log(Level.SEVERE, "Invalid message content: {0}", message);
            return "Failed to publish: message is invalid or empty.";
        }

        // Publish the message to topic-two using CompletableFuture
        LOGGER.log(Level.INFO, "Publishing message to topic-two...");
        CompletableFuture.supplyAsync(() -> pubSubTemplate.publish("topic-two", message))
                .thenAccept(result -> {
                    LOGGER.log(Level.INFO, "Message published successfully to topic-two with ID: {0}", result);
                })
                .exceptionally(ex -> {
                    LOGGER.log(Level.SEVERE, "Failed to publish message to topic-two: {0}", ex.getMessage());
                    return null;
                });

        LOGGER.log(Level.INFO, "Publish request for message: {0} has been initiated.", message);
        return "Message publishing initiated for topic-two.";
    }
}
