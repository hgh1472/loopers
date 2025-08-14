package com.loopers.domain.cache;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private final ProductCacheRepository productCacheRepository;

    public Optional<ProductDetailCache> findProductDetail(Long productId) {
        String key = String.format(CacheKeys.PRODUCT_DETAIL.key(), productId);
        try {
            return productCacheRepository.findProductDetail(key);
        } catch (RuntimeException e) {
            log.error("캐시 조회 연산 실패: {}", e.getMessage());
            return Optional.empty();
        }
    }

    public void writeProductDetail(CacheCommand.ProductDetail command) {
        ProductDetailCache productDetailCache = ProductDetailCache.from(command);
        String key = String.format(CacheKeys.PRODUCT_DETAIL.key(), command.id());
        try {
            productCacheRepository.writeProductDetail(productDetailCache, key, CacheKeys.PRODUCT_DETAIL.ttl());
        } catch (RuntimeException e) {
            log.error("캐시 쓰기 연산 실패: {}", e.getMessage());
        }
    }
}
