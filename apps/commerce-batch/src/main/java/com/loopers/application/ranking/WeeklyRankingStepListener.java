package com.loopers.application.ranking;

import com.loopers.domain.ranking.RankMvRepository;
import com.loopers.domain.ranking.RankingBuffer;
import com.loopers.domain.ranking.WeeklyProductRankMv;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

@RequiredArgsConstructor
public class WeeklyRankingStepListener implements StepExecutionListener {
    private final RankingBuffer rankingBuffer;
    private final RankMvRepository rankMvRepository;

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String dateStr = stepExecution.getJobParameters().getString("date");
        LocalDate date = LocalDate.parse(dateStr);

        List<WeeklyProductRankMv> mvs = rankingBuffer.getWeeklyRankings(300).stream()
                .map(r -> new WeeklyProductRankMv(r.productId(), r.rank(), r.score(), r.weightedScore(), date.plusDays(1)))
                .toList();

        rankMvRepository.saveWeeklyRankingMvs(mvs);
        rankingBuffer.clearWeeklyBuffer();

        return ExitStatus.COMPLETED;
    }
}
