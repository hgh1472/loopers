package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.loopers.support.error.CoreException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Nested
    @DisplayName("유저를 생성할 때,")
    class Create {
        @DisplayName("이미 존재하는 ID가 주어지면, CONFLICT 예외가 발생한다.")
        @Test
        void throwsConflictException_whenLoginIdAlreadyExists() {
            UserCommand.Join command = new UserCommand.Join("exist", "email@email.com", "1999-06-23", "MALE");
            given(userRepository.findByLoginId(new LoginId("exist")))
                    .willReturn(Optional.of(User.create(command)));

            assertThatThrownBy(() -> userService.join(command))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("이미 가입된 ID입니다.");
        }
    }

    @Nested
    @DisplayName("유저 정보를 조회할 때,")
    class Get {
        @DisplayName("존재하지 않는 유저 ID로 조회하면, null을 반환한다.")
        @Test
        void throwsNotFoundException_whenNonExistId() {
            given(userRepository.findByLoginId(new LoginId("nonExist")))
                    .willReturn(Optional.empty());

            UserInfo userInfo = userService.getUser("nonExist");

            assertThat(userInfo).isNull();
        }
    }
}
