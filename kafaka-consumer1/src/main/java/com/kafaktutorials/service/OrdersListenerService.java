package com.kafaktutorials.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrdersListenerService {

  @KafkaListener(topics = "orders", groupId = "order-notification")
  public void consumeOrders(String order) {
    System.out.println(order);
  }
}
