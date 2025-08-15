package com.loopers.infrastructure.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.loopers.domain.cache.ProductCacheRepository;
import com.loopers.domain.cache.ProductDetailCache;
import java.time.Duration;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ProductRedisRepository implements ProductCacheRepository {

    private final ProductDetailCacheReader productDetailCacheReader;
    private final ProductDetailCacheWriter productDetailCacheWriter;

    @Override
    public Optional<ProductDetailCache> findProductDetail(String key) {
        try {
            return productDetailCacheReader.readProductCard(key);
        } catch (JsonProcessingException e) {
            log.error("JSON 역직렬화 실패: {}", e.getMessage());
            productDetailCacheWriter.evictProductCard(key);
            return Optional.empty();
        }
    }

    @Override
    public void writeProductDetail(ProductDetailCache productDetailCache, String key, Duration ttl) {
        try {
            productDetailCacheWriter.writeProductCard(productDetailCache, key, ttl);
        } catch (JsonProcessingException e) {
            log.error("JSON 직렬화 실패: {}", e.getMessage());
        }
    }

}
