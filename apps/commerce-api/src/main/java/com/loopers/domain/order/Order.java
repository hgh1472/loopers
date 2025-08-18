package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;

@Entity
@Getter
@Table(name = "orders")
public class Order extends BaseEntity {

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    List<OrderLine> orderLines = new ArrayList<>();

    private Long userId;

    private OrderStatus status;

    @Embedded
    @AttributeOverrides(value = {
            @AttributeOverride(name = "receiverName", column = @Column(name = "receiver_name", nullable = false)),
            @AttributeOverride(name = "phoneNumber", column = @Column(name = "phone_number", nullable = false)),
            @AttributeOverride(name = "baseAddress", column = @Column(name = "base_address", nullable = false)),
            @AttributeOverride(name = "detailAddress", column = @Column(name = "detail_address", nullable = false)),
            @AttributeOverride(name = "requirements", column = @Column(name = "delivery_requirements"))
    })
    private OrderDelivery orderDelivery;

    @AttributeOverrides(value = {
            @AttributeOverride(name = "paymentAmount",
                    column = @Column(name = "payment_amount", nullable = false, precision = 10, scale = 2))
    })
    private OrderPayment orderPayment;

    protected Order() {
    }

    private Order(Long userId, OrderStatus status, OrderDelivery orderDelivery,
                  OrderPayment orderPayment) {
        this.userId = userId;
        this.status = status;
        this.orderDelivery = orderDelivery;
        this.orderPayment = orderPayment;
    }

    public static Order of(OrderCommand.Order command) {
        if (command.userId() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID가 존재하지 않습니다.");
        }
        OrderPayment orderPayment = new OrderPayment(command.originalAmount(), command.discountAmount(), command.pointAmount());

        Order order = new Order(command.userId(), OrderStatus.PENDING, OrderDelivery.from(command.delivery()), orderPayment);

        List<OrderLine> orderLines = OrderLine.of(command.lines());
        orderLines.forEach(order::addLine);

        return order;
    }

    public void addLine(OrderLine orderLine) {
        if (orderLine == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 항목이 존재하지 않습니다.");
        }
        orderLine.assign(this);
        this.orderLines.add(orderLine);
    }

    public enum OrderStatus {
        PENDING, PAID, DELIVERING, COMPLETED, CANCELED
    }
}
