package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserTest {

    @Nested
    @DisplayName("User를 생성할 때,")
    class Create {
        @DisplayName("ID는 영문 및 숫자가 아니면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void throwsBadRequestException_whenNonAlphaNumericId() {
            UserCommand.Join command = new UserCommand.Join("user!", "user@loopers.com", "1999-06-23", "MALE");

            CoreException thrown = assertThrows(CoreException.class, () -> User.create(command));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "ID는 영문 및 숫자만 포함할 수 있습니다."));
        }

        @DisplayName("ID가 10자 이내가 아니면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void createOverLengthId() {
            UserCommand.Join command = new UserCommand.Join("OverLengthId", "user@loopers.com", "1999-06-23", "MALE");

            CoreException thrown = assertThrows(CoreException.class, () -> User.create(command));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "ID는 10자 이내이어야 합니다."));
        }

        @DisplayName("이메일이 xx@yy.zz 형식이 아니면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void createEmailFormat() {
            UserCommand.Join command = new UserCommand.Join("hgh1472", "user@loopers", "1999-06-23", "MALE");

            CoreException thrown = assertThrows(CoreException.class, () -> User.create(command));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "이메일은 xx@yy.zz 형식이어야 합니다."));
        }

        @DisplayName("생년월일이 yyyy-MM-dd 형식이 아니면, BAD_REQUEST 예외를 발생시킨다.")
        @Test
        void createInvalidBirthFormat() {
            UserCommand.Join command = new UserCommand.Join("hgh1472", "user@loopers.com", "1999/06/23", "MALE");

            CoreException thrown = assertThrows(CoreException.class, () -> User.create(command));

            assertThat(thrown)
                    .usingRecursiveComparison()
                    .isEqualTo(new CoreException(ErrorType.BAD_REQUEST, "생년월일은 yyyy-MM-dd 형식이어야 합니다."));
        }
    }
}
