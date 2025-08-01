package com.loopers.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class PriceTest {

    @Nested
    @DisplayName("가격 생성 시,")
    class Create {
        @DisplayName("가격이 음수라면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenNegativePrice() {
            CoreException thrown = assertThrows(CoreException.class, () -> new Price(new BigDecimal(-1000L)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "가격은 0 이상이어야 합니다."));
        }

        @DisplayName("가격이 null이라면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenZeroPrice() {
            CoreException thrown = assertThrows(CoreException.class, () -> new Price(null));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "가격은 0 이상이어야 합니다."));
        }
    }
}
