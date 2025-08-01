package com.loopers.application.order;

import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.point.PointService;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.stock.StockService;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final StockService stockService;
    private final PointService pointService;

    @Transactional
    public OrderResult order(OrderCriteria.Order criteria) {
        UserInfo userInfo = userService.findUser(new UserCommand.Find(criteria.userId()));
        if (userInfo == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        Set<Long> productIds = criteria.lines().stream().map(OrderCriteria.Line::productId).collect(Collectors.toSet());

        Map<Long, ProductInfo> productInfos = productService.getProducts(new ProductCommand.GetProducts(productIds)).stream()
                .collect(Collectors.toMap(ProductInfo::id, product -> product));
        List<OrderCommand.Line> lines = criteria.lines().stream()
                .map(line ->
                        new OrderCommand.Line(line.productId(), line.quantity(), productInfos.get(line.productId()).price()))
                .toList();

        OrderInfo orderInfo = orderService.order(criteria.toOrderCommandWith(lines));

        pointService.use(new PointCommand.Use(criteria.userId(), orderInfo.payment().paymentAmount().longValue()));

        stockService.deductAll(criteria.toCommandDeduct());

        return OrderResult.from(orderInfo);
    }

    @Transactional(readOnly = true)
    public OrderResult get(OrderCriteria.Get criteria) {
        UserInfo userInfo = userService.findUser(new UserCommand.Find(criteria.userId()));
        if (userInfo == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }
        OrderInfo orderInfo = orderService.get(new OrderCommand.Get(criteria.orderId()));
        if (!userInfo.id().equals(orderInfo.userId())) {
            throw new CoreException(ErrorType.CONFLICT, "주문 정보에 접근할 수 없습니다.");
        }
        return OrderResult.from(orderInfo);
    }

    @Transactional(readOnly = true)
    public List<OrderResult> getOrdersOf(OrderCriteria.GetOrders criteria) {
        UserInfo userInfo = userService.findUser(new UserCommand.Find(criteria.userId()));
        if (userInfo == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        return orderService.getOrdersOf(new OrderCommand.GetOrders(criteria.userId()))
                .stream()
                .map(OrderResult::from)
                .toList();
    }
}
