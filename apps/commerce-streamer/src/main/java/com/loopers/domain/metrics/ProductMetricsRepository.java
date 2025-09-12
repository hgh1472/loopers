package com.loopers.domain.metrics;

import java.time.LocalDate;
import java.util.Optional;

public interface ProductMetricsRepository {
    ProductMetrics save(ProductMetrics productMetrics);

    Optional<ProductMetrics> findByDailyMetrics(Long productId, LocalDate date);

    Optional<ProductMetrics> findByDailyMetricsWithLock(Long productId, LocalDate date);
}
