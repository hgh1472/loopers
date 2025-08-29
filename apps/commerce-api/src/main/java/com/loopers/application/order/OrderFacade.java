package com.loopers.application.order;

import com.loopers.domain.coupon.CouponCommand;
import com.loopers.domain.coupon.CouponService;
import com.loopers.domain.order.OrderCommand;
import com.loopers.domain.order.OrderInfo;
import com.loopers.domain.order.OrderService;
import com.loopers.domain.point.InsufficientPointException;
import com.loopers.domain.point.PointCommand;
import com.loopers.domain.product.ProductCommand;
import com.loopers.domain.product.ProductInfo;
import com.loopers.domain.product.ProductService;
import com.loopers.domain.stock.InsufficientStockException;
import com.loopers.domain.stock.StockCommand;
import com.loopers.domain.user.UserCommand;
import com.loopers.domain.user.UserInfo;
import com.loopers.domain.user.UserService;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.math.BigDecimal;
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

    private final OrderEventPublisher orderEventPublisher;
    private final AmountProcessor amountProcessor;
    private final ResourceProcessor resourceProcessor;
    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final CouponService couponService;

    @Transactional
    public OrderResult order(OrderCriteria.Order criteria) {
        UserInfo userInfo = userService.findUser(new UserCommand.Find(criteria.userId()));
        if (userInfo == null) {
            throw new CoreException(ErrorType.NOT_FOUND, "사용자를 찾을 수 없습니다.");
        }

        Set<Long> productIds = criteria.lines().stream().map(OrderCriteria.Line::productId).collect(Collectors.toSet());

        Map<Long, BigDecimal> productPriceMap = productService.getPurchasableProducts(new ProductCommand.Purchasable(productIds))
                .stream()
                .collect(Collectors.toMap(ProductInfo::id, ProductInfo::price));
        if (productPriceMap.size() != productIds.size()) {
            throw new CoreException(ErrorType.NOT_FOUND, "주문에 필요한 상품 정보를 찾을 수 없습니다.");
        }
        List<OrderCommand.Line> lines = criteria.toCommandLines(productPriceMap);

        AmountResult amountResult = amountProcessor.applyDiscount(criteria.couponId(), criteria.userId(), lines,
                criteria.point());

        OrderInfo orderInfo = orderService.order(criteria.toOrderCommandWith(lines, criteria.couponId(), amountResult));
        orderEventPublisher.publish(new OrderApplicationEvent.Created(orderInfo.id(), orderInfo.userId(), orderInfo.couponId()));

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

    @Transactional
    public void cancelCreatedOrdersBefore(OrderCriteria.Expire criteria) {
        List<OrderInfo> expiredOrderInfos = orderService.expireCreatedOrdersBefore(new OrderCommand.Expire(criteria.time()));
        for (OrderInfo orderInfo : expiredOrderInfos) {
            if (orderInfo.couponId() == null) {
                continue;
            }
            couponService.restore(new CouponCommand.Restore(orderInfo.couponId(), orderInfo.userId()));
        }
        orderEventPublisher.publish(new OrderApplicationEvent.Expired(expiredOrderInfos.stream().map(OrderInfo::id).toList()));
    }

    @Transactional
    public void failPayment(OrderCriteria.FailPayment criteria) {
        OrderInfo orderInfo = orderService.get(new OrderCommand.Get(criteria.orderId()));
        orderService.fail(new OrderCommand.Fail(orderInfo.id(), OrderCommand.Fail.Reason.PAYMENT_FAILED));
        if (orderInfo.couponId() != null) {
            couponService.restore(new CouponCommand.Restore(orderInfo.couponId(), orderInfo.userId()));
        }
    }

    @Transactional
    public void succeedPayment(OrderCriteria.Success criteria) {
        OrderInfo orderInfo = orderService.get(new OrderCommand.Get(criteria.orderId()));

        List<StockCommand.Deduct> stockCommands = orderInfo.lines().stream()
                .map(line -> new StockCommand.Deduct(line.productId(), line.quantity()))
                .toList();
        PointCommand.Use pointCommand = new PointCommand.Use(orderInfo.userId(), orderInfo.payment().pointAmount());

        try {
            resourceProcessor.deduct(stockCommands, pointCommand);
        } catch (InsufficientPointException e) {
            orderEventPublisher.publish(new OrderApplicationEvent.Refund(orderInfo.id(), orderInfo.couponId(),
                    criteria.transactionKey(), orderInfo.userId(), OrderApplicationEvent.Refund.Reason.POINT_EXHAUSTED));
        } catch (InsufficientStockException e) {
            orderEventPublisher.publish(new OrderApplicationEvent.Refund(orderInfo.id(), orderInfo.couponId(),
                    criteria.transactionKey(), orderInfo.userId(), OrderApplicationEvent.Refund.Reason.OUT_OF_STOCK));
        }

        orderService.paid(new OrderCommand.Paid(orderInfo.id()));
        orderEventPublisher.publish(new OrderApplicationEvent.Paid(orderInfo.id(), criteria.transactionKey()));
    }
}
