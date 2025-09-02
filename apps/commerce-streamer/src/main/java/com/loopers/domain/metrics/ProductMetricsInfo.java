package com.loopers.domain.metrics;

public record ProductMetricsInfo(
        Long productId,
        Long likeCount
) {
    public static ProductMetricsInfo from(ProductMetrics productMetrics) {
        return new ProductMetricsInfo(
                productMetrics.getProductId(),
                productMetrics.getLikeCount()
        );
    }
}
