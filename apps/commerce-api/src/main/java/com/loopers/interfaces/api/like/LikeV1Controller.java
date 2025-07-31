package com.loopers.interfaces.api.like;

import com.loopers.application.like.LikeCriteria;
import com.loopers.application.like.LikeFacade;
import com.loopers.application.like.LikeResult;
import com.loopers.interfaces.api.ApiResponse;
import com.loopers.interfaces.api.like.LikeV1Dto.ProductLikeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/like/products")
public class LikeV1Controller implements LikeV1ApiSpec {

    private final LikeFacade likeFacade;

    @Override
    @PostMapping("/{productId}")
    public ApiResponse<LikeV1Dto.ProductLikeResponse> createLike(@RequestHeader("X-USER-ID") Long userId,
                                                                 @PathVariable Long productId) {
        LikeResult.Product result = likeFacade.like(new LikeCriteria.Product(productId, userId));
        return ApiResponse.success(LikeV1Dto.ProductLikeResponse.from(result));
    }

    @Override
    @DeleteMapping("/{productId}")
    public ApiResponse<ProductLikeResponse> cancelLike(@RequestHeader("X-USER-ID") Long userId, @PathVariable Long productId) {
        LikeResult.Product result = likeFacade.cancelLike(new LikeCriteria.Product(productId, userId));
        return ApiResponse.success(LikeV1Dto.ProductLikeResponse.from(result));
    }
}
