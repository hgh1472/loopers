package com.loopers.interfaces.api.order;

import com.loopers.application.order.OrderCriteria;
import com.loopers.application.order.OrderCriteria.Order;
import com.loopers.application.order.OrderFacade;
import com.loopers.application.order.OrderResult;
import com.loopers.interfaces.api.ApiResponse;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderV1ApiController implements OrderV1ApiSpec {

    private final OrderFacade orderFacade;

    @Override
    @PostMapping
    public ApiResponse<OrderV1Dto.OrderResponse> order(@RequestBody OrderV1Dto.OrderRequest orderRequest, @RequestHeader("X-USER-ID") Long userId) {
        List<OrderCriteria.Line> lines = orderRequest.lines().stream()
                .map(OrderV1Dto.Line::toCriteriaLine)
                .toList();
        OrderResult orderResult = orderFacade.order(new Order(userId, lines, orderRequest.delivery().toCriteriaDelivery()));
        return ApiResponse.success(OrderV1Dto.OrderResponse.from(orderResult));
    }
}
