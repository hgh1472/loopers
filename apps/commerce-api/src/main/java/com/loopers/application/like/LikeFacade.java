package com.loopers.application.like;

import com.loopers.domain.count.ProductCountService;
import com.loopers.domain.like.ProductLikeInfo;
import com.loopers.domain.like.ProductLikeService;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
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
        validateCriteria(criteria);
        ProductLikeInfo productLikeInfo = productLikeService.like(criteria.toLikeCreateCommand());
        if (productLikeInfo.changed()) {
            productCountService.incrementLike(criteria.productId());
        }
        return LikeResult.Product.from(productLikeInfo);
    }

    @Transactional
    public LikeResult.Product cancelLike(LikeCriteria.Product criteria) {
        validateCriteria(criteria);
        ProductLikeInfo productLikeInfo = productLikeService.cancelLike(criteria.toLikeDeleteCommand());
        if (productLikeInfo.changed()) {
            productCountService.decrementLike(criteria.productId());
        }
        return LikeResult.Product.from(productLikeInfo);
    }

    private void validateCriteria(LikeCriteria.Product criteria) {
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
