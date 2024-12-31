package com.levi.config;

import com.google.cloud.spring.pubsub.core.PubSubTemplate;
import com.google.cloud.spring.pubsub.integration.AckMode;
import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
//import org.springframework.cloud.gcp.pubsub.integration.AckMode;
//import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;
import java.util.logging.Logger;

@Configuration
public class PubSubConfiguration {

    private static final Logger LOGGER = Logger.getLogger(PubSubConfiguration.class.getName());

    @Autowired
    private PubSubTemplate pubSubTemplate;

    @Bean
    public PubSubInboundChannelAdapter messageChannelAdapter(PubSubMessageReceiver receiver) {
        PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(pubSubTemplate, "subscription-one");
        adapter.setOutputChannel(receiver.messageChannel());
        adapter.setAckMode(AckMode.MANUAL);
        return adapter;
    }
}

@Component
class PubSubMessageReceiver {

    private static final Logger LOGGER = Logger.getLogger(PubSubMessageReceiver.class.getName());

    @Autowired
    private PubSubTemplate pubSubTemplate;

    @Bean
    public MessageChannel messageChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageHandler messageHandler() {
        return message -> {
            String payload = (String) message.getPayload();
            LOGGER.log(Level.INFO, "Received message from topic-one: {0}", payload);

            CompletableFuture.supplyAsync(() -> pubSubTemplate.publish("topic-two", payload))
                    .thenAccept(result -> LOGGER.log(Level.INFO, "Message published to topic-two with ID: {0}", result))
                    .exceptionally(ex -> {
                        LOGGER.log(Level.SEVERE, "Failed to publish message to topic-two: {0}", ex.getMessage());
                        return null;
                    });
        };
    }
}
