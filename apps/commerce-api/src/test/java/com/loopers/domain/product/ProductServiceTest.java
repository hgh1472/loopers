package com.loopers.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Nested
    @DisplayName("상품 조회 시,")
    class Find {
        @DisplayName("존재하지 않는 상품 ID라면, null을 반환한다.")
        @Test
        void returnNull_whenProductDoesNotExist() {
            Long productId = 2L;
            given(productRepository.findById(productId))
                    .willReturn(Optional.empty());

            ProductInfo productInfo = productService.findProduct(new ProductCommand.Find(productId));

            assertThat(productInfo).isNull();
        }
    }
}
