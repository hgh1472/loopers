package com.loopers.domain.user;

import com.loopers.domain.BaseEntity;
import com.loopers.support.error.CoreException;
import com.loopers.support.error.ErrorType;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "login_id"))
    private LoginId loginId;

    @Embedded
    @AttributeOverride(name = "email", column = @Column(name = "email"))
    private Email email;

    @AttributeOverride(name = "birthDate", column = @Column(name = "birth_date"))
    private BirthDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Long point;

    protected User() {
    }

    private User(LoginId loginId, Email email, BirthDate birthDate, Gender gender, Long point) {
        this.loginId = loginId;
        this.email = email;
        this.birthDate = birthDate;
        this.gender = gender;
        this.point = point;
    }

    public static User create(UserCommand.Join command) {
        LoginId loginId = command.toLoginId();
        Email email = command.toEmail();
        BirthDate birthDate = command.toBirthDate();
        Gender gender = command.toGender();

        return new User(loginId, email, birthDate, gender, 0L);
    }

    public void chargePoint(Long point) {
        if (point <= 0) {
            throw new CoreException(ErrorType.BAD_REQUEST, "0 이하의 정수로 포인트를 충전할 수 없습니다.");
        }
        this.point += point;
    }
}
