package com.loopers.application.ranking;

import com.loopers.domain.ranking.MonthlyProductRankMv;
import com.loopers.domain.ranking.RankMvRepository;
import com.loopers.domain.ranking.RankingBuffer;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MonthlyRankingJobListener implements JobExecutionListener {
    private final RankingBuffer rankingBuffer;
    private final RankMvRepository rankMvRepository;

    @Override
    public void afterJob(JobExecution jobExecution) {
        String dateStr = jobExecution.getJobParameters().getString("date");
        LocalDate date = LocalDate.parse(dateStr);

        List<MonthlyProductRankMv> mvs = rankingBuffer.getMonthlyRankings(300).stream()
                .map(info -> new MonthlyProductRankMv(info.productId(), info.score(), info.rank(), date.plusDays(1)))
                .toList();

        rankMvRepository.saveMonthlyRankingMvs(mvs);

        rankingBuffer.clearMonthlyBuffer();
    }
}
