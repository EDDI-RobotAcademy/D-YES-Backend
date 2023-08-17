package com.dyes.backend.domain.user.repository;

import com.dyes.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT u FROM User u WHERE u.id = :id")
    Optional<User> findByStringId(String id);
    @Query("SELECT u FROM User u WHERE u.accessToken = :accessToken")
    Optional<User> findByAccessToken(String accessToken);
}
