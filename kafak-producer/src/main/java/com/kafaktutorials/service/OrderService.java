package com.kafaktutorials.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class OrderService implements CommandLineRunner {

  @Autowired KafkaTemplate kafkaTemplate;

  @Value("${order_create_topic}")
  private String topic;

  @Override
  public void run(String... args) throws Exception {
    String[] names = {
      "Aarav", "Priya", "Marcus", "Sofia", "Elijah", "Yuna", "Tariq", "Ingrid", "Luca", "Amara"
    };

    for (int i = 0; i < 10; i++) {
      CompletableFuture send = kafkaTemplate.send(topic, "My Name is " + names[i] );
    }
  }
}
