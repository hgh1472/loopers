package com.loopers.application.product;

import com.loopers.domain.brand.BrandCommand;
import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.cache.CacheCommand;
import com.loopers.domain.cache.CacheService;
import com.loopers.domain.cache.ProductDetailCache;
import com.loopers.domain.count.ProductCountCommand;
import com.loopers.domain.count.ProductCountInfo;
import com.loopers.domain.count.ProductCountService;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockInfo;
import com.loopers.domain.stock.StockService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductDetailProcessor {

    private final CacheService cacheService;
    private final ProductService productService;
    private final BrandService brandService;
    private final StockService stockService;
    private final ProductCountService productCountService;

    public ProductResult.ProductDetail getProductDetail(Long productId) {
        ProductInfo productInfo = productService.findProduct(new ProductCommand.Find(productId));
        if (productInfo == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다.");
        }

        Optional<ProductDetailCache> cache = cacheService.findProductDetail(productId);
        if (cache.isPresent()) {
            return ProductResult.ProductDetail.from(cache.get());
        }

        BrandInfo brandInfo = brandService.findBy(new BrandCommand.Find(productInfo.brandId()));
        StockInfo stockInfo = stockService.findStock(new StockCommand.Find(productInfo.id()));
        ProductCountInfo countInfo = productCountService.getProductCount(new ProductCountCommand.Get(productInfo.id()));

        cacheService.writeProductDetail(CacheCommand.ProductDetail.of(productInfo, brandInfo, stockInfo, countInfo));
        return ProductResult.ProductDetail.from(productInfo, brandInfo, stockInfo, countInfo);
    }
}
