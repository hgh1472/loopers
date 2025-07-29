package com.loopers.domain.stock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

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
}
