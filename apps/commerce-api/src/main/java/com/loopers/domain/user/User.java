package com.loopers.domain.user;

import com.loopers.application.user.JoinRequest;
import com.loopers.domain.BaseEntity;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
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

    public static User create(JoinRequest request) {
        LoginId loginId = new LoginId(request.loginId());
        Email email = new Email(request.email());
        BirthDate birthDate = new BirthDate(request.birthDate());
        Gender gender = Gender.from(request.gender());

        return new User(loginId, email, birthDate, gender, 0L);
    }
}
