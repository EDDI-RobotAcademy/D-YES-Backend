package com.dyes.backend.domain.user.repository;

import com.dyes.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByStringId(String id);
}
