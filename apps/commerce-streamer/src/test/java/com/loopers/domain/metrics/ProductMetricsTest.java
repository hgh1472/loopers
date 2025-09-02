package com.loopers.domain.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ProductMetricsTest {

    @Nested
    @DisplayName("좋아요 감소 시,")
    class DecrementLikeCount {
        @Test
        @DisplayName("좋아요 수가 0일 경우, Conflict 예외가 발생한다.")
        void decrementLikeCount_Conflict() {
            ProductMetrics productMetrics = new ProductMetrics(1L);

            CoreException exception = assertThrows(CoreException.class, productMetrics::decrementLikeCount);

            assertThat(exception)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "좋아요 수는 0보다 작을 수 없습니다."));
        }
    }
}
