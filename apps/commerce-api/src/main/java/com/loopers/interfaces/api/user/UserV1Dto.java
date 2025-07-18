package com.loopers.interfaces.api.user;

import com.loopers.application.user.UserCriteria;
import com.loopers.application.user.UserInfo;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserV1Dto {
    public record JoinRequest(
            @NotNull(message = "로그인 ID는 필수입니다.")
            @Size(max = 10, message = "ID는 10자 이내이어야 합니다.")
            @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "ID는 영문 및 숫자만 포함할 수 있습니다.")
            String loginId,

            @NotNull(message = "이메일은 필수입니다.")
            @Email(regexp = "^[a-zA-Z0-9]+@[a-zA-Z0-9]+\\.[a-zA-Z0-9]+$", message = "이메일은 xx@yy.zz 형식이어야 합니다.")
            String email,

            @NotNull(message = "생년월일은 필수입니다.")
            @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "생년월일은 YYYY-MM-DD 형식이어야 합니다.")
            String birthDate,

            @NotNull(message = "성별은 필수입니다.")
            String gender
    ) {
        public UserCriteria.Join toCriteria() {
            return new UserCriteria.Join(
                    loginId,
                    email,
                    birthDate,
                    gender
            );
        }
    }

    public record UserResponse(Long id, String loginId, String email, String birthDate, String gender, Long point) {
        public static UserResponse from(UserInfo info) {
            return new UserResponse(
                    info.id(),
                    info.loginId().getId(),
                    info.email().getAddress(),
                    info.birthDate().getDate().toString(),
                    info.gender().name(),
                    info.point()
            );
        }
    }
}
