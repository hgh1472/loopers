package com.loopers.infrastructure.user;

import com.loopers.domain.user.LoginId;
import com.loopers.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    boolean existsByLoginId(LoginId loginId);
}
