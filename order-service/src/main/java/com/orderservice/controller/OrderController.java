package com.orderservice.controller;

import com.orderservice.dto.OrderRequest;
import com.orderservice.dto.OrderResponse;
import com.orderservice.model.Order;
import com.orderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing orders.
 *
 * This controller exposes endpoints for creating new orders and retrieving existing orders by their external ID.
 * It delegates business logic to the OrderService.
 */
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    /**
     * The service responsible for order-related business logic.
     */
    private final OrderService orderService;

    /**
     * Creates a new order based on the provided order details.
     *
     * The incoming order's description is used to create a new order. The order is saved in the database
     * and an event is published to Kafka. The endpoint returns the generated external ID for the order.
     *
     * @param orderRequest the request DTO containing order details.
     * @return the response DTO containing the created order details.
     */
    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest orderRequest) {
        log.info("Received request to create order: {}", orderRequest);
        String externalId = orderService.createOrder(orderRequest.getDescription());
        Order order = orderService.getOrder(externalId);
        OrderResponse response = mapToOrderResponse(order);
        log.info("Order created with externalId: {}", externalId);
        return response;
    }

    /**
     * Retrieves an order by its external ID.
     *
     * @param externalId the unique external identifier for the order.
     * @return the response DTO containing the order details, or null if not found.
     */
    @GetMapping("/{externalId}")
    public OrderResponse getOrder(@PathVariable String externalId) {
        log.info("Received request to get order with externalId: {}", externalId);
        Order order = orderService.getOrder(externalId);
        if (order == null) {
            log.warn("Order with externalId {} not found", externalId);
            return null;
        }
        return mapToOrderResponse(order);
    }

    /**
     * Maps an Order entity to an OrderResponse DTO.
     *
     * @param order the Order entity.
     * @return the corresponding OrderResponse DTO.
     */
    private OrderResponse mapToOrderResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setExternalId(order.getExternalId());
        response.setDescription(order.getDescription());
        response.setCreatedAt(order.getCreatedAt());
        response.setUpdatedAt(order.getUpdatedAt());
        return response;
    }

}
