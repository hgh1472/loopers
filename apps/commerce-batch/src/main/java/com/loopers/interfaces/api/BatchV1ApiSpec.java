package com.loopers.interfaces.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;

@Tag(name = "Batch API", description = "Loopers Batch API 입니다.")
public interface BatchV1ApiSpec {
    @Operation(
            summary = "랭킹 배치 실행",
            description = "랭킹 배치를 실행합니다."
    )
    ApiResponse<BatchV1Dto.RankingResponse> runRankingBatch(LocalDate date);
}
