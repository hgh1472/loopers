package com.loopers.domain.metrics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MetricsService {
    private final ProductMetricsRepository productMetricsRepository;

    @Transactional
    public ProductMetricsInfo incrementLikeCount(MetricCommand.IncrementLike cmd) {
        ProductMetrics productMetrics = productMetricsRepository.findByDailyMetrics(cmd.productId(), cmd.createdAt())
                .orElseGet(() -> new ProductMetrics(cmd.productId(), cmd.createdAt()));
        productMetrics.incrementLikeCount();
        return ProductMetricsInfo.from(productMetricsRepository.save(productMetrics));
    }

    @Transactional
    public ProductMetricsInfo decrementLikeCount(MetricCommand.DecrementLike cmd) {
        ProductMetrics productMetrics = productMetricsRepository.findByDailyMetrics(cmd.productId(), cmd.createdAt())
                .orElseGet(() -> new ProductMetrics(cmd.productId(), cmd.createdAt()));
        productMetrics.decrementLikeCount();
        return ProductMetricsInfo.from(productMetricsRepository.save(productMetrics));
    }

    @Transactional
    public ProductMetricsInfo incrementSalesCount(MetricCommand.IncrementSales cmd) {
        ProductMetrics productMetrics = productMetricsRepository.findByDailyMetrics(cmd.productId(), cmd.createdAt())
                .orElseGet(() -> new ProductMetrics(cmd.productId(), cmd.createdAt()));
        productMetrics.incrementSalesCount(cmd.quantity());
        return ProductMetricsInfo.from(productMetricsRepository.save(productMetrics));
    }
}
