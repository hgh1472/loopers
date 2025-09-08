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
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MetricsServiceTest {
    @InjectMocks
    private MetricsService metricsService;
    @Mock
    private ProductMetricsRepository productMetricsRepository;

    @Nested
    @DisplayName("상품 좋아요 수 증가 시,")
    class incrementLikeCount {
        @Test
        @DisplayName("당일 상품 메트릭 정보가 존재하지 않는 경우, 생성한다.")
        void createProductMetrics_whenNotExist() {
            LocalDate now = LocalDate.now();
            given(productMetricsRepository.findByDailyMetrics(1L, now)).willReturn(Optional.empty());
            given(productMetricsRepository.save(any()))
                    .willReturn(new ProductMetrics(1L, LocalDate.now()));

            metricsService.incrementLikeCount(new MetricCommand.IncrementLikes(1L, 2L, now));

            verify(productMetricsRepository, times(1))
                    .save(argThat(pm -> pm.getProductId().equals(1L) && pm.getLikeCount().equals(2L)));
        }

        @Test
        @DisplayName("당일 상품 메트릭 정보가 존재하는 경우, 기존 메트릭을 업데이트한다.")
        void updateProductMetrics_whenExist() {
            LocalDate now = LocalDate.now();
            ProductMetrics existMetrics = new ProductMetrics(1L, now);
            existMetrics.incrementLikeCount();
            given(productMetricsRepository.findByDailyMetrics(1L, now))
                    .willReturn(Optional.of(existMetrics));
            given(productMetricsRepository.save(any()))
                    .willReturn(existMetrics);

            metricsService.incrementLikeCount(new MetricCommand.IncrementLikes(1L, 2L, now));

            verify(productMetricsRepository, times(1))
                    .save(argThat(pm -> pm.getProductId().equals(1L) && pm.getLikeCount().equals(3L)));
        }
    }

    @Nested
    @DisplayName("당일 상품 좋아요 수 감소 시,")
    class decrementLikeCount {
        @Test
        @DisplayName("상품 메트릭 정보가 존재하지 않는 경우, Conflict 예외가 발생한다.")
        void throwIllegalStateException_whenDecrementNotExistMetric() {
            LocalDate now = LocalDate.now();
            given(productMetricsRepository.findByDailyMetrics(1L, now)).willReturn(Optional.empty());

            CoreException exception = assertThrows(CoreException.class, () -> metricsService.decrementLikeCount(new MetricCommand.DecrementLikes(1L, 2L, now)));

            assertThat(exception)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "좋아요 수는 0보다 작을 수 없습니다."));
        }

        @Test
        @DisplayName("당일 상품 메트릭 정보가 존재하는 경우, 기존 메트릭을 업데이트한다.")
        void updateProductMetrics_whenExist() {
            LocalDate now = LocalDate.now();
            ProductMetrics existMetrics = new ProductMetrics(1L, now);
            existMetrics.incrementLikeCount(2L);
            given(productMetricsRepository.findByDailyMetrics(1L, now))
                    .willReturn(Optional.of(existMetrics));
            given(productMetricsRepository.save(any()))
                    .willReturn(existMetrics);

            metricsService.decrementLikeCount(new MetricCommand.DecrementLikes(1L, 2L, now));

            verify(productMetricsRepository, times(1))
                    .save(argThat(pm -> pm.getProductId().equals(1L) && pm.getLikeCount().equals(0L)));
        }
    }

    @Nested
    @DisplayName("상품 판매량 증가 시,")
    class incrementSalesCount {
        @Test
        @DisplayName("당일 상품 메트릭 정보가 존재하지 않는 경우, 생성한다.")
        void createProductMetrics_whenNotExist() {
            LocalDate now = LocalDate.now();
            given(productMetricsRepository.findByDailyMetrics(1L, now)).willReturn(Optional.empty());
            given(productMetricsRepository.save(any()))
                    .willReturn(new ProductMetrics(1L, LocalDate.now()));

            metricsService.incrementSalesCount(new MetricCommand.IncrementSales(1L, 3L, now));

            verify(productMetricsRepository, times(1))
                    .save(argThat(pm -> pm.getProductId().equals(1L) && pm.getSalesCount().equals(3L)));
        }

        @Test
        @DisplayName("당일 상품 메트릭 정보가 존재하는 경우, 기존 메트릭을 업데이트한다.")
        void updateProductMetrics_whenExist() {
            LocalDate now = LocalDate.now();
            ProductMetrics existMetrics = new ProductMetrics(1L, now);
            existMetrics.incrementSalesCount(2L);
            given(productMetricsRepository.findByDailyMetrics(1L, now))
                    .willReturn(Optional.of(existMetrics));
            given(productMetricsRepository.save(any()))
                    .willReturn(existMetrics);

            metricsService.incrementSalesCount(new MetricCommand.IncrementSales(1L, 3L, now));

            verify(productMetricsRepository, times(1))
                    .save(argThat(pm -> pm.getProductId().equals(1L) && pm.getSalesCount().equals(5L)));
        }
    }

    @Nested
    @DisplayName("상품 조회 수 증가 시,")
    class IncrementViewCount {
        @Test
        @DisplayName("당일 상품 메트릭 정보가 존재하지 않는 경우, 생성한다.")
        void createProductMetrics_whenNotExist() {
            LocalDate now = LocalDate.now();
            given(productMetricsRepository.findByDailyMetrics(1L, now)).willReturn(Optional.empty());
            given(productMetricsRepository.save(any()))
                    .willReturn(new ProductMetrics(1L, LocalDate.now()));

            metricsService.incrementViewCount(new MetricCommand.IncrementView(1L, now));

            verify(productMetricsRepository, times(1))
                    .save(argThat(pm -> pm.getProductId().equals(1L) && pm.getViewCount().equals(1L)));
        }

        @Test
        @DisplayName("당일 상품 메트릭 정보가 존재하는 경우, 기존 메트릭을 업데이트한다.")
        void updateProductMetrics_whenExist() {
            LocalDate now = LocalDate.now();
            ProductMetrics existMetrics = new ProductMetrics(1L, now);
            existMetrics.incrementViewCount();
            given(productMetricsRepository.findByDailyMetrics(1L, now))
                    .willReturn(Optional.of(existMetrics));
            given(productMetricsRepository.save(any()))
                    .willReturn(existMetrics);

            metricsService.incrementViewCount(new MetricCommand.IncrementView(1L, now));

            verify(productMetricsRepository, times(1))
                    .save(argThat(pm -> pm.getProductId().equals(1L) && pm.getViewCount().equals(2L)));
        }
    }
}
