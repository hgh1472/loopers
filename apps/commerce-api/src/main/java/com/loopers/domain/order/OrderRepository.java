package com.loopers.domain.order;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findById(UUID id);

    List<Order> findAllByUserId(Long userId);

    List<Order> findCreatedOrdersBefore(ZonedDateTime zonedDateTime);
}
