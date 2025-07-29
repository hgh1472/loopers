package com.loopers.domain.like;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductLikeServiceTest {

    @InjectMocks
    private ProductLikeService productLikeService;

    @Mock
    private ProductLikeRepository productLikeRepository;


    @Nested
    @DisplayName("상품의 좋아요 개수 조회 시,")
    class CountLikes {

        @DisplayName("상품의 좋아요가 없으면, 0을 반환한다.")
        @Test
        void returnZero_whenProductDoesNotExist() {
            ProductLikeCommand.Count command = new ProductLikeCommand.Count(1L);
            given(productLikeRepository.countByProductId(command.productId()))
                    .willReturn(0L);

            Long count = productLikeService.countLikes(command);

            assertThat(count).isZero();
        }
    }

    @Nested
    @DisplayName("유저의 상품 좋아요 여부 조회 시,")
    class IsLiked {

        @DisplayName("유저의 상품 좋아요가 없으면, false을 반환한다.")
        @Test
        void returnFalse_whenProductDoesNotExist() {
            given(productLikeRepository.existsByProductIdAndUserId(1L, 1L))
                    .willReturn(false);
            ProductLikeCommand.IsLiked nonExistCommand = new ProductLikeCommand.IsLiked(1L, 1L);

            boolean isLiked = productLikeService.isLiked(nonExistCommand);

            assertThat(isLiked).isFalse();
        }

    }
}
