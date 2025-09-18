package com.loopers.interfaces.api.ranking;

import com.loopers.application.ranking.RankingCriteria;
import com.loopers.application.ranking.RankingFacade;
import com.loopers.application.ranking.RankingResult;
import com.loopers.domain.PageResponse;
import com.loopers.interfaces.api.ApiResponse;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/rankings")
public class RankingV1Controller implements RankingV1ApiSpec {
    private final RankingFacade rankingFacade;

    @Override
    @GetMapping
    public ApiResponse<PageResponse<RankingV1Dto.RankingResponse>> getRanking(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        LocalDate today = LocalDate.now();
        PageResponse<RankingResult> result = rankingFacade.getRankProducts(new RankingCriteria.Search(page, size, today));
        return ApiResponse.success(result.map(RankingV1Dto.RankingResponse::from));
    }

    @Override
    public ApiResponse<PageResponse<RankingV1Dto.RankingResponse>> getWeeklyRanking(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) LocalDate date) {
        date = date == null ? LocalDate.now() : date;
        PageResponse<RankingResult> result = rankingFacade.getWeeklyRankProduct(new RankingCriteria.Search(page, size, date));
        return ApiResponse.success(result.map(RankingV1Dto.RankingResponse::from));
    }

    @Override
    public ApiResponse<PageResponse<RankingV1Dto.RankingResponse>> getMonthlyRanking(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) LocalDate date) {
        date = date == null ? LocalDate.now() : date;
        PageResponse<RankingResult> result = rankingFacade.getMonthlyRankProduct(new RankingCriteria.Search(page, size, date));
        return ApiResponse.success(result.map(RankingV1Dto.RankingResponse::from));
    }
}
