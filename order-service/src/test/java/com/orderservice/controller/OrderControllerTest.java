package com.orderservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.orderservice.dto.OrderRequest;
import com.orderservice.model.Order;
import com.orderservice.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Integration test for {@link OrderController} using @SpringBootTest.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc; // Used to send HTTP requests to the controller

    @Autowired
    private ObjectMapper objectMapper; // Used to convert Java objects to JSON

    @Autowired
    private OrderRepository orderRepository; // Direct DB access for validation

    @BeforeEach
    void setup() {
        orderRepository.deleteAll(); // Ensure DB is clean before each test
    }

    /**
     * Test case: creates an order.
     */
    @Test
    void testCreateOrder() throws Exception {
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setDescription("Test Order");
        orderRequest.setItemName("Order Name");

        // When: Sending POST request to create a new order
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                // Then: Expect HTTP 201 (Created)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.externalId", notNullValue()))
                .andExpect(jsonPath("$.description").value("Test Order"));
    }

    /**
     * Test case: Attempts to create an order with an empty description.
     * Expected: HTTP 400 Bad Request.
     */
    @Test
    void shouldReturnBadRequestWhenCreatingOrderWithEmptyDescription() throws Exception {
        // Given: An invalid order request (empty description)
        OrderRequest orderRequest = new OrderRequest();
        orderRequest.setDescription("");

        // When: Sending POST request with invalid data
        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(orderRequest)))
                // Then: Expect HTTP 400 Bad Request
                .andExpect(status().isBadRequest());
    }

    /**
     * Test case: Retrieves an existing order.
     * Expected: HTTP 200 OK, and correct order details.
     */
    @Test
    void shouldGetOrderSuccessfully() throws Exception {
        // Given: A saved order
        Order order = new Order();
        order.setExternalId(UUID.randomUUID().toString());
        order.setDescription("Existing Order");
        order.setItemName("Order Name");
        orderRepository.save(order);

        // When: Sending GET request to fetch the order
        mockMvc.perform(get("/orders/{externalId}", order.getExternalId()))
                // Then: Expect HTTP 200 OK and correct JSON response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.externalId").value(order.getExternalId()))
                .andExpect(jsonPath("$.description").value("Existing Order"));
    }

    /**
     * Test case: Attempts to retrieve a non-existent order.
     * Expected: HTTP 404 Not Found.
     */
    @Test
    void shouldReturnNotFoundForNonExistingOrder() throws Exception {
        // Given: A non-existent order ID
        String nonExistentId = UUID.randomUUID().toString();

        // When: Sending GET request for a non-existent order
        mockMvc.perform(get("/orders/{externalId}", nonExistentId))
                // Then: Expect HTTP 404 Not Found
                .andExpect(status().isNotFound());
    }
}
