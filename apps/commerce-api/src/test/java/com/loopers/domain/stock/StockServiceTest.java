package com.loopers.domain.stock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

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
class StockServiceTest {

    @InjectMocks
    private StockService stockService;

    @Mock
    private StockRepository stockRepository;

    @Nested
    @DisplayName("재고 조회 시,")
    class Find {
        @DisplayName("존재하지 않는 상품의 재고를 조회하면, null을 반환한다.")
        @Test
        void returnNull_whenStockDoesNotExist() {
            Long productId = 1L;
            given(stockRepository.findByProductId(productId))
                    .willReturn(Optional.empty());

            StockInfo stockInfo = stockService.findStock(new StockCommand.Find(productId));

            assertThat(stockInfo).isNull();
        }
    }

    @Nested
    @DisplayName("재고 차감 시,")
    class Deduct {
        @DisplayName("존재하지 않는 상품의 재고를 차감하려고 하면, 예외를 발생시킨다.")
        @Test
        void throwNotFoundException_whenStockDoesNotExist() {
            Long productId = 1L;
            Long quantity = 10L;
            given(stockRepository.findByProductId(productId))
                    .willReturn(Optional.empty());

            CoreException thrown = assertThrows(CoreException.class, () -> stockService.deduct(new StockCommand.Deduct(productId, quantity)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다."));
        }
    }
}
