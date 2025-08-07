package com.loopers.application.order;

import com.loopers.domain.order.OrderCommand.Line;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderProcessor {
    private final ProductService productService;
    private final OrderService orderService;
    private final DiscountProcessor discountProcessor;

    public OrderInfo placeOrder(OrderCriteria.Order criteria) {
        Set<Long> productIds = criteria.lines().stream().map(OrderCriteria.Line::productId).collect(Collectors.toSet());

        Map<Long, BigDecimal> productPriceMap = productService.getPurchasableProducts(new ProductCommand.Purchasable(productIds))
                .stream()
                .collect(Collectors.toMap(ProductInfo::id, ProductInfo::price));
        if (productPriceMap.size() != productIds.size()) {
            throw new CoreException(ErrorType.NOT_FOUND, "주문에 필요한 상품 정보를 찾을 수 없습니다.");
        }

        List<Line> lines = criteria.toCommandLines(productPriceMap);

        BigDecimal originalAmount = calculateOriginalAmountOf(lines);
        BigDecimal paymentAmount = discountProcessor.applyDiscount(criteria.couponId(), criteria.userId(), originalAmount);

        return orderService.order(criteria.toOrderCommandWith(lines, originalAmount, paymentAmount));
    }

    private static BigDecimal calculateOriginalAmountOf(List<Line> lines) {
        return lines.stream()
                .map(Line::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(0, RoundingMode.FLOOR);
    }
}
