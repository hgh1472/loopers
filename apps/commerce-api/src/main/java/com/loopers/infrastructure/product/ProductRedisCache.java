package com.loopers.infrastructure.product;

import com.loopers.domain.product.ProductCache;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ProductRedisCache implements ProductCache {

    private final ProductCacheReader productCacheReader;
    private final ProductCacheWriter productCacheWriter;

    @Override
    public void writeProductCount(Long brandId, Long count) {
        try {
            if (brandId != null) {
                productCacheWriter.writeBrandProductCount(brandId, count);
                return;
            }
            productCacheWriter.writeAllProductCount(count);
        } catch (RuntimeException e) {
            log.error("레디스 쓰기 연산 실패: {}", e.getMessage());
        }
    }

    @Override
    public void writeAllProductCount(Long count) {
        try {
            productCacheWriter.writeAllProductCount(count);
        } catch (RuntimeException e) {
            log.error("레디스 쓰기 연산 실패: {}", e.getMessage());
        }
    }

    @Override
    public Optional<Long> readProductCount(Long brandId) {
        try {
            return (brandId != null) ? productCacheReader.getBrandProductCount(brandId) : productCacheReader.getAllProductCount();
        } catch (RuntimeException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Long> readAllProductCount() {
        try {
            return productCacheReader.getAllProductCount();
        } catch (RuntimeException e) {
            log.error("레디스 읽기 연산 실패: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
