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
    public List<ProductMetricsInfo> incrementSalesCount(MetricCommand.IncrementSales cmd) {
        List<ProductMetricsInfo> infos = new ArrayList<>();
        for (MetricCommand.SaleLine saleLine : cmd.saleLines()) {
            ProductMetrics productMetrics = productMetricsRepository.findByDailyMetrics(saleLine.productId(), cmd.createdAt())
                    .orElseGet(() -> new ProductMetrics(saleLine.productId(), cmd.createdAt()));
            productMetrics.incrementSalesCount(saleLine.quantity());
            infos.add(ProductMetricsInfo.from(productMetricsRepository.save(productMetrics)));
        }
        return infos;
    }

    @Transactional
    public ProductMetricsInfo incrementViewCount(MetricCommand.IncrementView cmd) {
        ProductMetrics productMetrics = productMetricsRepository.findByDailyMetrics(cmd.productId(), cmd.createdAt())
                .orElseGet(() -> new ProductMetrics(cmd.productId(), cmd.createdAt()));
        productMetrics.incrementViewCount();
        return ProductMetricsInfo.from(productMetricsRepository.save(productMetrics));
    }
}
