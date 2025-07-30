package com.loopers.infrastructure.order;

import com.loopers.domain.order.Order;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface OrderJpaRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o join fetch o.orderLines where o.id = :id")
    Optional<Order> findByIdWithOrderLines(Long id);
}
