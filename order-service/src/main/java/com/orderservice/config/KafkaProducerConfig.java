package com.orderservice.config;

import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Map;

/**
 * Configuration class for Kafka Producer.
 *
 * This configuration leverages Spring Boot's auto-configuration by injecting the
 * KafkaProperties bean, which is populated from the external configuration in application.yml.
 * It sets up a ProducerFactory and a KafkaTemplate for sending messages.
 */
@Configuration
public class KafkaProducerConfig {

    private final KafkaProperties kafkaProperties;

    /**
     * Constructor that injects the KafkaProperties bean.
     *
     * @param kafkaProperties the Kafka properties loaded from the application configuration.
     */
    public KafkaProducerConfig(KafkaProperties kafkaProperties) {
        this.kafkaProperties = kafkaProperties;
    }

    /**
     * Creates a ProducerFactory that uses the properties defined in application.yml.
     *
     * @return the ProducerFactory configured for Kafka producers.
     */
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> configProps = kafkaProperties.buildProducerProperties();
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    /**
     * Creates a KafkaTemplate for sending messages.
     *
     * @return the KafkaTemplate configured with the ProducerFactory.
     */
    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

}
