package com.loopers.interfaces.api;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.time.LocalDate;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/batch/")
public class BatchV1Controller implements BatchV1ApiSpec {
    private final JobLauncher jobLauncher;
    private final Job rankingJob;

    @Override
    @PutMapping("ranking")
    public ApiResponse<BatchV1Dto.RankingResponse> runRankingBatch(@RequestParam LocalDate date) {
        String batchId = UUID.randomUUID().toString();
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("date", date.toString())
                .addString("batch-id", batchId)
                .toJobParameters();

        try {
            jobLauncher.run(rankingJob, jobParameters);
        } catch (Exception e) {
            log.error("배치 실패 - rankingJob", e);
            throw new CoreException(ErrorType.INTERNAL_ERROR, "배치 실행 중 오류가 발생했습니다. Batch ID : " + batchId);
        }
        return ApiResponse.success(new BatchV1Dto.RankingResponse(batchId));
    }
}
