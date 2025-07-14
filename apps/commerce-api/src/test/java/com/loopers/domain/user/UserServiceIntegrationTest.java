package com.loopers.domain.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

import com.loopers.application.user.JoinRequest;
import com.loopers.application.user.UserInfo;
import com.loopers.support.error.CoreException;
import com.loopers.utils.DatabaseCleanUp;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

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

    @DisplayName("회원 가입 시, 유저가 저장된다.")
    @Test
    void join() {
        JoinRequest request = new JoinRequest("hgh1472", "hgh1472@loopers.com", "1999-06-23", "MALE");

        UserInfo userInfo = userService.join(request);

        verify(userRepository).save(any());
        assertAll(
                () -> assertThat(userInfo.loginId()).isEqualTo(new LoginId(request.loginId())),
                () -> assertThat(userInfo.email()).isEqualTo(new Email(request.email())),
                () -> assertThat(userInfo.birthDate()).isEqualTo(new BirthDate(request.birthDate())),
                () -> assertThat(userInfo.gender()).isEqualTo(Gender.from(request.gender())),
                () -> assertThat(userInfo.point()).isEqualTo(0L)
        );
    }

    @DisplayName("이미 가입된 ID로 회원가입 시도 시, 실패한다.")
    @Test
    void joinWithDuplicateLoginId() {
        JoinRequest request = new JoinRequest("hgh1472", "hgh1472@loopers.com", "1999-06-23", "MALE");
        userService.join(request);
        JoinRequest duplicatedRequest = new JoinRequest("hgh1472", "hgh1472@naver.com", "1999-06-23", "MALE");

        assertThatThrownBy(() -> userService.join(duplicatedRequest))
                .isInstanceOf(CoreException.class)
                .hasMessage("이미 가입된 ID입니다.");
    }
}
