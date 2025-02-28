package com.payments.consumer;

import com.payments.model.Payment;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OrderEventConsumer {

    @KafkaListener(topics = "order-topic", groupId = "payment-service-group")
    public void listenOrderEvents(Object orderEvent) {

        log.info("Received new Order event: {}", orderEvent);
        // Parse 'orderEvent' into Order model
        // Create a Payment record
        Payment payment = new Payment();
        payment.setPaymentId("PAY-" + System.currentTimeMillis());
        payment.setOrderData(orderEvent.toString());
        payment.setAmount(100.00);
        // Save to DB: Firestore
        // paymentRepository.save(payment);

    }

}