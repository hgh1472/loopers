package com.loopers.domain.count;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductCountTest {

    @DisplayName("좋아요 수를 증가시킨다.")
    @Test
    void incrementLike() {
        ProductCount productCount = ProductCount.from(1L);
        Long before = productCount.getLikeCount();

        productCount.incrementLike();

        Long after = productCount.getLikeCount();
        assertThat(after).isEqualTo(before + 1);
    }

    @DisplayName("좋아요 수 감소 시, 기존 좋아요 수가 0이면, CONFLICT 예외를 발생시킨다.")
    @Test
    void throwConflictException_whenNegativeCount() {
        ProductCount productCount = ProductCount.from(1L);

        CoreException thrown = assertThrows(CoreException.class, productCount::decrementLike);

        assertThat(thrown)
                .usingRecursiveComparison()
                .isEqualTo(new CoreException(ErrorType.CONFLICT, "더 이상 좋아요 수를 감소시킬 수 없습니다."));
    }

    @DisplayName("좋아요 수를 감소시킨다.")
    @Test
    void decrementLike() {
        ProductCount productCount = ProductCount.from(1L);
        productCount.incrementLike();
        Long before = productCount.getLikeCount();

        productCount.decrementLike();

        Long after = productCount.getLikeCount();
        assertThat(after).isEqualTo(before - 1);
    }
}
