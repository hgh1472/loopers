package com.loopers.application.product;

import com.loopers.domain.PageResponse;
import com.loopers.domain.like.LikeInfo;
import com.loopers.domain.like.ProductLikeCommand;
import com.loopers.domain.like.ProductLikeService;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ProductFacade {

    private final ProductDetailProcessor productDetailProcessor;
    private final ProductUserDetailProcessor productUserDetailProcessor;
    private final ProductService productService;
    private final ProductLikeService productLikeService;
    private final UserService userService;

    @Transactional(readOnly = true)
    public ProductResult getProduct(ProductCriteria.Get criteria) {
        ProductResult.ProductDetail detail = productDetailProcessor.getProductDetail(criteria.productId());
        ProductResult.ProductUserDetail userDetail = productUserDetailProcessor.getProductUserDetail(criteria);
        return ProductResult.from(detail, userDetail);
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
