package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.loopers.application.user.JoinRequest;
import com.loopers.support.error.CoreException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class UserTest {

    @Nested
    @DisplayName("User를 생성할 때,")
    class Create {
        @DisplayName("ID는 영문 및 숫자가 아니면 생성에 실패한다.")
        @Test
        void createNonAlphaNumericId() {
            JoinRequest request = new JoinRequest("user!", "user@loopers.com", "1999-06-23", "MALE");

            assertThatThrownBy(() -> User.create(request))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("ID는 영문 및 숫자만 포함할 수 있습니다.");
        }

        @DisplayName("ID가 10자 이내가 아니면 생성에 실패한다.")
        @Test
        void createOverLengthId() {
            JoinRequest request = new JoinRequest("OverLengthId", "user@loopers.com", "1999-06-23", "MALE");

            assertThatThrownBy(() -> User.create(request))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("ID는 10자 이내이어야 합니다.");
        }

        @DisplayName("이메일이 xx@yy.zz 형식이 아니면 생성에 실패한다.")
        @Test
        void createEmailFormat() {
            JoinRequest request = new JoinRequest("hgh1472", "user@loopers", "1999/06/23", "MALE");

            assertThatThrownBy(() -> User.create(request))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("이메일은 xx@yy.zz 형식이어야 합니다.");

        }

        @DisplayName("생년월일이 yyyy-MM-dd 형식이 아니면 생성에 실패한다.")
        @Test
        void createInvalidBirthFormat() {
            JoinRequest request = new JoinRequest("hgh1472", "user@loopers.com", "1999/06/23", "MALE");

            assertThatThrownBy(() -> User.create(request))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("생년월일은 yyyy-MM-dd 형식이어야 합니다.");
        }
    }
}
