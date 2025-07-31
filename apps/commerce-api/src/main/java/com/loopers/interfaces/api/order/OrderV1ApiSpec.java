package com.loopers.interfaces.api.order;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Order API", description = "Loopers Order API 입니다.")
public interface OrderV1ApiSpec {

    @Operation(
            summary = "주문 및 결제",
            description = "상품을 주문하고 주문합니다."
    )
    ApiResponse<OrderV1Dto.OrderResponse> order(
            @Schema(name = "주문 요청", description = "주문에 필요한 정보") OrderV1Dto.OrderRequest orderRequest,
            @Schema(name = "사용자 ID", description = "주문하는 사용자 ID") Long userId);

    @Operation(
            summary = "주문 단일 조회",
            description = "주문 ID로 주문을 조회합니다."
    )
    ApiResponse<OrderV1Dto.OrderResponse> get(
            @Schema(name = "사용자 ID", description = "주문을 조회하는 사용자 ID") Long userId,
            @Schema(name = "주문 ID", description = "조회할 주문의 ID") Long orderId);
}
