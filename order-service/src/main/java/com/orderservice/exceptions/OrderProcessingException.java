package com.orderservice.exceptions;

/**
 * Custom exception for errors during order processing.
 */
public class OrderProcessingException extends RuntimeException {

    public OrderProcessingException(String message, Throwable cause) {
        super(message, cause);
    }

}