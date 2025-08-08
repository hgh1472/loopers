package com.loopers.application.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;

import com.loopers.domain.order.OrderService;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.user.UserCommand;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderProcessorTest {
    @InjectMocks
    private OrderProcessor orderProcessor;
    @Mock
    private ProductService productService;
    @Mock
    private OrderService orderService;
    @Mock
    private DiscountProcessor discountProcessor;

    @DisplayName("주문 상품 중, 구매 불가능한 상품이 있는 경우, NOT_FOUND 예외가 발생한다.")
    @Test
    void throwNotFoundException_whenNotPurchasable() {
        List<OrderCriteria.Line> lines = List.of(
            new OrderCriteria.Line(1L, 1L),
            new OrderCriteria.Line(2L, 2L)
        );
        OrderCriteria.Delivery delivery = new OrderCriteria.Delivery("주문자", "010-1234-5678",
                "서울시 강남구 역삼동 123-45", "서울시 강남구 역삼동 123-45", "요청사항");
        given(productService.getPurchasableProducts(new ProductCommand.Purchasable(Set.of(1L, 2L))))
                .willReturn(List.of(new ProductInfo(1L, 2L, "상품1", BigDecimal.valueOf(1000), "ON_SALE")));

        OrderCriteria.Order criteria = new OrderCriteria.Order(1L, lines, delivery, 1L);

        CoreException thrown = assertThrows(CoreException.class, () -> orderProcessor.placeOrder(criteria));

        assertThat(thrown)
                .usingRecursiveComparison()
                .isEqualTo(new CoreException(ErrorType.NOT_FOUND, "주문에 필요한 상품 정보를 찾을 수 없습니다."));
    }


}
