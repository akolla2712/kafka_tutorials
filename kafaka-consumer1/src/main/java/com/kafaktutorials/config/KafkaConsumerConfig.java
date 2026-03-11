package com.kafaktutorials.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConsumerConfig {

  @Value("${bootstrap.server}")
  private String bootstrapServer;

  @Bean
  public ConsumerFactory<String, Object> consumerFactory() {
    Map<String, Object> properties = new HashMap<>();
    properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
    properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    properties.put(ConsumerConfig.GROUP_ID_CONFIG, "order-notification");
    properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    return new DefaultKafkaConsumerFactory<>(properties);
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, Object>
        stringObjectConcurrentKafkaListenerContainerFactory =
            new ConcurrentKafkaListenerContainerFactory<>();
    stringObjectConcurrentKafkaListenerContainerFactory.setConsumerFactory(consumerFactory());
    return stringObjectConcurrentKafkaListenerContainerFactory;
  }
}
