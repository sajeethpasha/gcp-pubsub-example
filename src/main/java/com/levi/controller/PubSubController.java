//package com.levi.controller;
//
//import com.levi.service.PubSubService;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//@RestController
//public class PubSubController {
//    private final PubSubService pubSubService;
//
//    public PubSubController(PubSubService pubSubService) {
//        this.pubSubService = pubSubService;
//    }
//
//    @GetMapping("/start-subscription")
//    public String startSubscription() {
//        pubSubService.subscribeToTopicOne("subscription-one");
//        return "Subscription started for topic-one";
//    }
//}
