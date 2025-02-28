package com.orderservice.service;

import com.orderservice.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class OrderService {

    private static final String ORDER_TOPIC = "order-topic";

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public String createOrder(Order order) {
        // Generate ID, etc.
        String orderId = UUID.randomUUID().toString();
        order.setId(orderId);

        // Publish to Kafka
        kafkaTemplate.send(ORDER_TOPIC, orderId, order);

        return orderId;
    }

    public Order getOrderById(String id) {
        // In a real application, fetch order details from DB
        Order dummyOrder = new Order();
        dummyOrder.setId(id);
        dummyOrder.setDescription("Dummy order from getOrderById()");
        return dummyOrder;
    }
}
