package com.loopers.domain.product;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductCountProcessor {
    private final ProductRepository productRepository;
    private final ProductCache productCache;

    public Long getProductCount(Long brandId) {
        Optional<Long> cachedCount =
                (brandId == null) ? productCache.readAllProductCount() : productCache.readProductCount(brandId);

        if (cachedCount.isEmpty()) {
            Long count;
            if (brandId == null) {
                count = productRepository.countAllProducts();
                productCache.writeAllProductCount(count);
            } else {
                count = productRepository.countBrandProducts(brandId);
                productCache.writeProductCount(brandId, count);
            }
            cachedCount = Optional.of(count);
        }

        return cachedCount.get();
    }

    public void updateCache() {
        Long count = productRepository.countAllProducts();
        productCache.writeAllProductCount(count);
    }
}
