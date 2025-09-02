package com.loopers.domain.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MetricServiceTest {
    @InjectMocks
    private MetricService metricService;
    @Mock
    private ProductMetricsRepository productMetricsRepository;

    @Nested
    @DisplayName("상품 좋아요 수 증가 시,")
    class incrementLikeCount {
        @Test
        @DisplayName("상품 메트릭 정보가 존재하지 않는 경우, 생성한다.")
        void createProductMetrics_whenNotExist() {
            given(productMetricsRepository.findByProductId(1L)).willReturn(Optional.empty());
            given(productMetricsRepository.save(any()))
                    .willReturn(new ProductMetrics(1L));

            metricService.incrementsLikeCount(new MetricCommand.IncrementLike(1L));

            verify(productMetricsRepository, times(1))
                    .save(argThat(pm -> pm.getProductId().equals(1L) && pm.getLikeCount().equals(1L)));
        }

        @Test
        @DisplayName("상품 메트릭 정보가 존재하는 경우, 기존 메트릭을 업데이트한다.")
        void updateProductMetrics_whenExist() {
            ProductMetrics existMetrics = new ProductMetrics(1L);
            existMetrics.incrementLikeCount();
            given(productMetricsRepository.findByProductId(1L))
                    .willReturn(Optional.of(existMetrics));
            given(productMetricsRepository.save(any()))
                    .willReturn(existMetrics);

            metricService.incrementsLikeCount(new MetricCommand.IncrementLike(1L));

            verify(productMetricsRepository, times(1))
                    .save(argThat(pm -> pm.getProductId().equals(1L) && pm.getLikeCount().equals(2L)));
        }
    }

    @Nested
    @DisplayName("상품 좋아요 수 감소 시,")
    class decrementLikeCount {
        @Test
        @DisplayName("상품 메트릭 정보가 존재하지 않는 경우, Conflict 예외가 발생한다.")
        void throwIllegalStateException_whenDecrementNotExistMetric() {
            given(productMetricsRepository.findByProductId(1L)).willReturn(Optional.empty());

            CoreException exception = assertThrows(CoreException.class, () -> metricService.decrementsLikeCount(new MetricCommand.DecrementLike(1L)));

            assertThat(exception)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "좋아요 수는 0보다 작을 수 없습니다."));
        }

        @Test
        @DisplayName("상품 메트릭 정보가 존재하는 경우, 기존 메트릭을 업데이트한다.")
        void updateProductMetrics_whenExist() {
            ProductMetrics existMetrics = new ProductMetrics(1L);
            existMetrics.incrementLikeCount();
            given(productMetricsRepository.findByProductId(1L))
                    .willReturn(Optional.of(existMetrics));
            given(productMetricsRepository.save(any()))
                    .willReturn(existMetrics);

            metricService.decrementsLikeCount(new MetricCommand.DecrementLike(1L));

            verify(productMetricsRepository, times(1))
                    .save(argThat(pm -> pm.getProductId().equals(1L) && pm.getLikeCount().equals(0L)));
        }
    }
}
