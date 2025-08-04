package com.loopers.domain.stock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class StockTest {

    @Nested
    @DisplayName("재고 생성 시,")
    class Create {

        @DisplayName("상품 ID가 null이라면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenNullStock() {
            CoreException thrown = assertThrows(CoreException.class, () -> Stock.create(new StockCommand.Create(null, 100L)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "상품 ID는 필수입니다."));
        }

        @DisplayName("재고가 null이라면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenNegativeQuantity() {
            CoreException thrown = assertThrows(CoreException.class, () -> Stock.create(new StockCommand.Create(1L, null)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "수량은 0 이상이어야 합니다."));
        }

        @DisplayName("재고가 음수라면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenNegativeStock() {
            CoreException thrown = assertThrows(CoreException.class, () -> Stock.create(new StockCommand.Create(1L, -100L)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "수량은 0 이상이어야 합니다."));
        }
    }

    @Nested
    @DisplayName("재고 차감 시,")
    class Deduct {
        @DisplayName("차감할 수량이 null이면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenNullQuantity() {
            Stock stock = Stock.create(new StockCommand.Create(1L, 100L));
            CoreException thrown = assertThrows(CoreException.class, () -> stock.deduct(null));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "차감할 수량은 1 이상이어야 합니다."));
        }

        @DisplayName("차감할 수량이 0 이하라면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenZeroOrNegativeQuantity() {
            Stock stock = Stock.create(new StockCommand.Create(1L, 100L));
            CoreException thrown = assertThrows(CoreException.class, () -> stock.deduct(0L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "차감할 수량은 1 이상이어야 합니다."));
        }

        @DisplayName("재고가 부족하면, CONFLICT 예외를 발생시킨다.")
        @Test
        void throwConflictException_whenInsufficientStock() {
            Stock stock = Stock.create(new StockCommand.Create(1L, 100L));
            CoreException thrown = assertThrows(CoreException.class, () -> stock.deduct(101L));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.CONFLICT, "재고가 부족합니다."));
        }
    }
}
