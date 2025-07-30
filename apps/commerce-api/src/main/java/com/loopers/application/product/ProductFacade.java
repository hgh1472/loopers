package com.loopers.application.product;

import com.loopers.domain.brand.BrandCommand;
import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.like.ProductLikeCommand;
import com.loopers.domain.like.ProductLikeService;
import com.loopers.domain.product.ProductCommand.Find;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockInfo;
import com.loopers.domain.stock.StockService;
import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductService productService;

    private final BrandService brandService;

    private final ProductLikeService productLikeService;

    private final StockService stockService;

    private final UserService userService;

    @Transactional(readOnly = true)
    public ProductResult getProduct(ProductCriteria.Get criteria) {
        ProductInfo productInfo = productService.findProduct(new Find(criteria.productId()));
        if (productInfo == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다.");
        }
        BrandInfo brandInfo = brandService.findBy(new BrandCommand.Find(productInfo.brandId()));
        StockInfo stockInfo = stockService.findStock(new StockCommand.Find(productInfo.id()));
        Long likeCount = productLikeService.countLikes(new ProductLikeCommand.Count(productInfo.id()));

        boolean isLiked = false;
        if (criteria.userId() != null) {
            UserInfo userInfo = userService.findUser(criteria.userId());
            if (userInfo != null) {
                isLiked = productLikeService.isLiked(new ProductLikeCommand.IsLiked(productInfo.id(), userInfo.id()));
            }
        }
        return ProductResult.from(productInfo, brandInfo, stockInfo, likeCount, isLiked);
    }
}
