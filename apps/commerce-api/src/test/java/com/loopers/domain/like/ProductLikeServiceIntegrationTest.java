package com.loopers.domain.like;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import com.loopers.domain.like.ProductLikeCommand.Create;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductLikeServiceIntegrationTest {

    @Autowired
    private ProductLikeService productLikeService;
    @Autowired
    private ProductLikeRepository productLikeRepository;
    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @Nested
    @DisplayName("상품 좋아요 생성 시,")
    class Like {
        @DisplayName("이미 상품 좋아요가 존재할 경우, 유니크키 예외를 무시하고, 정상 응답을 반환한다.")
        @Test
        void returnProductLikeInfo_whenDuplicateProductLikeExists() {
            productLikeRepository.save(ProductLike.create(new ProductLikeCommand.Create(1L, 1L)));

            ProductLikeActionInfo productLikeActionInfo = productLikeService.like(new Create(1L, 1L));

            assertAll(
                    () -> assertThat(productLikeActionInfo).isNotNull(),
                    () -> assertThat(productLikeActionInfo.productId()).isEqualTo(1L),
                    () -> assertThat(productLikeActionInfo.userId()).isEqualTo(1L)
            );
        }
    }

    @Nested
    @DisplayName("상품 좋아요 취소 시,")
    class CancelLike {

        @DisplayName("상품 좋아요가 존재하지 않더라도, 정상 응답을 반환한다.")
        @Test
        void returnProductLikeInfo_whenProductLikeExists() {
            productLikeRepository.save(ProductLike.create(new ProductLikeCommand.Create(1L, 1L)));

            ProductLikeActionInfo productLikeActionInfo = productLikeService.cancelLike(new ProductLikeCommand.Delete(1L, 1L));

            assertAll(
                    () -> assertThat(productLikeActionInfo).isNotNull(),
                    () -> assertThat(productLikeActionInfo.productId()).isEqualTo(1L),
                    () -> assertThat(productLikeActionInfo.userId()).isEqualTo(1L)
            );
        }
    }
}
