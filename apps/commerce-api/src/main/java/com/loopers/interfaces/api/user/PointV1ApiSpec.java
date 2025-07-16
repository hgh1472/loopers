package com.loopers.interfaces.api.user;

import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Point API", description = "Loopers Point API입니다.")
public interface PointV1ApiSpec {

    @Operation(
            summary = "포인트 조회",
            description = "X-USER-ID 헤더의 로그인 ID를 통해 포인트를 조회합니다."
    )
    ApiResponse<PointV1Dto.PointResponse> getPoints(
            @Schema(name = "사용자 ID", description = "포인트를 조회할 사용자의 로그인 ID")
            String loginId
    );

    @Operation(
            summary = "포인트 충전",
            description = "X-USER-ID 헤더의 로그인 ID와 포인트 충전 요청을 통해 포인트를 충전합니다."
    )
    ApiResponse<PointV1Dto.PointResponse> chargePoint(
            @Schema(name = "사용자 ID", description = "포인트를 충전할 사용자의 로그인 ID")
            String loginId,
            @Schema(name = "포인트 충전 요청", description = "충전할 포인트 수량")
            PointV1Dto.ChargeRequest chargeRequest
    );
}
