package com.loopers.domain.cache;

import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.count.ProductCountInfo;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.stock.StockInfo;
import java.math.BigDecimal;

public class CacheCommand {
    public record ProductDetail(
            Long id,
            String brandName,
            String productName,
            BigDecimal price,
            String status,
            Long quantity,
            Long likeCount
    ) {
        public static ProductDetail of(ProductInfo productInfo, BrandInfo brandInfo, StockInfo stockInfo,
                                       ProductCountInfo productCountInfo) {
            return new ProductDetail(
                    productInfo.id(),
                    brandInfo.name(),
                    productInfo.name(),
                    productInfo.price(),
                    productInfo.status(),
                    stockInfo.quantity(),
                    productCountInfo.likeCount()
            );
        }
    }
}
