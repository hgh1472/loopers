package com.loopers.domain.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

class CardNoTest {

    @Nested
    @DisplayName("카드 번호 생성 시,")
    class Create {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("카드 번호가 null 또는 빈 문자열인 경우, BAD_REQUEST 예외가 발생한다.")
        void throwBadRequestException_whenCardNoIsNullOrEmpty(String cardNo) {
            CoreException thrown = assertThrows(CoreException.class, () -> new CardNo(cardNo));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "카드 번호를 입력해주세요."));
        }

        @Test
        @DisplayName("카드 번호 형식이 올바르지 않은 경우, BAD_REQUEST 예외가 발생한다.")
        void throwBadRequestException_whenCardNoFormatIsInvalid() {
            String invalidCardNo = "1234-5678-9012"; // 형식이 잘못됨
            CoreException thrown = assertThrows(CoreException.class, () -> new CardNo(invalidCardNo));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "카드 번호 형식이 올바르지 않습니다."));
        }

        @Test
        @DisplayName("카드 번호에 숫자 외 문자가 포함되는 경우, BAD_REQUEST 예외가 발생한다.")
        void throwBadRequestException_whenCardNoContainsNonNumericCharacters() {
            String invalidCardNo = "1234-5678-9012-ABCD";

            CoreException thrown = assertThrows(CoreException.class, () -> new CardNo(invalidCardNo));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "카드 번호 형식이 올바르지 않습니다."));
        }

        @Test
        @DisplayName("카드 번호는 0000-0000-0000-0000 형식이어야 한다.")
        void cardNoShouldBeValidFormat() {
            String validCardNo = "1234-5678-9012-3456";

            CardNo cardNo = new CardNo(validCardNo);

            assertThat(cardNo.value()).isEqualTo(validCardNo);
        }
    }
}
