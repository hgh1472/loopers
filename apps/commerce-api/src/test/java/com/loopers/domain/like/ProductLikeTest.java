package com.loopers.domain.like;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ProductLikeTest {

    @Nested
    @DisplayName("상품 좋아요 생성 시,")
    class Create {

        @DisplayName("사용자 ID가 null이라면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenUserIdIsNull() {
            ProductLikeCommand.Create command = new ProductLikeCommand.Create(1L, null);

            CoreException thrown = assertThrows(CoreException.class, () -> ProductLike.create(command));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "사용자 ID는 필수입니다."));
        }

        @DisplayName("상품 ID가 null이라면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwBadRequestException_whenProductIdIsNull() {
            ProductLikeCommand.Create command = new ProductLikeCommand.Create(null, 1L);

            CoreException thrown = assertThrows(CoreException.class, () -> ProductLike.create(command));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "상품 ID는 필수입니다."));
        }
    }
}
