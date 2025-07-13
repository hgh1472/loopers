package com.loopers.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "users")
public class User extends BaseEntity {

    private String loginId;

    private String email;

    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private Long point;
}
