package com.loopers.application.ranking;

import com.loopers.domain.ranking.MonthlyProductRankMv;
import com.loopers.domain.ranking.RankMvRepository;
import com.loopers.domain.ranking.RankingBuffer;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

@RequiredArgsConstructor
public class MonthlyRankingStepListener implements StepExecutionListener {
    private final RankingBuffer rankingBuffer;
    private final RankMvRepository rankMvRepository;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String dateStr = stepExecution.getJobParameters().getString("date");
        LocalDate date = LocalDate.parse(dateStr);

        List<MonthlyProductRankMv> mvs = rankingBuffer.getMonthlyRankings(300).stream()
                .map(r -> new MonthlyProductRankMv(r.productId(), r.score(), r.weightedScore(), r.rank(), date.plusDays(1)))
                .toList();

        rankMvRepository.saveMonthlyRankingMvs(mvs);
        rankingBuffer.clearMonthlyBuffer();

        return ExitStatus.COMPLETED;
    }
}
