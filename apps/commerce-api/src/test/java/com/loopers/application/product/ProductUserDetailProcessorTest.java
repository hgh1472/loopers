package com.loopers.application.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.*;

import com.loopers.domain.like.LikeInfo;
import com.loopers.domain.like.ProductLikeCommand;
import com.loopers.domain.like.ProductLikeService;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductUserDetailProcessorTest {

    @InjectMocks
    private ProductUserDetailProcessor productUserDetailProcessor;
    @Mock
    private UserService userService;
    @Mock
    private ProductLikeService productLikeService;

    @Nested
    @DisplayName("유저의 상품 관련 정보 조회 시,")
    class Get {

        @Test
        @DisplayName("로그인 유저일 경우, 좋아요 여부를 가져온다.")
        void getProductLiked_withUser() {
            given(userService.findUser(new UserCommand.Find(1L)))
                    .willReturn(new UserInfo(1L, "test", "hgh1472@loopers.com", LocalDate.now(), "MALE"));
            given(productLikeService.isLiked(new ProductLikeCommand.IsLiked(1L, 1L)))
                    .willReturn(new LikeInfo.IsLiked(true));

            ProductResult.ProductUserDetail userDetail = productUserDetailProcessor.getProductUserDetail(new ProductCriteria.Get(1L, 1L));

            assertThat(userDetail.isLiked()).isTrue();
        }

        @Test
        @DisplayName("비로그인 유저일 경우, 좋아요 여부는 false로 반환한다.")
        void returnLikedFalse_withoutUser() {
            given(userService.findUser(new UserCommand.Find(1L)))
                    .willReturn(null);

            ProductResult.ProductUserDetail userDetail = productUserDetailProcessor.getProductUserDetail(new ProductCriteria.Get(1L, 1L));

            assertThat(userDetail.isLiked()).isFalse();
        }
    }
}
