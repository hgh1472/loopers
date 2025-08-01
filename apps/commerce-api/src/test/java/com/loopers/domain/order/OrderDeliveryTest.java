package com.loopers.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class OrderDeliveryTest {

    @Nested
    @DisplayName("배송지 정보 생성 시,")
    class Create {

        @DisplayName("배송지 정보가 존재하지 않으면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void returnBadRequestException_whenDeliveryInfoIsNull() {
            OrderCommand.Delivery delivery = null;

            CoreException exception = assertThrows(CoreException.class, () -> OrderDelivery.from(delivery));
            assertThat(exception)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "배송 정보가 존재하지 않습니다."));
        }

        @DisplayName("수령인 이름이 null이거나 비어있으면, BAD_REQUEST 예외를 발생시킨다.")
        @NullAndEmptySource
        @ParameterizedTest(name = "수령인 이름 = {0}")
        void returnBadRequestException_whenRecipientNameIsNullOrEmpty(String recipientName) {
            OrderCommand.Delivery delivery = new OrderCommand.Delivery(recipientName, "010-1234-5678", "서울시 강남구 역삼동", "123-456", "요구사항 없음");

            CoreException exception = assertThrows(CoreException.class, () -> OrderDelivery.from(delivery));
            assertThat(exception)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "수령인 이름이 존재하지 않습니다."));
        }

        @DisplayName("수령인 전화번호가 null이거나 비어있으면, BAD_REQUEST 예외를 발생시킨다.")
        @NullAndEmptySource
        @ParameterizedTest(name = "수령인 전화번호 = {0}")
        void returnBadRequestException_whenRecipientPhoneIsNullOrEmpty(String recipientPhone) {
            OrderCommand.Delivery delivery = new OrderCommand.Delivery("홍길동", recipientPhone, "서울시 강남구 역삼동", "123-456", "요구사항 없음");

            CoreException exception = assertThrows(CoreException.class, () -> OrderDelivery.from(delivery));
            assertThat(exception)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "휴대폰 번호가 존재하지 않습니다."));
        }

        @DisplayName("휴대폰 변호 형식이 xxx-xxxx-xxxx가 아니면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void returnBadReqeustException_whenInvalidPhoneNumberPattern() {
            OrderCommand.Delivery delivery = new OrderCommand.Delivery("홍길동", "01012345678", "서울시 강남구 역삼동", "123-456", "요구사항 없음");

            CoreException exception = assertThrows(CoreException.class, () -> OrderDelivery.from(delivery));
            assertThat(exception)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "휴대폰 번호 형식은 xxx-xxxx-xxxx이어야 합니다."));
        }

        @DisplayName("기본 주소가 null이거나 비어있으면, BAD_REQUEST 예외를 발생시킨다.")
        @NullAndEmptySource
        @ParameterizedTest(name = "배송지 주소 = {0}")
        void returnBadRequestException_whenBaseAddressIsNullOrEmpty(String baseAddress) {
            OrderCommand.Delivery delivery = new OrderCommand.Delivery("홍길동", "010-1234-5678", baseAddress, "123-456", "요구사항 없음");

            CoreException exception = assertThrows(CoreException.class, () -> OrderDelivery.from(delivery));
            assertThat(exception)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "기본 주소가 존재하지 않습니다."));
        }

        @DisplayName("상세 주소가 null이거나 비어있으면, BAD_REQUEST 예외를 발생시킨다.")
        @NullAndEmptySource
        @ParameterizedTest(name = "상세 주소 = {0}")
        void returnBadRequestException_whenDetailAddressIsNullOrEmpty(String detailAddress) {
            OrderCommand.Delivery delivery = new OrderCommand.Delivery("홍길동", "010-1234-5678", "서울시 강남구 역삼동", detailAddress, "요구사항 없음");

            CoreException exception = assertThrows(CoreException.class, () -> OrderDelivery.from(delivery));
            assertThat(exception)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "상세 주소가 존재하지 않습니다."));
        }

        @DisplayName("요구사항이 50자를 초과하면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void returnBadRequestException_whenRequirementsExceed50Characters() {
            String requirements = "요구사항이 너무 길어서 50자를 초과하는 경우입니다. 이 경우에는 BAD_REQUEST 예외가 발생해야 합니다.";
            assertThat(requirements).hasSizeGreaterThan(50);
            OrderCommand.Delivery delivery = new OrderCommand.Delivery("홍길동", "010-1234-5678", "서울시 강남구 역삼동", "123-456", requirements);

            CoreException exception = assertThrows(CoreException.class, () -> OrderDelivery.from(delivery));
            assertThat(exception)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "배송 요구사항은 50자 이하여야 합니다."));
        }
    }

}
