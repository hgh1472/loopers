package com.loopers.domain.metrics;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MetricService {
    private final ProductMetricsRepository productMetricsRepository;

    @Transactional
    public ProductMetricsInfo incrementsLikeCount(MetricCommand.IncrementLike cmd) {
        ProductMetrics productMetrics = productMetricsRepository.findByProductId(cmd.productId())
                        .orElseGet(() -> new ProductMetrics(cmd.productId()));
        productMetrics.incrementLikeCount();
        return ProductMetricsInfo.from(productMetricsRepository.save(productMetrics));
    }

    @Transactional
    public ProductMetricsInfo decrementsLikeCount(MetricCommand.DecrementLike cmd) {
        ProductMetrics productMetrics = productMetricsRepository.findByProductId(cmd.productId())
                        .orElseGet(() -> new ProductMetrics(cmd.productId()));
        productMetrics.decrementLikeCount();
        return ProductMetricsInfo.from(productMetricsRepository.save(productMetrics));
    }
}
