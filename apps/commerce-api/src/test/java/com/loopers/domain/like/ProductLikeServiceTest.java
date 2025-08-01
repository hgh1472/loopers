package com.loopers.domain.like;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

@ExtendWith(MockitoExtension.class)
class ProductLikeServiceTest {

    @InjectMocks
    private ProductLikeService productLikeService;

    @Mock
    private ProductLikeRepository productLikeRepository;

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

    @Nested
    @DisplayName("상품 좋아요 생성 시,")
    class Like {

        @DisplayName("좋아요가 이미 존재할 경우, ConstraintViolationException을 무시하고, 생성 성공 응답을 반환한다.")
        @Test
        void returnProductLikeInfo_whenDuplicateProductLikeExists() {
            ProductLikeCommand.Create command = new ProductLikeCommand.Create(1L, 1L);
            given(productLikeRepository.save(any(ProductLike.class)))
                    .willThrow(new DataIntegrityViolationException("유니크 제약조건"));

            ProductLikeActionInfo productLikeActionInfo = productLikeService.like(command);

            assertAll(
                    () -> assertThat(productLikeActionInfo).isNotNull(),
                    () -> assertThat(productLikeActionInfo.productId()).isEqualTo(command.productId()),
                    () -> assertThat(productLikeActionInfo.userId()).isEqualTo(command.userId())
            );
        }

        @DisplayName("좋아요가 이미 존재할 경우, 변경 여부는 false로 반환한다.")
        @Test
        void returnProductLikeInfoWithFalse_whenDuplicateProductLikeExists() {
            ProductLikeCommand.Create command = new ProductLikeCommand.Create(1L, 1L);
            given(productLikeRepository.save(any(ProductLike.class)))
                    .willThrow(new DataIntegrityViolationException("유니크 제약조건"));

            ProductLikeActionInfo productLikeActionInfo = productLikeService.like(command);

            assertAll(
                    () -> assertThat(productLikeActionInfo).isNotNull(),
                    () -> assertThat(productLikeActionInfo.productId()).isEqualTo(command.productId()),
                    () -> assertThat(productLikeActionInfo.userId()).isEqualTo(command.userId()),
                    () -> assertThat(productLikeActionInfo.changed()).isFalse()
            );
        }

        @DisplayName("좋아요가 존재할 경우, 변경 여부는 true로 반환한다.")
        @Test
        void returnProductLikeInfoWithTrue_whenProductLikeCreated() {
            ProductLikeCommand.Create command = new ProductLikeCommand.Create(1L, 1L);
            ProductLike productLike = ProductLike.create(command);
            given(productLikeRepository.save(any(ProductLike.class)))
                    .willReturn(productLike);

            ProductLikeActionInfo productLikeActionInfo = productLikeService.like(command);

            assertAll(
                    () -> assertThat(productLikeActionInfo).isNotNull(),
                    () -> assertThat(productLikeActionInfo.productId()).isEqualTo(command.productId()),
                    () -> assertThat(productLikeActionInfo.userId()).isEqualTo(command.userId()),
                    () -> assertThat(productLikeActionInfo.changed()).isTrue()
            );
        }
    }

    @Nested
    @DisplayName("상품 좋아요 취소 시,")
    class CancelLike {
        @DisplayName("좋아요가 존재여부와 상관없이, 성공 응답을 반환한다.")
        @Test
        void returnProductLikeInfo_whenProductLikeDoesNotExist() {
            ProductLikeCommand.Delete command = new ProductLikeCommand.Delete(1L, 1L);

            ProductLikeActionInfo productLikeActionInfo = productLikeService.cancelLike(command);

            assertAll(
                    () -> assertThat(productLikeActionInfo).isNotNull(),
                    () -> assertThat(productLikeActionInfo.productId()).isEqualTo(command.productId()),
                    () -> assertThat(productLikeActionInfo.userId()).isEqualTo(command.userId())
            );
        }

        @DisplayName("좋아요가 존재하지 않을 경우, 변경 여부는 false로 반환한다.")
        @Test
        void returnProductLikeInfoWithFalse_whenProductLikeDoesNotExist() {
            ProductLikeCommand.Delete command = new ProductLikeCommand.Delete(1L, 1L);
            given(productLikeRepository.deleteByProductIdAndUserId(command.productId(), command.userId()))
                    .willReturn(false);

            ProductLikeActionInfo productLikeActionInfo = productLikeService.cancelLike(command);

            assertAll(
                    () -> assertThat(productLikeActionInfo).isNotNull(),
                    () -> assertThat(productLikeActionInfo.productId()).isEqualTo(command.productId()),
                    () -> assertThat(productLikeActionInfo.userId()).isEqualTo(command.userId()),
                    () -> assertThat(productLikeActionInfo.changed()).isFalse()
            );
        }

        @DisplayName("좋아요가 존재할 경우, 변경 여부는 true로 반환한다.")
        @Test
        void returnProductLikeInfoWithTrue_whenProductLikeExists() {
            ProductLikeCommand.Delete command = new ProductLikeCommand.Delete(1L, 1L);
            given(productLikeRepository.deleteByProductIdAndUserId(command.productId(), command.userId()))
                    .willReturn(true);

            ProductLikeActionInfo productLikeActionInfo = productLikeService.cancelLike(command);

            assertAll(
                    () -> assertThat(productLikeActionInfo).isNotNull(),
                    () -> assertThat(productLikeActionInfo.productId()).isEqualTo(command.productId()),
                    () -> assertThat(productLikeActionInfo.userId()).isEqualTo(command.userId()),
                    () -> assertThat(productLikeActionInfo.changed()).isTrue()
            );
        }
    }

    @Nested
    @DisplayName("여러 상품 좋아요 여부 조회 시,")
    class AreLiked {

        @DisplayName("유저가 좋아요를 등록한 상품만 반환한다.")
        @Test
        void returnMapOfProductIdsAndLikedStatus() {
            ProductLikeCommand.AreLiked command = new ProductLikeCommand.AreLiked(Set.of(1L, 2L), 1L);
            given(productLikeRepository.findProductLikesOf(1L, Set.of(1L, 2L)))
                    .willReturn(List.of(
                            ProductLike.create(new ProductLikeCommand.Create(1L, 1L))
                    ));

            List<ProductLikeStateInfo> productLikeStateInfos = productLikeService.areLiked(command);

            assertAll(
                    () -> assertThat(productLikeStateInfos).hasSize(2),
                    () -> assertThat(productLikeStateInfos)
                            .extracting("productId", "userId", "liked")
                            .contains(
                                    tuple(1L, 1L, true),
                                    tuple(2L, 1L, false)
                            )
            );
        }
    }
}
