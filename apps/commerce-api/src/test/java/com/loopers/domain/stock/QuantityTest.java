package com.loopers.domain.stock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

class QuantityTest {

    @Nested
    @DisplayName("수량 생성 시,")
    class Create {
        @DisplayName("수량이 음수라면, BAD_REQUEST 예외를 발생시킨다.")
        @org.junit.jupiter.api.Test
        void throwBadRequestException_whenNegativeQuantity() {
            CoreException thrown = assertThrows(CoreException.class, () -> new Quantity(-1L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "수량은 0 이상이어야 합니다."));
        }

        @DisplayName("수량이 null이라면, BAD_REQUEST 예외를 발생시킨다.")
        @org.junit.jupiter.api.Test
        void throwBadRequestException_whenNullQuantity() {
            CoreException thrown = assertThrows(CoreException.class, () -> new Quantity(null));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "수량은 0 이상이어야 합니다."));
        }
    }

}
