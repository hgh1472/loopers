package com.loopers.domain.metrics;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MetricsService {
    private final ProductMetricsRepository productMetricsRepository;

    @Transactional
    public ProductMetricsInfo incrementLikeCount(MetricCommand.IncrementLikes cmd) {
        ProductMetrics productMetrics = productMetricsRepository.findByDailyMetrics(cmd.productId(), cmd.createdAt())
                .orElseGet(() -> new ProductMetrics(cmd.productId(), cmd.createdAt()));
        productMetrics.incrementLikeCount(cmd.count());
        return ProductMetricsInfo.from(productMetricsRepository.save(productMetrics));
    }

    @Transactional
    public List<ProductMetricsInfo> incrementLikeCounts(List<MetricCommand.IncrementLikes> cmds) {
        return cmds.stream()
                .map(this::incrementLikeCount)
                .toList();
    }

    @Transactional
    public ProductMetricsInfo decrementLikeCount(MetricCommand.DecrementLikes cmd) {
        ProductMetrics productMetrics = productMetricsRepository.findByDailyMetrics(cmd.productId(), cmd.createdAt())
                .orElseGet(() -> new ProductMetrics(cmd.productId(), cmd.createdAt()));
        productMetrics.decrementLikeCount(cmd.count());
        return ProductMetricsInfo.from(productMetricsRepository.save(productMetrics));
    }

    @Transactional
    public List<ProductMetricsInfo> decrementLikeCounts(List<MetricCommand.DecrementLikes> cmds) {
        return cmds.stream()
                .map(this::decrementLikeCount)
                .toList();
    }

    @Transactional
    public ProductMetricsInfo incrementSalesCount(MetricCommand.IncrementSales cmd) {
        List<ProductMetricsInfo> infos = new ArrayList<>();
        ProductMetrics productMetrics = productMetricsRepository.findByDailyMetrics(cmd.productId(), cmd.createdAt())
                .orElseGet(() -> new ProductMetrics(cmd.productId(), cmd.createdAt()));
        productMetrics.incrementSalesCount(cmd.quantity());
        return ProductMetricsInfo.from(productMetricsRepository.save(productMetrics));
    }

    @Transactional
    public List<ProductMetricsInfo> incrementSalesCounts(List<MetricCommand.IncrementSales> cmds) {
        return cmds.stream()
                .map(this::incrementSalesCount)
                .toList();
    }

    @Transactional
    public ProductMetricsInfo incrementViewCount(MetricCommand.IncrementView cmd) {
        ProductMetrics productMetrics = productMetricsRepository.findByDailyMetrics(cmd.productId(), cmd.createdAt())
                .orElseGet(() -> new ProductMetrics(cmd.productId(), cmd.createdAt()));
        productMetrics.incrementViewCount();
        return ProductMetricsInfo.from(productMetricsRepository.save(productMetrics));
    }
}
