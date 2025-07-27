package com.loopers.domain.user;

import java.util.Optional;

public interface UserRepository {
    User save(User user);

    Optional<User> findById(Long userId);

    boolean existsBy(LoginId loginId);
}
