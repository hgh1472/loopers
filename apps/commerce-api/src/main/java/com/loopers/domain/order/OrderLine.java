package com.loopers.domain.order;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Entity
@Getter
@Table(name = "order_line")
public class OrderLine extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_order_id")
    private Order order;

    @Column(name = "ref_product_id", nullable = false)
    private Long productId;

    @Column(name = "quantity", nullable = false)
    private Long quantity;

    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    protected OrderLine() {
    }

    public OrderLine(OrderCommand.Line line) {
        this.productId = line.productId();
        this.quantity = line.quantity();
        this.amount = line.amount();
    }

    public static OrderLine from(OrderCommand.Line line) {
        if (line == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 항목이 존재하지 않습니다.");
        }

        if (line.productId() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품 ID는 필수입니다.");
        }

        if (line.quantity() == null || line.quantity() <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "수량은 1 이상이어야 합니다.");
        }

        if (line.amount() == null || line.amount().compareTo(BigDecimal.ZERO) < 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "가격은 0 이상이어야 합니다.");
        }

        return new OrderLine(line);
    }

    public static List<OrderLine> of(List<OrderCommand.Line> lines) {
        if (lines == null || lines.isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문 항목이 존재하지 않습니다.");
        }

        List<OrderCommand.Line> mergedLines = lines.stream()
                .collect(Collectors.toMap(OrderCommand.Line::productId, line -> line,
                        (l1, l2) ->
                                new OrderCommand.Line(l1.productId(),
                                        l1.quantity() + l2.quantity(),
                                        l1.amount().add(l2.amount())))
                ).values().stream()
                .toList();

        return mergedLines.stream()
                .map(OrderLine::from)
                .toList();
    }

    public void assign(Order order) {
        if (order == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "주문이 존재하지 않습니다.");
        }
        this.order = order;
    }
}
