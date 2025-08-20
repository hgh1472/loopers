package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.Getter;

@Entity
@Getter
@Table(name = "orders")
public class Order {

    @Id
    private UUID id;

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

    @Column(name = "ref_coupon_id")
    private Long couponId;

    @Column(name = "created_at", nullable = false, updatable = false)
    private ZonedDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private ZonedDateTime updatedAt;

    @Column(name = "deleted_at")
    private ZonedDateTime deletedAt;

    protected Order() {
    }

    @PrePersist
    private void prePersist() {
        ZonedDateTime now = ZonedDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    private void preUpdate() {
        this.updatedAt = ZonedDateTime.now();
    }

    private Order(Long userId, Long couponId, OrderStatus status, OrderDelivery orderDelivery, OrderPayment orderPayment) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.couponId = couponId;
        this.status = status;
        this.orderDelivery = orderDelivery;
        this.orderPayment = orderPayment;
    }

    public static Order of(OrderCommand.Order command) {
        if (command.userId() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "사용자 ID가 존재하지 않습니다.");
        }
        OrderPayment orderPayment = new OrderPayment(command.originalAmount(), command.discountAmount(), command.pointAmount());

        Order order = new Order(command.userId(), command.couponId(), OrderStatus.PENDING, OrderDelivery.from(command.delivery()),
                orderPayment);

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

    public void fail(OrderCommand.Fail.Reason reason) {
        if (this.status != OrderStatus.PENDING) {
            throw new CoreException(ErrorType.CONFLICT, "주문 상태가 취소할 수 없는 상태입니다.");
        }
        switch (reason) {
            case OUT_OF_STOCK -> this.status = OrderStatus.OUT_OF_STOCK;
            case POINT_EXHAUSTED -> this.status = OrderStatus.POINT_EXHAUSTED;
            case PAYMENT_FAILED -> this.status = OrderStatus.PAYMENT_FAILED;
        }
    }

    public void paid() {
        if (this.status != OrderStatus.PENDING) {
            throw new CoreException(ErrorType.CONFLICT, "주문 상태가 결제할 수 없는 상태입니다.");
        }
        this.status = OrderStatus.PAID;
    }

    public enum OrderStatus {
        PENDING, OUT_OF_STOCK, POINT_EXHAUSTED, PAYMENT_FAILED, PAID, DELIVERING, COMPLETED, CANCELED
    }
}
