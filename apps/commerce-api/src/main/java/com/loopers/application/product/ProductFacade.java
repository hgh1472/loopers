package com.loopers.application.product;

import com.loopers.domain.PageResponse;
import com.loopers.domain.brand.BrandCommand;
import com.loopers.domain.brand.BrandInfo;
import com.loopers.domain.brand.BrandService;
import com.loopers.domain.cache.CacheCommand;
import com.loopers.domain.cache.CacheService;
import com.loopers.domain.cache.ProductDetailCache;
import com.loopers.domain.count.ProductCountCommand;
import com.loopers.domain.count.ProductCountInfo;
import com.loopers.domain.count.ProductCountService;
import com.loopers.domain.like.LikeInfo;
import com.loopers.domain.like.ProductLikeCommand;
import com.loopers.domain.like.ProductLikeService;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.stock.StockInfo;
import com.loopers.domain.stock.StockService;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final CacheService cacheService;
    private final ProductService productService;
    private final BrandService brandService;
    private final ProductLikeService productLikeService;
    private final StockService stockService;
    private final ProductCountService productCountService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public ProductResult getProduct(ProductCriteria.Get criteria) {
        ProductInfo productInfo = productService.findProduct(new ProductCommand.Find(criteria.productId()));
        if (productInfo == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다.");
        }

        Optional<ProductDetailCache> cache = cacheService.findProductDetail(criteria.productId());

        UserInfo userInfo = userService.findUser(new UserCommand.Find(criteria.userId()));
        boolean isLiked = false;
        if (userInfo != null) {
            isLiked = productLikeService.isLiked(new ProductLikeCommand.IsLiked(productInfo.id(), userInfo.id()))
                    .liked();
        }

        if (cache.isPresent()) {
            return ProductResult.from(ProductResult.ProductDetail.from(cache.get()),
                    ProductResult.ProductUserDetail.from(isLiked));
        }

        BrandInfo brandInfo = brandService.findBy(new BrandCommand.Find(productInfo.brandId()));
        StockInfo stockInfo = stockService.findStock(new StockCommand.Find(productInfo.id()));
        ProductCountInfo countInfo = productCountService.getProductCount(new ProductCountCommand.Get(productInfo.id()));

        cacheService.writeProductDetail(CacheCommand.ProductDetail.of(productInfo, brandInfo, stockInfo, countInfo));
        return ProductResult.from(productInfo, brandInfo, stockInfo, countInfo.likeCount(), isLiked);
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResult.Card> searchProducts(ProductCriteria.Search criteria) {
        PageResponse<ProductInfo.Search> infos = productService.search(criteria.toPageCommand());

        UserInfo userInfo = userService.findUser(new UserCommand.Find(criteria.userId()));
        if (userInfo == null) {
            return infos.map(info -> ProductResult.Card.from(info, false));
        }

        Set<Long> productIds = infos.getContent().stream().map(ProductInfo.Search::id).collect(Collectors.toSet());
        Map<Long, LikeInfo.ProductState> stateMap = productLikeService.areLiked(
                        new ProductLikeCommand.AreLiked(productIds, userInfo.id()))
                .stream()
                .collect(Collectors.toMap(LikeInfo.ProductState::productId, info -> info));

        return infos.map(info -> ProductResult.Card.from(info, stateMap.get(info.id()).isLiked()));
    }
}
