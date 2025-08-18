package com.loopers.application.product;

import com.loopers.domain.like.ProductLikeCommand;
import com.loopers.domain.like.ProductLikeService;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductUserDetailProcessor {

    private final UserService userService;
    private final ProductLikeService productLikeService;

    public ProductResult.ProductUserDetail getProductUserDetail(ProductCriteria.Get criteria) {
        UserInfo userInfo = userService.findUser(new UserCommand.Find(criteria.userId()));
        boolean isLiked = false;
        if (userInfo != null) {
            isLiked = productLikeService.isLiked(new ProductLikeCommand.IsLiked(criteria.productId(), criteria.userId())).liked();
        }

        return ProductResult.ProductUserDetail.from(isLiked);
    }
}
