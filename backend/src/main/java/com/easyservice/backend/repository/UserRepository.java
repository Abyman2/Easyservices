package com.easyservice.backend.repository;

import com.easyservice.backend.model.User;
import java.util.Optional;

public interface UserRepository {

    User save(User user);

    Optional<User> findById(String id);

    Optional<User> findByEmail(String email);

    Optional<User> findByPhone(String phone);

    void clear();
}

