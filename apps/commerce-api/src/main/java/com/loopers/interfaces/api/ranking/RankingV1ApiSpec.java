package com.loopers.interfaces.api.ranking;

import com.loopers.domain.PageResponse;
import com.loopers.interfaces.api.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;

@Tag(name = "Ranking API", description = "Loopers Ranking API입니다.")
public interface RankingV1ApiSpec {
    @Operation(
            summary = "랭킹 조회",
            description = "랭킹 정보를 조회합니다."
    )
    ApiResponse<PageResponse<RankingV1Dto.RankingResponse>> getRanking(int page, int size);

    @Operation(
            summary = "주간 랭킹 조회",
            description = "주간 랭킹 정보를 조회합니다."
    )
    ApiResponse<PageResponse<RankingV1Dto.RankingResponse>> getWeeklyRanking(int page, int size, LocalDate date);
}
