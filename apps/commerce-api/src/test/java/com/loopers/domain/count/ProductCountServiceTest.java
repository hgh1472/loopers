package com.loopers.domain.count;

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
public class ProductCountServiceTest {

    @InjectMocks
    private ProductCountService productCountService;
    @Mock
    private ProductCountRepository productCountRepository;

    @DisplayName("상품 좋아요 수 조회 시, 상품이 존재하지 않으면, NOT_FOUND 예외를 발생시킨다.")
    @Test
    void throwNotFoundException_whenProductDoesNotExist() {
        Long productId = 1L;
        given(productCountRepository.findBy(productId))
                .willReturn(Optional.empty());

        CoreException thrown = assertThrows(CoreException.class, () -> productCountService.getProductCount(new ProductCountCommand.Get(productId)));

        assertThat(thrown)
                .usingRecursiveComparison()
                .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다."));
    }

    @Nested
    @DisplayName("상품 좋아요 수 증가 시,")
    class IncrementLike {

        @DisplayName("존재하지 않는 상품 ID라면, NOT_FOUND 예외를 발생시킨다.")
        @Test
        void throwNotFoundException_whenProductDoesNotExist() {
            Long productId = 1L;
            given(productCountRepository.findBy(productId))
                    .willReturn(Optional.empty());

            CoreException thrown = assertThrows(CoreException.class, () -> productCountService.incrementLike(new ProductCountCommand.Increment(productId)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다."));
        }

        @DisplayName("상품 좋아요 수를 증가시킨다.")
        @Test
        void incrementLikeCount_whenProductExists() {
            Long productId = 1L;
            ProductCount productCount = ProductCount.from(productId);
            Long before = productCount.getLikeCount();
            given(productCountRepository.findBy(productId))
                    .willReturn(Optional.of(productCount));

            ProductCountInfo result = productCountService.incrementLike(new ProductCountCommand.Increment(productId));

            assertThat(result.likeCount()).isEqualTo(before + 1);
        }
    }

    @Nested
    @DisplayName("상품 좋아요 수 감소 시,")
    class DecrementLike {

        @DisplayName("존재하지 않는 상품 ID라면, NOT_FOUND 예외를 발생시킨다.")
        @Test
        void throwNotFoundException_whenProductDoesNotExist() {
            Long productId = 1L;
            given(productCountRepository.findBy(productId))
                    .willReturn(Optional.empty());

            CoreException thrown = assertThrows(CoreException.class, () -> productCountService.decrementLike(new ProductCountCommand.Decrement(productId)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 상품입니다."));
        }

        @DisplayName("상품 좋아요 수를 감소시킨다.")
        @Test
        void decrementLikeCount_whenProductExists() {
            Long productId = 1L;
            ProductCount productCount = ProductCount.from(productId);
            productCount.incrementLike();
            Long before = productCount.getLikeCount();
            given(productCountRepository.findBy(productId))
                    .willReturn(Optional.of(productCount));

            ProductCountInfo result = productCountService.decrementLike(new ProductCountCommand.Decrement(productId));

            assertThat(result.likeCount()).isEqualTo(before - 1);
        }
    }
}
