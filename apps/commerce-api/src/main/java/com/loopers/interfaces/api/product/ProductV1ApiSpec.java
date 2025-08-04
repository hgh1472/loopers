package com.loopers.interfaces.api.product;

import com.loopers.domain.PageResponse;
import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Product API", description = "Loopers Product API입니다.")
public interface ProductV1ApiSpec {

    @Operation(
            summary = "상품 상세 조회",
            description = "상품 상세 정보를 조회합니다."
    )
    ApiResponse<ProductV1Dto.ProductResponse> getProduct(
            Long productId, Long userId
    );

    @Operation(
            summary = "상품 목록 조회",
            description = "상품 목록을 조회합니다."
    )
    ApiResponse<PageResponse<ProductV1Dto.ProductCard>> searchProducts(
            ProductV1Dto.ProductSearchRequest request, Long userId
    );
}
