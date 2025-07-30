package com.loopers.domain.order;

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
        Order order = Order.of(command.userId(), command.delivery());
        List<OrderLine> orderLines = OrderLine.of(command.lines());
        orderLines.forEach(order::addLine);
        return OrderInfo.from(orderRepository.save(order));
    }
}
