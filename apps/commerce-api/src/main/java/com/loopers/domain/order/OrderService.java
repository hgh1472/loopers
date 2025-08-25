package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    @Transactional
    public OrderInfo order(OrderCommand.Order command) {
        Order order = Order.of(command);
        return OrderInfo.from(orderRepository.save(order));
    }

    @Transactional(readOnly = true)
    public OrderInfo get(OrderCommand.Get command) {
        return orderRepository.findById(command.orderId())
                .map(OrderInfo::from)
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 주문입니다."));
    }

    @Transactional(readOnly = true)
    public List<OrderInfo> getOrdersOf(OrderCommand.GetOrders command) {
        return orderRepository.findAllByUserId(command.userId())
                .stream()
                .map(OrderInfo::from)
                .toList();
    }

    @Transactional
    public List<OrderInfo> expireCreatedOrdersBefore(OrderCommand.Expire command) {
        List<Order> createdOrders = orderRepository.findCreatedOrdersBefore(command.time());
        createdOrders.forEach(Order::expired);
        return createdOrders.stream()
                .map(OrderInfo::from)
                .toList();
    }

    @Transactional
    public OrderInfo fail(OrderCommand.Fail command) {
        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 주문입니다."));
        order.fail(command.reason());
        return OrderInfo.from(order);
    }

    @Transactional
    public OrderInfo paid(OrderCommand.Paid command) {
        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 주문입니다."));
        order.paid();
        return OrderInfo.from(order);
    }

    @Transactional
    public OrderInfo pending(OrderCommand.Pending command) {
        Order order = orderRepository.findById(command.orderId())
                .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "존재하지 않는 주문입니다."));
        order.pending();
        return OrderInfo.from(order);
    }
}
