package com.loopers.application.like;

import com.loopers.domain.count.ProductCountInfo;
import com.loopers.domain.count.ProductCountService;
import com.loopers.domain.like.ProductLikeActionInfo;
import com.loopers.domain.like.ProductLikeCommand;
import com.loopers.domain.like.ProductLikeInfo;
import com.loopers.domain.like.ProductLikeService;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductSearchInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class LikeFacade {

    private final UserService userService;
    private final ProductService productService;
    private final ProductLikeService productLikeService;
    private final ProductCountService productCountService;

    @Transactional
    public LikeResult.Product like(LikeCriteria.Product criteria) {
        validateProductCriteria(criteria);
        ProductLikeActionInfo productLikeActionInfo = productLikeService.like(criteria.toLikeCreateCommand());
        if (productLikeActionInfo.changed()) {
            productCountService.incrementLike(criteria.productId());
        }
        return LikeResult.Product.from(productLikeActionInfo);
    }

    @Transactional
    public LikeResult.Product cancelLike(LikeCriteria.Product criteria) {
        validateProductCriteria(criteria);
        ProductLikeActionInfo productLikeActionInfo = productLikeService.cancelLike(criteria.toLikeDeleteCommand());
        if (productLikeActionInfo.changed()) {
            productCountService.decrementLike(criteria.productId());
        }
        return LikeResult.Product.from(productLikeActionInfo);
    }

    @Transactional(readOnly = true)
    public List<LikeResult.ProductList> getLikedProducts(LikeCriteria.LikedProducts criteria) {
        UserInfo userInfo = userService.findUser(criteria.userId());
        if (userInfo == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        Map<Long, ProductLikeInfo> likeMap = productLikeService.getMyProductLikes(new ProductLikeCommand.Get(criteria.userId()))
                .stream()
                .collect(Collectors.toMap(ProductLikeInfo::productId, productLikeInfo -> productLikeInfo));

        Map<Long, ProductCountInfo> countMap = productCountService.getProductCounts(likeMap.keySet()).stream()
                .collect(Collectors.toMap(ProductCountInfo::productId, productCountInfo -> productCountInfo));

        Map<Long, ProductSearchInfo> productMap = productService.searchProducts(new ProductCommand.Search(likeMap.keySet())).stream()
                .collect(Collectors.toMap(ProductSearchInfo::id, productSearchInfo -> productSearchInfo));

        return countMap.keySet().stream()
                .map(productId -> LikeResult.ProductList.from(countMap.get(productId), productMap.get(productId), true))
                .toList();
    }

    private void validateProductCriteria(LikeCriteria.Product criteria) {
        UserInfo userInfo = userService.findUser(criteria.userId());
        if (userInfo == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        ProductInfo productInfo = productService.findProduct(new ProductCommand.Find(criteria.productId()));
        if (productInfo == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다.");
        }
    }
}
