package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductScheduler {

    private final ProductCacheWriter productCacheWriter;
    private final ProductRepository productRepository;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void refreshCountCache() {
        Long count = productRepository.countBrandProducts(null);
        productCacheWriter.writeAllProductCount(count);
    }
}
