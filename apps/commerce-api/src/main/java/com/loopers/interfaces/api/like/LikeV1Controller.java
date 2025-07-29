package com.loopers.interfaces.api.like;

import com.loopers.domain.like.ProductLikeCommand;
import com.loopers.domain.like.ProductLikeCommand.Delete;
import com.loopers.domain.like.ProductLikeInfo;
import com.loopers.domain.like.ProductLikeService;
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

    private final ProductLikeService productLikeService;

    @Override
    @PostMapping("/{productId}")
    public ApiResponse<LikeV1Dto.ProductLikeResponse> createLike(@RequestHeader("X-USER-ID") Long userId,
                                                                 @PathVariable Long productId) {
        ProductLikeInfo productLikeInfo = productLikeService.like(new ProductLikeCommand.Create(productId, userId));
        return ApiResponse.success(LikeV1Dto.ProductLikeResponse.from(productLikeInfo));
    }

    @Override
    @DeleteMapping("/{productId}")
    public ApiResponse<ProductLikeResponse> cancelLike(@RequestHeader("X-USER-ID") Long userId, @PathVariable Long productId) {
        ProductLikeInfo productLikeInfo = productLikeService.cancelLike(new Delete(productId, userId));
        return ApiResponse.success(LikeV1Dto.ProductLikeResponse.from(productLikeInfo));
    }
}
