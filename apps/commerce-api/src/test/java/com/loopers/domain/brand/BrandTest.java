package com.loopers.domain.brand;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BrandTest {

    @Nested
    @DisplayName("브랜드 생성 시,")
    class Create {
        @DisplayName("이름이 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwBadRequestException_whenNameIsNull() {
            String name = null;
            String description = "브랜드 설명";

            CoreException thrown = assertThrows(CoreException.class, () -> Brand.create(new BrandCommand.Create(name, description)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "브랜드명은 필수입니다."));
        }

        @DisplayName("이름이 비어있으면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwBadRequestException_whenNameIsBlank() {
            String name = "   ";
            String description = "브랜드 설명";

            CoreException thrown = assertThrows(CoreException.class, () -> Brand.create(new BrandCommand.Create(name, description)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "브랜드명은 필수입니다."));
        }

        @DisplayName("설명이 null이면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwBadRequestException_whenDescriptionIsNull() {
            String name = "브랜드명";
            String description = null;

            CoreException thrown = assertThrows(CoreException.class, () -> Brand.create(new BrandCommand.Create(name, description)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "브랜드 설명은 필수입니다."));
        }

        @DisplayName("설명이 비어있으면, BAD_REQUEST 예외가 발생한다.")
        @Test
        void throwBadRequestException_whenDescriptionIsBlank() {
            String name = "브랜드명";
            String description = "   ";

            CoreException thrown = assertThrows(CoreException.class, () -> Brand.create(new BrandCommand.Create(name, description)));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "브랜드 설명은 필수입니다."));
        }
    }
}
