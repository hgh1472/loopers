package com.loopers.domain.stock;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "stock")
public class Stock extends BaseEntity {

    @Column(name = "ref_product_id", nullable = false)
    private Long productId;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "quantity", nullable = false))
    private Quantity quantity;

    protected Stock() {
    }

    private Stock(Long productId, Quantity quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public static Stock create(StockCommand.Create command) {
        if (command.productId() == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상품 ID는 필수입니다.");
        }
        return new Stock(command.productId(), new Quantity(command.quantity()));
    }

    public void deduct(Long quantity) {
        if (quantity == null || quantity <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "차감할 수량은 1 이상이어야 합니다.");
        }
        if (this.quantity.getValue() < quantity) {
            throw new CoreException(ErrorType.CONFLICT, "재고가 부족합니다.");
        }
        this.quantity = new Quantity(this.quantity.getValue() - quantity);
    }

    public Long getProductId() {
        return productId;
    }

    public Quantity getQuantity() {
        return new Quantity(this.quantity.getValue());
    }
}
