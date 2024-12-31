//package com.levi.consumer;
//
//import com.google.api.gax.rpc.NotFoundException;
//import com.google.cloud.pubsub.v1.SubscriptionAdminClient;
//import com.google.pubsub.v1.ProjectSubscriptionName;
//import com.google.pubsub.v1.ProjectTopicName;
//import com.google.pubsub.v1.Subscription;
//import com.google.cloud.spring.pubsub.core.PubSubTemplate;
//import com.google.cloud.spring.pubsub.integration.inbound.PubSubInboundChannelAdapter;
//import com.google.cloud.spring.pubsub.support.AcknowledgeablePubsubMessage;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.integration.annotation.ServiceActivator;
//import org.springframework.integration.channel.DirectChannel;
//import org.springframework.integration.core.MessageProducer;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.messaging.MessageHandler;
//
//import java.io.IOException;
//import java.util.concurrent.CompletableFuture;
//
//@Configuration
//public class PubSubConsumer {
//    private static final Logger logger = LoggerFactory.getLogger(PubSubConsumer.class);
//
//    private final PubSubTemplate pubSubTemplate;
//
//    @Value("${output.pubsub.topic}")
//    private String topic;
//
//    @Value("${spring.cloud.gcp.project-id}")
//    private String projectId;
//
//    @Value("${spring.cloud.gcp.pubsub.subscription-name}")
//    private String subscriptionId;
//
//    @Value("${spring.cloud.gcp.pubsub.topic-name}")
//    private String topicId;
//
//    public PubSubConsumer(PubSubTemplate pubSubTemplate) {
//        this.pubSubTemplate = pubSubTemplate;
//    }
//
//    @Bean
//    public MessageChannel inputMessageChannel() {
//        return new DirectChannel();
//    }
//
//    @Bean
//    public MessageProducer pubSubInboundChannelAdapter(MessageChannel inputMessageChannel) throws IOException {
//        createSubscriptionIfNotExists();
//        PubSubInboundChannelAdapter adapter = new PubSubInboundChannelAdapter(pubSubTemplate, subscriptionId);
//        adapter.setOutputChannel(inputMessageChannel);
//        return adapter;
//    }
//
//    private void createSubscriptionIfNotExists() throws IOException {
//        try (SubscriptionAdminClient subscriptionAdminClient = SubscriptionAdminClient.create()) {
//            ProjectSubscriptionName subscriptionName = ProjectSubscriptionName.of(projectId, subscriptionId);
//            ProjectTopicName topicName = ProjectTopicName.of(projectId, topicId);
//
//            try {
//                subscriptionAdminClient.getSubscription(subscriptionName);
//                logger.info("Subscription {} already exists", subscriptionName);
//            } catch (NotFoundException e) {
//                Subscription subscription = subscriptionAdminClient.createSubscription(
//                        Subscription.newBuilder()
//                                .setName(subscriptionName.toString())
//                                .setTopic(topicName.toString())
//                                .setEnableMessageOrdering(true)
//                                .build());
//                logger.info("Created a subscription with ordering: {}", subscription.getAllFields());
//            }
//        }
//    }
//
//    @Bean
//    @ServiceActivator(inputChannel = "inputMessageChannel")
//    public MessageHandler messageReceiver() {
//        return message -> {
//            String payload = new String((byte[]) message.getPayload());
//            logger.info("Message arrived! Payload: {}", payload);
//            try {
//                CompletableFuture<String> completable = pubSubTemplate.publish(topic, payload).completable();
//                completable.thenAccept(messageId -> logger.info("Message published. Message ID: {}", messageId));
//            } catch (Exception e) {
//                logger.error("Error processing message", e);
//            }
//        };
//    }
//}
