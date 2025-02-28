package com.payments.model;

import lombok.Data;

@Data
public class Payment {
    private String paymentId;
    private double amount;
    private String orderData;

    @Override
    public String toString() {
        return String.format("Payment[paymentId=%s, amount=%.2f, orderData=%s]",
                paymentId, amount, orderData);
    }
}
