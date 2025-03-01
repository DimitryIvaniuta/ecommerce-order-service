package com.orderservice.dto;

import lombok.Data;

/**
 * Data Transfer Object for creating an order.
 */
@Data
public class OrderRequest {

    /**
     * The description for the new order.
     */
    private String description;

}

