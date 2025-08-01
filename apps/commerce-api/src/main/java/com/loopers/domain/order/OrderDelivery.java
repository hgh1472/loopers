package com.loopers.domain.order;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.Embeddable;
import java.util.Objects;
import lombok.Getter;

@Getter
@Embeddable
public class OrderDelivery {

    public static final String PHONE_NUMBER_PATTERN = "^\\d{3}-\\d{4}-\\d{4}$";
    private String receiverName;

    private String phoneNumber;

    private String baseAddress;

    private String detailAddress;

    private String requirements;

    protected OrderDelivery() {
    }

    private OrderDelivery(String receiverName, String baseAddress, String detailAddress, String phoneNumber, String requirements) {
        this.receiverName = receiverName;
        this.baseAddress = baseAddress;
        this.detailAddress = detailAddress;
        this.phoneNumber = phoneNumber;
        this.requirements = requirements;
    }

    public static OrderDelivery from(OrderCommand.Delivery delivery) {
        if (delivery == null) {
            throw new CoreException(ErrorType.BAD_REQUEST, "배송 정보가 존재하지 않습니다.");
        }
        if (delivery.phoneNumber() == null || delivery.phoneNumber().isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "휴대폰 번호가 존재하지 않습니다.");
        }
        if (!delivery.phoneNumber().matches(PHONE_NUMBER_PATTERN)) {
            throw new CoreException(ErrorType.BAD_REQUEST, "휴대폰 번호 형식은 xxx-xxxx-xxxx이어야 합니다.");
        }
        if (delivery.receiverName() == null || delivery.receiverName().isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "수령인 이름이 존재하지 않습니다.");
        }
        if (delivery.baseAddress() == null || delivery.baseAddress().isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "기본 주소가 존재하지 않습니다.");
        }
        if (delivery.detailAddress() == null || delivery.detailAddress().isEmpty()) {
            throw new CoreException(ErrorType.BAD_REQUEST, "상세 주소가 존재하지 않습니다.");
        }
        if (!delivery.requirements().isBlank() && delivery.requirements().length() > 50) {
            throw new CoreException(ErrorType.BAD_REQUEST, "배송 요구사항은 50자 이하여야 합니다.");
        }

        return new OrderDelivery(
                delivery.receiverName(),
                delivery.baseAddress(),
                delivery.detailAddress(),
                delivery.phoneNumber(),
                delivery.requirements()
        );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderDelivery that = (OrderDelivery) o;
        return Objects.equals(receiverName, that.receiverName) && Objects.equals(phoneNumber,
                that.phoneNumber) && Objects.equals(baseAddress, that.baseAddress) && Objects.equals(
                detailAddress, that.detailAddress) && Objects.equals(requirements, that.requirements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(receiverName, phoneNumber, baseAddress, detailAddress, requirements);
    }
}
