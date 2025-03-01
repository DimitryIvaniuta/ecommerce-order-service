package com.orderservice.repository;

import com.orderservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing Order entities.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Finds an order by its unique external identifier.
     *
     * @param externalId the unique external identifier of the order.
     * @return an Optional containing the found Order if present.
     */
    Optional<Order> findByExternalId(String externalId);
}
