package com.orderservice.service;

import com.orderservice.exceptions.OrderProcessingException;
import com.orderservice.model.Order;
import com.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Service class for managing orders.
 *
 * This service handles creating new orders, persisting them to the database,
 * and publishing order events to Kafka.
 */
@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    private static final String ORDER_TOPIC = "order-topic";

    private final OrderRepository orderRepository;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * Constructs an OrderService with the necessary dependencies.
     *
     * @param orderRepository the repository used to persist orders.
     * @param kafkaTemplate   the KafkaTemplate used to publish order events.
     */
    public OrderService(OrderRepository orderRepository, KafkaTemplate<String, Object> kafkaTemplate) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Creates a new order with the specified description.
     *
     * A unique external ID is generated for the order. The order is then saved to the database,
     * and an event is published to the Kafka topic for further processing.
     *
     * @param order value.
     * @return the generated external ID of the created order.
     */
    public String createOrder(Order order) {
        // Generate a unique external ID for the order
        String externalId = UUID.randomUUID().toString();

        // Create the Order entity
        order.setExternalId(externalId);


        try {
            // Save the order in the database
            orderRepository.save(order);
            logger.info("Order saved: externalId={}, description={}", externalId, order.getDescription());
        } catch (Exception e) {
            logger.error("Error saving order with externalId {}: {}", externalId, e.getMessage());
            throw new OrderProcessingException("Error saving order with externalId " + externalId, e);
        }

        try {
            // Publish the order event to Kafka
            kafkaTemplate.send(ORDER_TOPIC, externalId, order);
            logger.info("Order event published to Kafka for externalId={}", externalId);
        } catch (Exception e) {
            logger.error("Error publishing Kafka event for order with externalId {}: {}", externalId, e.getMessage());
            throw new OrderProcessingException("Error publishing Kafka event for order with externalId " + externalId, e);
        }

        return externalId;
    }

    /**
     * Retrieves an order by its external ID.
     *
     * @param externalId the unique external identifier for the order.
     * @return the {@link Order} if found; otherwise, returns {@code null}.
     */
    public Order getOrder(String externalId) {
        return orderRepository.findByExternalId(externalId).orElse(null);
    }
}
