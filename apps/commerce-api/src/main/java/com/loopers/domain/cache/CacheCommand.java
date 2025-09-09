package com.loopers.domain.cache;

import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.count.ProductCountInfo;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.ranking.RankingInfo;
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
            Long likeCount,
            Long rank
    ) {
        public static ProductDetail of(ProductInfo productInfo, BrandInfo brandInfo, StockInfo stockInfo,
                                       ProductCountInfo productCountInfo, RankingInfo rankingInfo) {
            return new ProductDetail(
                    productInfo.id(),
                    brandInfo.name(),
                    productInfo.name(),
                    productInfo.price(),
                    productInfo.status(),
                    stockInfo.quantity(),
                    productCountInfo.likeCount(),
                    rankingInfo.rank()
            );
        }
    }
}
