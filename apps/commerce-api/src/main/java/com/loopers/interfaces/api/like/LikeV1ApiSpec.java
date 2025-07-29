package com.loopers.interfaces.api.like;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Like API", description = "Loopers Like API입니다.")
public interface LikeV1ApiSpec {

    @Operation(
            summary = "좋아요 생성",
            description = "X-USER-ID 헤더의 ID와 상품 ID를 통해 좋아요를 생성합니다."
    )
    ApiResponse<LikeV1Dto.ProductLikeResponse> createLike(
            Long userId, Long productId
    );

    @Operation(
            summary = "좋아요 취소",
            description = "X-USER-ID 헤더의 ID와 상품 ID를 통해 좋아요를 취소합니다."
    )
    ApiResponse<LikeV1Dto.ProductLikeResponse> cancelLike(
            Long userId, Long productId
    );
}
