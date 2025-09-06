package com.loopers.infrastructure.metrics;

import com.loopers.domain.metrics.ProductMetrics;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductMetricsJpaRepository extends JpaRepository<ProductMetrics, Long> {
    Optional<ProductMetrics> findByProductIdAndDate(Long productId, LocalDate date);
}
