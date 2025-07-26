package com.loopers.domain.user;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "member")
public class User extends BaseEntity {

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "login_id", nullable = false, unique = true))
    private LoginId loginId;

    @Embedded
    @AttributeOverride(name = "email", column = @Column(name = "email", nullable = false, unique = true))
    private Email email;

    @AttributeOverride(name = "birthDate", column = @Column(name = "birth_date", nullable = false))
    private BirthDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    protected User() {
    }

    private User(LoginId loginId, Email email, BirthDate birthDate, Gender gender) {
        this.loginId = loginId;
        this.email = email;
        this.birthDate = birthDate;
        this.gender = gender;
    }

    public static User create(UserCommand.Join command) {
        LoginId loginId = command.toLoginId();
        Email email = command.toEmail();
        BirthDate birthDate = command.toBirthDate();
        Gender gender = command.toGender();

        return new User(loginId, email, birthDate, gender);
    }

    public enum Gender {
        MALE,
        FEMALE;

        public static Gender from(String gender) {
            for (Gender g : values()) {
                if (g.name().equals(gender)) {
                    return g;
                }
            }
            throw new CoreException(ErrorType.BAD_REQUEST, "성별은 두 가지 중 선택해야 합니다.");
        }
    }
}
