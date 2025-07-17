package com.loopers.domain.user;

import com.loopers.support.error.CoreException;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@SpringBootTest
class UserServiceIntegrationTest {
    @Autowired
    private UserService userService;

    @MockitoSpyBean
    private UserRepository userRepository;

    @Autowired
    private DatabaseCleanUp databaseCleanUp;

    @AfterEach
    void tearDown() {
        databaseCleanUp.truncateAllTables();
    }

    @DisplayName("회원 가입 시,")
    @Nested
    class Join {
        @DisplayName("유저가 저장된다.")
        @Test
        void join() {
            UserCommand.Join command = new UserCommand.Join("hgh1472", "hgh1472@loopers.com", "1999-06-23", "MALE");

            User user = userService.join(command);

            verify(userRepository).save(any());
            assertAll(
                    () -> assertThat(user.getId()).isNotNull(),
                    () -> assertThat(user.getLoginId()).isEqualTo(new LoginId(command.loginId())),
                    () -> assertThat(user.getEmail()).isEqualTo(new Email(command.email())),
                    () -> assertThat(user.getBirthDate()).isEqualTo(new BirthDate(command.birthDate())),
                    () -> assertThat(user.getGender()).isEqualTo(Gender.from(command.gender()))
            );
        }

        @DisplayName("이미 가입된 ID로 시도할 경우, 실패한다.")
        @Test
        void join_withDuplicateLoginId() {
            UserCommand.Join command = new UserCommand.Join("hgh1472", "hgh1472@loopers.com", "1999-06-23", "MALE");
            userService.join(command);
            UserCommand.Join duplicatedRequest = new UserCommand.Join("hgh1472", "hgh1472@naver.com", "1999-06-23", "MALE");

            assertThatThrownBy(() -> userService.join(duplicatedRequest))
                    .isInstanceOf(CoreException.class)
                    .hasMessage("이미 가입된 ID입니다.");
        }
    }

    @DisplayName("회원 정보 조회 시,")
    @Nested
    class GetUserInfo {
        @DisplayName("해당 ID 회원이 존재할 경우, 회원 정보가 반환된다.")
        @Test
        void getUserInfo() {
            User savedUser = userRepository.save(
                    User.create(new UserCommand.Join("hgh1472", "hgh1472@loopers.com", "1999-06-23", "MALE")));

            User user = userService.getUser(savedUser.getLoginId());

            assertAll(
                    () -> assertThat(user.getLoginId()).isEqualTo(savedUser.getLoginId()),
                    () -> assertThat(user.getEmail()).isEqualTo(savedUser.getEmail()),
                    () -> assertThat(user.getBirthDate()).isEqualTo(savedUser.getBirthDate()),
                    () -> assertThat(user.getGender()).isEqualTo(savedUser.getGender())
            );
        }

        @DisplayName("해당 ID 회원이 존재하지 않는 경우, null이 반환된다.")
        @Test
        void getUserInfo_withNotFoundUserId() {
            User user = userService.getUser(new LoginId("NONEXIST"));

            assertThat(user).isNull();
        }
    }
}
