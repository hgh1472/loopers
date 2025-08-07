package com.loopers.application.order;

import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderFacade {

    private final UserService userService;
    private final OrderService orderService;
    private final OrderProcessor orderProcessor;
    private final PaymentProcessor paymentProcessor;

    @Transactional
    public OrderResult order(OrderCriteria.Order criteria) {
        UserInfo userInfo = userService.findUser(new UserCommand.Find(criteria.userId()));
        if (userInfo == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        OrderInfo orderInfo = orderProcessor.placeOrder(criteria);
        PointCommand.Use pointCommand = new PointCommand.Use(criteria.userId(), orderInfo.payment().paymentAmount().longValue());
        paymentProcessor.pay(pointCommand, criteria.toCommandDeduct());

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
