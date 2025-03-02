package com.orderservice.dto;

import com.orderservice.model.Order;
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

    /**
     * Item name value.
     */
    private String itemName;

    /**
     * Convert to Order entity.
     *
     * @return Order value
     */
    public Order toOrder() {
        Order order = new Order();
        order.setDescription(description);
        order.setItemName(itemName);
        return order;
    }

}

