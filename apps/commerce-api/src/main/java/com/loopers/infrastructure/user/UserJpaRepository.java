package com.loopers.infrastructure.user;

import com.loopers.domain.user.LoginId;
import com.loopers.domain.user.User;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    Optional<User> findByLoginId(LoginId loginId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from User u where u.loginId = :loginId")
    Optional<User> findByLoginIdWithLock(LoginId loginId);
}
