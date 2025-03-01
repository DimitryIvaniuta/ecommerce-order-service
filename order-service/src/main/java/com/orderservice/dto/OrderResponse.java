package com.orderservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing order details.
 */
@Data
public class OrderResponse {

    /**
     * The unique external identifier of the order.
     */
    private String externalId;

    /**
     * The description of the order.
     */
    private String description;

    /**
     * The timestamp when the order was created.
     */
    private LocalDateTime createdAt;

    /**
     * The timestamp when the order was last updated.
     */
    private LocalDateTime updatedAt;

}
